package com.example.demor2dbc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demor2dbc.dto.input.InGroupDto;
import com.example.demor2dbc.dto.input.InJobDto;
import com.example.demor2dbc.dto.input.InPersonDto;
import com.example.demor2dbc.dto.input.InTagDto;
import com.example.demor2dbc.dto.input.InTodoDto;
import com.example.demor2dbc.entities.write.WmPerson;
import com.example.demor2dbc.security.UserDto;
import com.example.demor2dbc.statics.GroupName;
import com.example.demor2dbc.statics.JobName;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PopulatorService {
	@Autowired
	private PersonService personService;

	@Transactional
	public Flux<WmPerson> InitData() {
		UserDto user=new UserDto("60fb5cd7-786b-4450-8938-d4ec0e212eb8",null,null,null);
		List<InPersonDto> pple1 = new ArrayList<>();
		InPersonDto p = new InPersonDto();
		pple1.add(p);
		p.setName("josh");
		p.setAddress("Address 1 in paris");
		Set<InJobDto> jobs = new HashSet<>();
		InJobDto job = new InJobDto();
		jobs.add(job);
		job.setName(JobName.ARCHI);
		job.setLocation("Zenica French");
		List<InGroupDto> gps = new ArrayList<>();
		InGroupDto g = new InGroupDto();
		gps.add(g);
		g.setName(GroupName.GROUP1);
		job.setGroups(gps);
		job = new InJobDto();
		jobs.add(job);
		job.setName(JobName.DEV);
		gps = new ArrayList<>();
		g = new InGroupDto();
		gps.add(g);
		g.setName(GroupName.GROUP2);
		job.setGroups(gps);
		p.setJobs(jobs);
		Set<InTodoDto> todos = new HashSet<>();
		InTodoDto todo = new InTodoDto();
		todo.setDescription("Java dev");
		todo.setDetails("coding some class");
		todo.setDone(false);
		Set<InTagDto> tags = new HashSet<>();
		InTagDto e = new InTagDto();
		e.setId(1L);
		tags.add(e);
		e = new InTagDto();
		e.setId(3L);
		tags.add(e);
		todo.setTags(tags);
		todos.add(todo);
		todo = new InTodoDto();
		tags = new HashSet<>();
		e = new InTagDto();
		e.setId(2L);
		tags.add(e);
		todo.setTags(tags);
		todos.add(todo);

		p.setTodos(todos);

		p = new InPersonDto();
		pple1.add(p);
		p.setName("joshwa");
		todos = new HashSet<>();
		todo = new InTodoDto();
		todo.setDescription("Clojure dev");
		todo.setDetails("coding some class");
		todo.setDone(true);
		todos.add(todo);
		todo = new InTodoDto();
		todo.setDescription("Kotlin dev");
		todo.setDetails("best doing with coroutine");
		todo.setDone(true);
		todos.add(todo);
		p.setTodos(todos);
		return personService.createPeople(Flux.fromIterable(pple1),Mono.just(user));
	}
}
