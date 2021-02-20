package com.example.demor2dbc.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
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

import com.example.demor2dbc.PersonService;
import com.example.demor2dbc.dto.input.InPersonDto;
import com.example.demor2dbc.exceptions.ForbiddenAccessException;
import com.example.demor2dbc.exceptions.ResourceNotFoundException;
import com.example.demor2dbc.exceptions.UnAuthorizedAccessException;
import com.example.demor2dbc.mappers.PersonMapper;
import com.example.demor2dbc.security.SecurityUtils;
import com.example.demor2dbc.security.UserDto;
import com.example.demor2dbc.web.dto.response.HrEntityDto;
import com.example.demor2dbc.web.dto.response.HrPersonDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/people")
public class PersonController {

	@Autowired
	PersonService personService;

	@GetMapping("/")
	@ResponseStatus(HttpStatus.OK)
	public Flux<HrPersonDto> index(Authentication authentication, ServerHttpRequest request,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {
		UserDto extractUser = SecurityUtils.extractUser(authentication);
		if (extractUser == null || extractUser.getSub() == null) {
			throw new UnAuthorizedAccessException();
		}
		return personService.readPeople(extractUser.getSub(), PageRequest.of(page, size))
				.map(person -> PersonMapper.INSTANCE.personToHrPersonDto(person))
				.switchIfEmpty(Flux.error(new ResourceNotFoundException()));
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<HrPersonDto> getOne(Authentication authentication, @PathVariable("id") long id) {

		UserDto connectedUser = SecurityUtils.extractUser(authentication);
		if (connectedUser == null || connectedUser.getSub() == null) {
			throw new UnAuthorizedAccessException();
		}
		return personService.findPerson(id).flatMap(person -> {
			return connectedUser.getSub().equals(person.getUserId())
					? Mono.just(PersonMapper.INSTANCE.personToHrPersonDto(person))
					: Mono.error(new ForbiddenAccessException());

		}).switchIfEmpty(Mono.error(new ResourceNotFoundException()));
	}

	@PostMapping("/")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<HrEntityDto> create(@RequestBody Mono<InPersonDto> person) {

		return personService.createPerson(person, SecurityUtils.getCurrentUser())
				.map(wmp -> new HrEntityDto(wmp.getId()));
	}

	@PostMapping("/flux")
	@ResponseStatus(HttpStatus.CREATED)
	public Flux<HrEntityDto> create(@RequestBody Flux<InPersonDto> people) {
		return personService.createPeople(people, SecurityUtils.getCurrentUser())
				.map(wmp -> new HrEntityDto(wmp.getId()));
	}

	@PutMapping("/flux")
	@ResponseStatus(HttpStatus.OK)
	public Flux<HrEntityDto> update(@RequestBody Flux<InPersonDto> people) {
		return personService.updatePeople(people, SecurityUtils.getCurrentUser(), false)
				.map(wmp -> new HrEntityDto(wmp.getId()));
	}

	@PatchMapping("/flux")
	@ResponseStatus(HttpStatus.OK)
	public Flux<HrEntityDto> patch(@RequestBody Flux<InPersonDto> people) {
		return personService.updatePeople(people, SecurityUtils.getCurrentUser(), true)
				.map(wmp -> new HrEntityDto(wmp.getId()));
	}

}