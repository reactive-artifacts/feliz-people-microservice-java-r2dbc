package com.example.demor2dbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.example.demor2dbc.dto.input.InGroupDto;
import com.example.demor2dbc.dto.input.InJobDto;
import com.example.demor2dbc.dto.input.InPersonDto;
import com.example.demor2dbc.dto.input.InTagDto;
import com.example.demor2dbc.dto.input.InTodoDto;
import com.example.demor2dbc.entities.read.Group;
import com.example.demor2dbc.entities.read.Job;
import com.example.demor2dbc.entities.read.Person;
import com.example.demor2dbc.entities.read.Todo;
import com.example.demor2dbc.entities.write.WmEntity;
import com.example.demor2dbc.entities.write.WmGroup;
import com.example.demor2dbc.entities.write.WmJob;
import com.example.demor2dbc.entities.write.WmPerson;
import com.example.demor2dbc.entities.write.WmTodo;
import com.example.demor2dbc.entities.write.WmTodoTag;
import com.example.demor2dbc.exceptions.IllegalAccessOperation;
import com.example.demor2dbc.mappers.JobCloneMapper;
import com.example.demor2dbc.mappers.PersonCloneMapper;
import com.example.demor2dbc.mappers.TodoCloneMapper;
import com.example.demor2dbc.repositories.PersonReactRepo;
import com.example.demor2dbc.security.UserDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@Service
public class PersonService {

	public static final Logger LOG = Loggers.getLogger(PersonService.class);

	@Autowired
	private R2dbcEntityTemplate template;
	@Autowired
	private PersonReactRepo repo;

	@Transactional(readOnly = true)
	public Flux<Person> readPeople(Pageable page) {
		return findPeople(page);
	}

	@Transactional(readOnly = true)
	public Mono<Person> findPerson(long id) {
		return deepMap(repo.findPersonById(id).flux()).singleOrEmpty();
	}
		
	
	@Transactional(readOnly = true)
	public Flux<Person> readPeople(String userId,Pageable page) {
		return deepMap(findPeople(userId,page));
	}


	private Flux<Person> deepMap(Flux<Person> people) {
		Assert.notNull(people, "people mustn't be null");

		return people.concatMap(person -> {
			Mono<Person> map = repo.findJobsByDeveloperId(person.getId()).transform(fjobs -> deepMapJobs(fjobs))
					.collect(Collectors.toSet()).map(x -> {
						Person newPerson = PersonCloneMapper.INSTANCE.clone(person);
						newPerson.setJobs(x);
						return newPerson;
					});
			return map;
		}

		).concatMap(person -> repo.findTodosByPersonId(person.getId()).transform(todos -> deepMapTodos(todos))
				.collect(Collectors.toSet()).map(x -> {
					Person newPerson = PersonCloneMapper.INSTANCE.clone(person);
					newPerson.setTodos(x);
					return newPerson;
				}));
	}

	private Flux<Job> deepMapJobs(Flux<Job> fJobs) {

		return fJobs.flatMap(job -> {
			return repo.findGroupsByJobId(job.getId()).reduce(new ArrayList<Group>(), (acc, val) -> {
				acc.add(val);
				return acc;
			}).map(x -> {
				Job newJob = JobCloneMapper.INSTANCE.clone(job);
				newJob.setGroups(x);
				return newJob;
			});
		});
	}

	private Flux<Todo> deepMapTodos(Flux<Todo> todos) {

		return todos.flatMap(todo -> {
			return repo.findTagsByTodoId(todo.getId()).collect(Collectors.toSet()).map(x -> {
				Todo newTodo = TodoCloneMapper.INSTANCE.clone(todo);
				newTodo.setTags(x);
				;
				return newTodo;
			});
		});
	}

	@Transactional
	public Mono<WmPerson> createPerson(Mono<InPersonDto> person, Mono<UserDto> userDto) {
       
		return createPeople(person.flux(), userDto).singleOrEmpty();
	}

	@Transactional
	public Flux<WmPerson> createPeople(Flux<InPersonDto> people, Mono<UserDto> userDto) {
		Assert.notNull(people, "people mustn't be null");
		Assert.notNull(userDto, "userDto mustn't be null");
		boolean usePatch = false;
		Flux<InPersonDto> newPeople = combinePeopleWithUser(people, userDto);

		return newPeople.flatMap(x -> save(mapToWmPerson(x))
				.flatMapMany(
						wmp -> saveOrUpdateJobs(x.getJobs(), wmp, usePatch).map((WmJob wmj) -> wmp).defaultIfEmpty(wmp))
				.distinct().flatMap(wmp -> saveOrUpdateToDo(x.getTodos(), wmp, usePatch).map((WmTodo wmt) -> wmp)
						.defaultIfEmpty(wmp))
				.distinct());

	}

