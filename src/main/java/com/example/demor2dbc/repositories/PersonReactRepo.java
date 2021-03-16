package com.example.demor2dbc.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.example.demor2dbc.entities.read.Group;
import com.example.demor2dbc.entities.read.Job;
import com.example.demor2dbc.entities.read.Person;
import com.example.demor2dbc.entities.read.Tag;
import com.example.demor2dbc.entities.read.Todo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonReactRepo extends ReactiveCrudRepository<Person, Long> {
	
	 static final String SELECT_PEOPLE="select distinct p.* "
			+ "from person p "
			+ "left join job j on (p.id=j.developer_id) "
			+ "left join todo td on (p.id=td.person_id)"
			+ "left join groupe g on (j.id=g.job_id) ";
	
	
	@Query(SELECT_PEOPLE
			+ "where 1=1 and p.deleted is not true order by p.id asc limit :limit offset :offset ")
	public Flux<Person> findAllPeople(@Param("limit")int limit,@Param("offset") long offset);
	
	@Query(SELECT_PEOPLE
			+ "where p.user_id=:userId and p.deleted is not true order by p.id asc limit :limit offset :offset ")
	public Flux<Person> findAllPeople(@Param("userId") String userId,@Param("limit")int limit,@Param("offset") long offset);
	
	@Query("select  p.* "
			+ "from person p "
			+ "where p.id=:id and p.deleted is not true")
	public Mono<Person> findPersonById(long id);
	
	@Query("select  p.* "
			+ "from person p "
			+ "where p.id=:id and p.user_id=:userId and p.deleted is not true")
	public Mono<Person> findPersonByIdAndUserId(long id, String userId);
	
	
	@Query("select j.id,j.name,j.location from job j where j.developer_id=:personId and j.deleted is not true")
	 Flux<Job> findJobsByDeveloperId(Long personId);
	 
	 @Query("select t.* from todo t where t.person_id=:personId and t.deleted is not true")
	 Flux<Todo> findTodosByPersonId(Long personId);
	
	@Query("select g.* from groupe g where g.job_id=:jobId and g.deleted is not true")
	public Flux<Group> findGroupsByJobId(Long jobId);
	
	@Query("select distinct tg.* from tag tg "
			+ "inner join todo_tag tt on (tg.id=tt.tag_id) "
			+ "where tg.deleted is not true and tt.todo_id=:todoId and tt.deleted is not true")
	public Flux<Tag> findTagsByTodoId(Long todoId);
	
	
	// update example
//update groupe g set name='Group1' from job j inner join person p on (p.id=j.developer_id) where g.job_id=j.id and  j.name=''  

	
}