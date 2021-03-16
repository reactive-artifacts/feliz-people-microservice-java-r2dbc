package com.example.demor2dbc;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.example.demor2dbc.entities.write.WmTag;
import com.example.demor2dbc.security.JwtGrantedAuthorityConverter;
import com.example.demor2dbc.security.SecurityUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class DemoR2dbcApplication {
	@Value("${spring.security.oauth2.client.provider.oidc.issuer-uri}")
	private String issuerUri;
	
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
	
	
	@Bean
	  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
	    // @formatter:off
	    http
	        .csrf().disable()
	        .httpBasic().disable()
	        .formLogin().disable()
	        .authorizeExchange()
	          .pathMatchers(HttpMethod.OPTIONS).permitAll()
	          .pathMatchers("/actuator/**").permitAll()
	          .pathMatchers("/lorem/**").permitAll()
	         // .pathMatchers("/people/**").hasAuthority("ROLE_USER")
	           .pathMatchers("/people/**").hasAuthority("ROLE_USER")
	          //.pathMatchers("/admin").hasAuthority("ADMIN")
	        .anyExchange().denyAll()
	            ;
	     http.oauth2Login()
        .and()
        .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtAuthenticationConverter());
	    // @formatter:on
	   http.oauth2Client();
	    return http.build();
	  }
	
	Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtGrantedAuthorityConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
	

	
	
	@Bean
	//https://github.com/spring-projects/spring-security/issues/6123
	//Go to line 3282 ServerHttpSecurity
    public ReactiveOAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcReactiveOAuth2UserService delegate = new OidcReactiveOAuth2UserService();

        return (userRequest) -> {
            // Delegate to the default implementation for loading a user
            return delegate.loadUser(userRequest).map(user -> {
                Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

                user.getAuthorities().forEach(authority -> {
                    if (authority instanceof OidcUserAuthority) {
                        OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                        mappedAuthorities.addAll(SecurityUtils.extractAuthorityFromClaims(oidcUserAuthority.getUserInfo().getClaims()));
                    }
                });

                return new DefaultOidcUser(mappedAuthorities, user.getIdToken(), user.getUserInfo());
            });
        };
    }
	
	@Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return ReactiveJwtDecoders.fromOidcIssuerLocation(issuerUri);
    }
   
}