	@Transactional
	public Flux<WmPerson> updatePeople(Flux<InPersonDto> people, Mono<UserDto> userDto, boolean usePatch) {
		Assert.notNull(people, "people mustn't be null");
		Assert.notNull(userDto, "userDto mustn't be null");
		
		Flux<InPersonDto> newPeople = combinePeopleWithUser(people, userDto);

		Flux<WmPerson> savedPeople = newPeople
				.flatMap(x -> update(mapToWmPerson(x), usePatch, new Fk<String>("user_id", x.getUserId())).flatMapMany(
						wmp -> saveOrUpdateJobs(x.getJobs(), wmp, usePatch).map((WmJob wmj) -> wmp).defaultIfEmpty(wmp)

				).distinct().flatMap(wmp -> saveOrUpdateToDo(x.getTodos(), wmp, usePatch).map((WmTodo wmt) -> wmp)
						.defaultIfEmpty(wmp)

				).distinct());
		return savedPeople;

	}

	private Flux<InPersonDto> combinePeopleWithUser(Flux<InPersonDto> people, Mono<UserDto> userDto) {
		Assert.notNull(people, "people mustn't be null");
		Assert.notNull(userDto, "userDto mustn't be null");
		
		Mono<UserDto> currentUser = userDto.switchIfEmpty(
				Mono.error(new IllegalArgumentException("Cannot attach flux of people to a non existing user")))
				;
        //the order between currentUser et people is very important ...
		Flux<InPersonDto> newPeople = Flux.combineLatest(currentUser, people, (user, inPerson) -> {
			inPerson.setUserId(user.getSub());
			return inPerson;
		});
		return newPeople;
	}

	private Flux<Person> findPeople(Pageable page) {
		return repo.findAllPeople(page.getPageSize(), page.getOffset());
	}
	
	private Flux<Person> findPeople(String userId,Pageable page) {
		return repo.findAllPeople(userId,page.getPageSize(), page.getOffset());
	}

	Flux<WmJob> saveOrUpdateJobs(Set<InJobDto> jobs, WmPerson wmp, boolean usePatch) {
		Mono<Integer> markAsDeleted = Mono.empty();
		Flux<WmJob> fWmJob = Flux.empty();
		if (!usePatch || jobs != null) {
			markAsDeleted = template.update(Query.query(Criteria.where("developer_id").is(wmp.getId())),
					Update.update("deleted", true), WmJob.class);
		}
		if (jobs != null) {
			Function<Integer, Flux<WmJob>> f = i -> Flux.fromIterable(jobs).flatMap(j -> {
				WmJob mapToWmJob = mapToWmJob(j, wmp);
				mapToWmJob.setDeleted(false);
				return saveOrUpdate(mapToWmJob, usePatch, new Fk<Long>("developer_id", wmp.getId()))
						.flatMapMany(wmj -> saveOrUpdateGroups(j.getGroups(), wmj, usePatch).map((WmGroup wmg) -> wmj)
								.defaultIfEmpty(wmj)

						)

						.distinct();
			}

			);
			fWmJob = markAsDeleted.flatMapMany(f);
		} else {
			fWmJob = markAsDeleted.flatMapMany(x -> Flux.empty());
		}
		return fWmJob;
	}

	Flux<WmGroup> saveOrUpdateGroups(List<InGroupDto> groups, WmJob wmj, boolean usePatch) {
		Mono<Integer> markAsDeleted = Mono.empty();
		Flux<WmGroup> fWmGroup = Flux.empty();
		if (!usePatch || groups != null) {
			markAsDeleted = template.update(Query.query(Criteria.where("job_id").is(wmj.getId())),
					Update.update("deleted", true), WmGroup.class);
		}
		if (groups != null) {
			Function<Integer, Flux<WmGroup>> f = i -> Flux.fromIterable(groups).flatMap(gr -> {
				WmGroup mapToWmGroup = mapToWmGroup(gr, wmj);
				mapToWmGroup.setDeleted(false);
				return saveOrUpdate(mapToWmGroup, usePatch, new Fk<Long>("job_id", wmj.getId()));
			});
			fWmGroup = markAsDeleted.flatMapMany(f);
		} else {
			fWmGroup = markAsDeleted.flatMapMany(x -> Flux.empty());
		}
		return fWmGroup;

	}

