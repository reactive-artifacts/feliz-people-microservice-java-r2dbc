package com.example.demor2dbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demor2dbc.dto.http.response.HrEntityDto;
import com.example.demor2dbc.dto.http.response.HrPersonDto;
import com.example.demor2dbc.dto.input.InPersonDto;
import com.example.demor2dbc.entities.read.Person;
import com.example.demor2dbc.exceptions.PersonNotFoundException;
import com.example.demor2dbc.mappers.PersonMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/people")
public class PersonController {
	
	@Autowired
	PersonService personService;
	
	
	@GetMapping("/")
	@ResponseStatus(HttpStatus.OK)
	public Flux<HrPersonDto> index(ServerHttpRequest request, @RequestParam(name = "page",defaultValue = "0") int page,
		      @RequestParam(name = "size", defaultValue = "10") int size) {
     		return personService.readPeople(PageRequest.of(page, size)).
     				map(person->PersonMapper.INSTANCE.personToHrPersonDto(person)).
     				switchIfEmpty(Flux.error(new PersonNotFoundException()));
	}
	
	
	
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<HrPersonDto> one(@PathVariable("id") long id) {
     		
		return personService.findPersonById(id).map(person->PersonMapper.INSTANCE.personToHrPersonDto(person)).
     				switchIfEmpty(Mono.error(new PersonNotFoundException()));
	}
	
	
	
	
	@PostMapping("/")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<HrEntityDto> create(@RequestBody Mono<InPersonDto> person) {
		
		return personService.createPerson(person).map(wmp->new HrEntityDto(wmp.getId()));
	}
	
	@PostMapping("/flux")
	@ResponseStatus(HttpStatus.CREATED)
	public Flux<HrEntityDto> create(@RequestBody Flux<InPersonDto> people) {
		return personService.createPeople(people).map(wmp->new HrEntityDto(wmp.getId()));
	}
	
	@PutMapping("/flux")
	@ResponseStatus(HttpStatus.OK)
	public Flux<HrEntityDto> update(@RequestBody Flux<InPersonDto> people) {
		return personService.updatePeople(people,false).map(wmp->new HrEntityDto(wmp.getId()));
	}
	
	
	@PatchMapping("/flux")
	@ResponseStatus(HttpStatus.OK)
	public Flux<HrEntityDto> patch(@RequestBody Flux<InPersonDto> people) {
		return personService.updatePeople(people,true).map(wmp->new HrEntityDto(wmp.getId()));
	}
	  
    
}