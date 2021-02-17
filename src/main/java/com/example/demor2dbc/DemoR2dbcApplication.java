package com.example.demor2dbc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demor2dbc.entities.write.WmTag;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class DemoR2dbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoR2dbcApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner initDatabase(PopulatorService popService,PersonService personService) {
		return (args)->{
		  
		
			
//		  Flux.just(new WmTag("Science"),new WmTag("Computer"),new WmTag("Lithum"))
//		  .flatMap(e->personService.save(e)).subscribe()
//		  ;
		   
			//popService.InitData().subscribe(x->System.out.println(x));
		  
		
		};
	}
   
}
