package com.example.demor2dbc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demor2dbc.entities.read.Group;
import com.example.demor2dbc.entities.read.Job;
import com.example.demor2dbc.entities.read.Person;
import com.example.demor2dbc.entities.read.Tag;
import com.example.demor2dbc.entities.read.Todo;
import com.example.demor2dbc.entities.write.WmEntity;
import com.example.demor2dbc.entities.write.WmGroup;
import com.example.demor2dbc.entities.write.WmJob;
import com.example.demor2dbc.entities.write.WmPerson;
import com.example.demor2dbc.entities.write.WmTodo;
import com.example.demor2dbc.entities.write.WmTodoTag;
import com.example.demor2dbc.repositories.PersonReactRepo;
import com.example.demor2dbc.statics.GroupName;
import com.example.demor2dbc.statics.JobName;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PopulatorService {
	@Autowired
	private R2dbcEntityTemplate template;
	@Autowired
	private PersonReactRepo repo;

	private Flux<Group> generateGroups() {
		List<Group> groups = new ArrayList<Group>();
		groups.add(new Group(1L, GroupName.GROUP1, new Job(1L)));
		groups.add(new Group(2L, GroupName.GROUP2, new Job(1L)));
		groups.add(new Group(3L, GroupName.GROUP1, new Job(2L)));
		groups.add(new Group(4L, GroupName.GROUP2, new Job(2L)));
		return Flux.fromIterable(groups);
	}

	public Flux<Group> findGroupByJobId(long id) {
		return generateGroups().filter(e -> e.getJob().getId() == id);
	}

	private Flux<Job> generateJobs() {
		List<Job> jobs = new ArrayList<Job>();
		jobs.add(new Job(1L, JobName.ARCHI, 1L));
		jobs.add(new Job(2L, JobName.DEV, 1L));
		jobs.add(new Job(3L, JobName.DEV, 2L));
		jobs.add(new Job(4L, JobName.TESTER, 3L));

		return Flux.fromIterable(jobs);
	}

	private Flux<Job> findJobByDeveloperId(long id) {

		Flux<Job> jobs = generateJobs().filter(j -> j.getDeveloperId() == id);
		return jobs;
	}

	private Flux<Job> graphOfJob(Flux<Job> fJobs) {
		return fJobs.flatMap(job -> {
			System.out.println("Im a in a graph ==" + job);
			return repo.findGroupsByJobId(job.getId()).reduce(new ArrayList<Group>(), (acc, val) -> {
				acc.add(val);
				return acc;
			}).map(x -> {
				job.setGroups(x);
				return job;
			});
		});
	}

	Flux<Person> generatePeople() {
		List<Person> devs = new ArrayList<Person>();
		devs.add(new Person(1L, "josh"));
		devs.add(new Person(2L, "joshwa"));
		devs.add(new Person(3L, "Moha"));
		devs.add(new Person(65L, "Bvo"));
		devs.add(new Person(68L, "Blavvo"));
		return Flux.fromIterable(devs);
	}

	@Transactional(readOnly = true)
	public Flux<Person> populatePeople() {
		// Fait attention if findJobByDeveloperId return empty; penser à
		// .defaultIfEmpty(wmp) after map like ...
		// je pense que flatMapMany c'est qui cause prob je pense c'est qui a besoin de
		// defaultIfEmpty(wmp)
		// preserve order with concatmap flatMap not presrve order because flatMap use
		// concurency
		Flux<Person> devs = findPeople().concatMap(dev -> repo.findJobsByDeveloperId(dev.getId())
				.transform(fjobs -> graphOfJob(fjobs)).collect(Collectors.toSet()).map(x -> {
					dev.setJobs(x);
					return dev;
				})).concatMap(dev -> repo.findTodosByPersonId(dev.getId()).collect(Collectors.toSet()).map(x -> {
					dev.setTodos(x);
					return dev;
				}));
		return devs;
	}

	@Transactional

	public Flux<WmPerson> InitData() {
		List<Person> pple1 = new ArrayList<>();
		Person p = new Person();
		pple1.add(p);
		p.setName("josh");
		p.setAddress("Address in paris");
		Set<Job> jobs = new HashSet<>();
		Job job = new Job();
		jobs.add(job);
		job.setName(JobName.ARCHI);
		job.setLocation("Zenica French");
		List<Group> gps = new ArrayList<>();
		Group g = new Group();
		gps.add(g);
		g.setName(GroupName.GROUP1);
		job.setGroups(gps);
		job = new Job();
		jobs.add(job);
		job.setName(JobName.DEV);
		gps = new ArrayList<>();
		g = new Group();
		gps.add(g);
		g.setName(GroupName.GROUP2);
		job.setGroups(gps);
		p.setJobs(jobs);
		Set<Todo> todos = new HashSet<>();
		Todo todo = new Todo("VB", "xx", false);
		Set<Tag> tags = new HashSet<>();
		tags.add(new Tag(1L));
		tags.add(new Tag(3L));
		todo.setTags(tags);
		todos.add(todo);
		todo = new Todo("JAVA", "ee", true);
		tags = new HashSet<>();
		tags.add(new Tag(2L));
		todo.setTags(tags);
		todos.add(todo);

		p.setTodos(todos);

		p = new Person();
		pple1.add(p);
		p.setName("joshwa");
		todos = new HashSet<>();
		todos.add(new Todo("JH", "kk", false));
		todos.add(new Todo("JOND", "ll", true));
		p.setTodos(todos);
		boolean usePatch=false;
		return Flux.fromIterable(pple1).flatMap(x -> save(mapToWmPerson(x))
				.flatMapMany(wmp -> saveOrUpdateJobs(x.getJobs(), wmp, usePatch).map((WmJob wmj) -> wmp).defaultIfEmpty(wmp)

				).distinct()
				.flatMap(wmp -> saveOrUpdateToDo(x.getTodos(), wmp, usePatch).map((WmTodo wmt) -> wmp).defaultIfEmpty(wmp)
				// dont forget to return wmp

				).distinct());

	}

	
	
	/**
	 * My best tentative ::::  goal :)
	 * Function<List<Long>,Mono<Integer>>fd=ids->template.update(Query.query(Criteria.where("developer_id").
					is(wmp.getId()).and("id").notIn(ids)),
					Update.update("deleted", true), WmJob.class)
					
Flux<WmJob> flatMap = Flux.fromIterable(jobs).flatMap(j -> {
				WmJob mapToWmJob = mapToWmJob(j, wmp);
				mapToWmJob.setDeleted(false);
				return saveOrUpdate(mapToWmJob,usePatch,new Fk("developer_id", wmp.getId()));
			}

			).share();
			 
			 Mono<Integer> updater = flatMap.reduce(new ArrayList<Long>(),(acc,cur)->
			 {acc.add(cur.getId()); return acc;}).flatMap(fd);
			 Mono<List<WmJob>> collectList = flatMap.collectList();
			 Flux<WmJob> flatMapIterable = Flux.zip(updater, collectList).map(tuple->tuple.getT2()).flatMapIterable(s->s)
	 * 
	 */
	
	
	Flux<WmJob> saveOrUpdateJobs(Set<Job> jobs, WmPerson wmp,boolean usePatch) {

			Mono<Integer> deleted = template.update(Query.query(Criteria.where("developer_id").is(wmp.getId())),
					Update.update("deleted", true), WmJob.class);

			Function<Integer, Flux<WmJob>> f = i -> Flux.fromIterable(jobs).flatMap(j -> {
				WmJob mapToWmJob = mapToWmJob(j, wmp);
				mapToWmJob.setDeleted(false);
				return saveOrUpdate(mapToWmJob,usePatch,new Fk("developer_id", wmp.getId())).flatMapMany(
						wmj -> saveOrUpdateGroups(j.getGroups(), wmj,usePatch).map((WmGroup wmg) -> wmj).defaultIfEmpty(wmj)

				)

						.distinct();
			}

			);
			return deleted.flatMapMany(f);
	}

	Flux<WmGroup> saveOrUpdateGroups(List<Group> groups, WmJob wmj,boolean usePatch) {
		Flux<WmGroup> flatMap = Flux.empty();
			Mono<Integer> deleted = template.update(Query.query(Criteria.where("job_id").is(wmj.getId())),
					Update.update("deleted", true), WmGroup.class);

			Function<Integer, Flux<WmGroup>> f = i -> Flux.fromIterable(groups).flatMap(gr -> {
				WmGroup mapToWmGroup = mapToWmGroup(gr, wmj);
				mapToWmGroup.setDeleted(false);
				return saveOrUpdate(mapToWmGroup,usePatch,new Fk("job_id", wmj.getId()));
			});
			flatMap = deleted.flatMapMany(f);
		return flatMap;

	}

	Flux<WmTodo> saveOrUpdateToDo(Set<Todo> todos, WmPerson wmp,boolean usePatch) {
		Flux<WmTodo> fWmTodo = Flux.empty();
			Mono<Integer> deleted = template.update(Query.query(Criteria.where("person_id").is(wmp.getId())),
					Update.update("deleted", true), WmTodo.class);

			Function<Integer, Flux<WmTodo>> f = i -> Flux.fromIterable(todos)
					.flatMap(t -> saveOrUpdate(mapToWmTodo(t, wmp),usePatch,new Fk("person_id", wmp.getId())).flatMapMany(
							wmt -> saveOrUpdateToDoTag(t.getTags(), wmt,usePatch).map(wmtdtg -> wmt).defaultIfEmpty(wmt)))
					.distinct();
			fWmTodo = deleted.flatMapMany(f);
		return fWmTodo;
	}

	
	// Attention cette method dn't use usePatch reellemnt , utilise seulemnt save method
	
	Flux<WmTodoTag> saveOrUpdateToDoTag(Set<Tag> tags, WmTodo wmt,boolean usePatch) {
		Flux<WmTodoTag> fWmTodoTag = Flux.empty();
			Mono<Integer> deleted = template.update(Query.query(Criteria.where("todo_id").is(wmt.getId())),
					Update.update("deleted", true), WmTodoTag.class);

			Function<Integer, Flux<WmTodoTag>> f = i -> Flux.fromIterable(tags)
					.flatMap(tag -> save(mapToWmTodoTag(tag, wmt)));

			fWmTodoTag = deleted.flatMapMany(f);
		
		return fWmTodoTag;

	}


	WmPerson mapToWmPerson(Person p) {
		WmPerson person = new WmPerson();
		person.setId(p.getId());
		person.setName(p.getName());
		person.setAddress(p.getAddress());
		return person;
	}

	WmJob mapToWmJob(Job j, WmPerson p) {
		WmJob wmj = new WmJob();
		wmj.setId(j.getId());
		wmj.setName(j.getName());
		wmj.setPersonId(p.getId());
		wmj.setLocation(j.getLocation());
		return wmj;
	}

	WmGroup mapToWmGroup(Group g, WmJob j) {
		WmGroup wmg = new WmGroup();
		wmg.setId(g.getId());
		wmg.setName(g.getName());
		wmg.setJobId(j.getId());
		return wmg;
	}

	WmTodo mapToWmTodo(Todo t, WmPerson p) {
		WmTodo wmt = new WmTodo();
		wmt.setId(t.getId());
		wmt.setDescription(t.getDescription());
		wmt.setDetails(t.getDetails());
		wmt.setDone(t.isDone());
		wmt.setPersonId(p.getId());
		return wmt;
	}

	WmTodoTag mapToWmTodoTag(Tag tag, WmTodo wmt) {

		return new WmTodoTag(wmt.getId(), tag.getId());
	}

	public <T extends WmEntity> Mono<T> save(T o) {

		return template.insert(o);

	}

	public <T extends WmEntity> Mono<T> saveOrUpdate(T o, boolean usePatch,Fk... parentFk) {

		Mono<T> savedOrUpdated = null;
		if (o.getId() == null) {
			savedOrUpdated = save(o);
		} else {
			savedOrUpdated = update(o,usePatch,parentFk);
		}

		return savedOrUpdated;

	}



	public <T extends WmEntity> Mono<T> update(T o,boolean usePatch,Fk... parentFk) {
		Mono<T> updated = null;
		if (o.getId() == null) {
			throw new IllegalArgumentException("When updating entity " + o.getClass() + " has no identity id");
		}

		Criteria criteria = Criteria.where("id").is(o.getId());
		for (Fk fk : parentFk) {
			criteria = criteria.and(fk.getColumnName()).is(fk.getValue());
		}
		updated = template.update(Query.query(criteria),usePatch?o.toPq():o.toUq(), o.getClass()).map((Integer e) -> {
			if (e == 0) {
				throw new IllegalArgumentException("update operation failed no row matched in data base with object " +o.toString());
			}
			return o;
		});

		return updated;

	}

	<T> Flux<T> of(T e) {
		return Flux.just(e);
	}

	Flux<Person> findPeople() {
		throw new IllegalArgumentException("not  yet implemented");
	}

	@Transactional
	public Flux<WmPerson> updateDb() {
		List<Person> pple1 = new ArrayList<>();
		Person p = new Person();
		p.setId(3L);
		//p.setAddress("A UP Me");
		pple1.add(p);
		p.setName("josh Updated");
		Set<Job> jobs = new HashSet<Job>();
		Job j = new Job(4L);
		//j.setName(JobName.ARCHI);
		//j.setLocation("Cap gemini One O O");
//		List<Group> gps = new ArrayList<>();
//		Group g = new Group();
//		gps.add(g);
//		g.setName(GroupName.GROUP1);
//		j.setGroups(gps);
//		
		jobs.add(j);
//		// without id
//		j = new Job();
//		j.setLocation("new Start up");
//		jobs.add(j);
		p.setJobs(jobs);
//		// add new person to update
//		p = new Person();
//		p.setId(12L);
//		p.setAddress("A UP yes");
//		pple1.add(p);
//		p.setName("josh wa");
		boolean usePatch=false;
		Flux<WmPerson> flatMap = Flux.fromIterable(pple1).flatMap(x -> update(mapToWmPerson(x),usePatch)
				.flatMapMany(wmp -> saveOrUpdateJobs(x.getJobs(), wmp,usePatch).map((WmJob wmj) -> wmp).defaultIfEmpty(wmp)

				).distinct()
				.flatMap(wmp -> saveOrUpdateToDo(x.getTodos(), wmp,usePatch).map((WmTodo wmt) -> wmp).defaultIfEmpty(wmp)
				// dont forget to return wmp

				).distinct());
		return flatMap;

	}
	
	
	
	@Transactional
	public Flux<WmPerson> patchDb() {
		List<Person> pple1 = new ArrayList<>();
		Person p = new Person();
		p.setId(3L);
		//p.setAddress("A UP Me ");
		pple1.add(p);
		//p.setName("josh Updated");
		Set<Job> jobs = new HashSet<Job>();
		Job j = new Job(4L);
		//j.setName(JobName.ARCHI);
		//j.setLocation("Cap gemini One O O");
//		List<Group> gps = new ArrayList<>();
//		Group g = new Group();
//		gps.add(g);
//		g.setName(GroupName.GROUP1);
//		j.setGroups(gps);
//		
		jobs.add(j);
//		// without id
		j = new Job();
		j.setLocation("new Start up");
		jobs.add(j);
		p.setJobs(jobs);
//		// add new person to update
//		p = new Person();
//		p.setId(12L);
//		p.setAddress("A UP yes");
//		pple1.add(p);
//		p.setName("josh wa");
        boolean usePatch=true;
		Flux<WmPerson> flatMap = Flux.fromIterable(pple1).flatMap(x -> update(mapToWmPerson(x),usePatch)
				.flatMapMany(wmp -> saveOrUpdateJobs(x.getJobs(), wmp,usePatch).map((WmJob wmj) -> wmp).defaultIfEmpty(wmp)

				).distinct()
				.flatMap(wmp -> saveOrUpdateToDo(x.getTodos(), wmp,usePatch).map((WmTodo wmt) -> wmp).defaultIfEmpty(wmp)
				// dont forget to return wmp

				).distinct());
		return flatMap;

	}

	class Fk {
		private String columnName;
		private Long value;

		public Fk(String columnName, Long value) {
			super();
			this.columnName = columnName;
			this.value = value;
		}

		public String getColumnName() {
			return columnName;
		}

		public Long getValue() {
			return value;
		}

	}

}
