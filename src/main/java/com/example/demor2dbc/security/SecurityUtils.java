package com.example.demor2dbc.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import reactor.core.publisher.Mono;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static Mono<UserDto> getCurrentUser() {
		return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
				.flatMap(authentication -> Mono.justOrEmpty(extractUser(authentication)));
	}

	public static UserDto extractUser(Authentication authentication) {
		
		if (authentication == null) {
			return null;
		}
		if (authentication instanceof JwtAuthenticationToken) {
			Jwt token = ((JwtAuthenticationToken) authentication).getToken();
			Map<String, Object> claims = token.getClaims();
			String sub=(String)claims.get("sub");
			String userName=(String)claims.get("preferred_username");
			String email=(String)claims.get("email");
			return new UserDto(sub, userName, email,((JwtAuthenticationToken) authentication).getAuthorities());
		}

		return null;
	}

	public static List<GrantedAuthority> extractAuthorityFromClaims(Map<String, Object> claims) {
		String scope=(String) claims.get("scope");
		Stream<GrantedAuthority> scopeStream = Arrays.stream(scope.split("\\s+")).map(x->"SCOPE_"+x).map(SimpleGrantedAuthority::new);
		Stream<GrantedAuthority> mapRolesToGrantedAuthorities = mapRolesToGrantedAuthorities(getRolesFromClaims(claims));
		return Stream.concat(mapRolesToGrantedAuthorities,scopeStream).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private static Collection<String> getRolesFromClaims(Map<String, Object> claims) {
		JSONObject realmAccess = (JSONObject) claims.get("realm_access");
		JSONArray roles = (JSONArray) realmAccess.get("roles");
		return roles.stream().map(Object::toString).collect(Collectors.toSet());
	}

	private static Stream<GrantedAuthority> mapRolesToGrantedAuthorities(Collection<String> roles) {
		return roles.stream().filter(role -> role.startsWith("ROLE_")).map(SimpleGrantedAuthority::new);
	}
}