	Flux<WmTodo> saveOrUpdateToDo(Set<InTodoDto> todos, WmPerson wmp, boolean usePatch) {
		Flux<WmTodo> fWmTodo = Flux.empty();
		Mono<Integer> markAsDeleted = Mono.empty();
		if (!usePatch || todos != null) {
			markAsDeleted = template.update(Query.query(Criteria.where("person_id").is(wmp.getId())),
					Update.update("deleted", true), WmTodo.class);
		}
		if (todos != null) {
			Function<Integer, Flux<WmTodo>> f = i -> Flux.fromIterable(todos)
					.flatMap(t -> saveOrUpdate(mapToWmTodo(t, wmp), usePatch, new Fk<Long>("person_id", wmp.getId()))
							.flatMapMany(wmt -> saveOrUpdateToDoTag(t.getTags(), wmt, usePatch).map(wmtdtg -> wmt)
									.defaultIfEmpty(wmt))
							.distinct());
			fWmTodo = markAsDeleted.flatMapMany(f);
		} else {
			fWmTodo = markAsDeleted.flatMapMany(x -> Flux.empty());
		}
		return fWmTodo;
	}

	Flux<WmTodoTag> saveOrUpdateToDoTag(Set<InTagDto> tags, WmTodo wmt, boolean usePatch) {
		Flux<WmTodoTag> fWmTodoTag = Flux.empty();
		Mono<Integer> markAsDeleted = Mono.empty();
		if (!usePatch || tags != null) {
			markAsDeleted = template.update(Query.query(Criteria.where("todo_id").is(wmt.getId())),
					Update.update("deleted", true), WmTodoTag.class);
		}
		if (tags != null) {
			Function<Integer, Flux<WmTodoTag>> f = i -> Flux.fromIterable(tags)
					.flatMap(tag -> save(mapToWmTodoTag(tag, wmt)));

			fWmTodoTag = markAsDeleted.flatMapMany(f);
		} else {
			fWmTodoTag = markAsDeleted.flatMapMany(x -> Flux.empty());
		}
		return fWmTodoTag;

	}

	WmPerson mapToWmPerson(InPersonDto p) {
		WmPerson person = new WmPerson();
		person.setId(p.getId());
		person.setName(p.getName());
		person.setAddress(p.getAddress());
		person.setUserId(p.getUserId());
		return person;
	}

	WmJob mapToWmJob(InJobDto j, WmPerson p) {
		WmJob wmj = new WmJob();
		wmj.setId(j.getId());
		wmj.setName(j.getName());
		wmj.setPersonId(p.getId());
		wmj.setLocation(j.getLocation());
		return wmj;
	}

	WmGroup mapToWmGroup(InGroupDto g, WmJob j) {
		WmGroup wmg = new WmGroup();
		wmg.setId(g.getId());
		wmg.setName(g.getName());
		wmg.setJobId(j.getId());
		return wmg;
	}

	WmTodo mapToWmTodo(InTodoDto t, WmPerson p) {
		WmTodo wmt = new WmTodo();
		wmt.setId(t.getId());
		wmt.setDescription(t.getDescription());
		wmt.setDetails(t.getDetails());
		wmt.setDone(t.isDone());
		wmt.setPersonId(p.getId());
		return wmt;
	}

	WmTodoTag mapToWmTodoTag(InTagDto tag, WmTodo wmt) {
		return new WmTodoTag(wmt.getId(), tag.getId());
	}

	public <T extends WmEntity> Mono<T> save(T o) {

		return template.insert(o);

	}

	<T extends WmEntity> Mono<T> saveOrUpdate(T o, boolean usePatch, Fk... parentFk) {

		Mono<T> savedOrUpdated = null;
		if (o.getId() == null) {
			savedOrUpdated = save(o);
		} else {
			savedOrUpdated = update(o, usePatch, parentFk);
		}

		return savedOrUpdated;

	}

	<T extends WmEntity> Mono<T> update(T o, boolean usePatch, Fk... parentFk) {
		Mono<T> updated = null;
		if (o.getId() == null) {
			// stop all the world here, dn't encapsulate Exception with Mono.error
			throw new IllegalAccessOperation("When updating entity " + o.getClass() + " has no identity id");
		}

		Criteria criteria = Criteria.where("id").is(o.getId());
		for (Fk fk : parentFk) {
			criteria = criteria.and(fk.getColumnName()).is(fk.getValue());
		}
		updated = template.update(Query.query(criteria), usePatch ? o.toPq() : o.toUq(), o.getClass())
				.flatMap((Integer e) -> {
					if (e == 0) {
						return Mono.error(new IllegalAccessOperation(
								"update operation failed no row matched in data base with object " + o.toString()));
					}
					return Mono.just(o);
				});

		return updated.doOnError(ex -> LOG.error("", ex));

	}

	<T> Flux<T> of(T e) {
		return Flux.just(e);
	}

	private class Fk<T> {
		private String columnName;
		private T value;

		public Fk(String columnName, T value) {
			super();
			this.columnName = columnName;
			this.value = value;
		}

		public String getColumnName() {
			return columnName;
		}

		public T getValue() {
			return value;
		}

	}

}