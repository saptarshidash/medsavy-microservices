package com.medsavy.authservice.configs;

import com.medsavy.authservice.calls.UserDto;
import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter {

  @Autowired
  private RouterValidator routerValidator;//custom route validator
  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private MyUserDetailService userDetailService;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    if (routerValidator.isSecured.test(request)) {
      if (this.isAuthMissing(request))
        return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);


      final String headerAuth = this.getAuthHeader(request);
      String token = headerAuth.substring(7);
      String username = jwtUtil.extractUsername(token);
      Claims claims = jwtUtil.extractAllClaims(token);
      String userRole =(String) claims.get("role");

      // make call to get user role from db
      UserDto userDto = userDetailService.fetchUserData(username);
      if(!userDto.getUsername().equals(username)) {
        return this.onError(exchange, "User not allowed to access the api", HttpStatus.UNAUTHORIZED);
      }


      if (!jwtUtil.validateToken(token, userDto))
        return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);

      this.populateRequestWithHeaders(exchange, token);
    }
    return chain.filter(exchange);
  }


  /*PRIVATE*/

  private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(httpStatus);
    return response.setComplete();
  }

  private String getAuthHeader(ServerHttpRequest request) {
    return request.getHeaders().getOrEmpty("Authorization").get(0);
  }

  private boolean isAuthMissing(ServerHttpRequest request) {
    return !request.getHeaders().containsKey("Authorization");
  }

  private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
    Claims claims = jwtUtil.extractAllClaims(token);
    exchange.getRequest().mutate()
        .header("id", String.valueOf(claims.get("id")))
        .header("role", String.valueOf(claims.get("role")))
        .build();
  }

}


