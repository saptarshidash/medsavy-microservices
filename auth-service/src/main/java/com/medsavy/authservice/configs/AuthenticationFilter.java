//package com.medsavy.authservice.configs;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.medsavy.authservice.calls.FilterResponse;
//import com.medsavy.authservice.calls.UserDto;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.List;
//import lombok.Data;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@RefreshScope
//@Component
//@Slf4j
//public class AuthenticationFilter implements GatewayFilter {
//
//  @Autowired
//  private RouterValidator routerValidator;//custom route validator
//  @Autowired
//  private JwtUtil jwtUtil;
//
//  @Autowired
//  private MyUserDetailService userDetailService;
//
//
//  @Override
//  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//    ServerHttpRequest request = exchange.getRequest();
//    ServerHttpResponse response = exchange.getResponse();
//    HttpHeaders headers = request.getHeaders();
//    ObjectMapper mapper = new ObjectMapper();
//    FilterResponse filterResponse = new FilterResponse();
//
//    List<String> user = headers.get("user");
//
//
//    if (routerValidator.isSecured.test(request)) {
//      if (this.isAuthMissing(request))
//        return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);
//
//
//      // IS HEADER MISSING
//      if(user == null || user.size() == 0) {
//        mapper = new ObjectMapper();
//        filterResponse.setMessage("Header user is missing");
//        filterResponse.setSuccess(false);
//        String json = null;
//        try {
//          json = mapper.writeValueAsString(filterResponse);
//        } catch (JsonProcessingException e) {
//          e.printStackTrace();
//        }
//        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
//        DataBuffer buffer = response.bufferFactory().wrap(bytes);
//        return response.writeWith(Mono.just(buffer));
//      }
//
//      final String headerAuth = this.getAuthHeader(request);
//      String token = headerAuth.substring(7);
//      String tokenUser = "";
//
//      try {
//         tokenUser = jwtUtil.extractUsername(token);
//         Claims claims = jwtUtil.extractAllClaims(token);
//         String userRole =(String) claims.get("role");
//
//        // make call to get user role from db
//
//        UserDto userDto = userDetailService.fetchUserData(user.get(0));
//        if(userDto == null) {
//          mapper = new ObjectMapper();
//          filterResponse.setMessage("User not found. Access denied");
//          filterResponse.setSuccess(false);
//          String json = mapper.writeValueAsString(filterResponse);
//          byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
//          DataBuffer buffer = response.bufferFactory().wrap(bytes);
//          return response.writeWith(Mono.just(buffer));
//        }
//        if(!userDto.getUsername().equals(tokenUser)) {
//          mapper = new ObjectMapper();
//          filterResponse.setMessage("Access denied");
//          filterResponse.setSuccess(false);
//          String json = mapper.writeValueAsString(filterResponse);
//          byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
//          DataBuffer buffer = response.bufferFactory().wrap(bytes);
//          return response.writeWith(Mono.just(buffer));
//        }
//
//      } catch (ExpiredJwtException | JsonProcessingException e) {
//        filterResponse.setMessage(e.getMessage());
//        filterResponse.setSuccess(false);
//        try {
//          String jsonResponse = mapper.writeValueAsString(filterResponse);
//          byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
//          DataBuffer buffer = response.bufferFactory().wrap(bytes);
//          return response.writeWith(Mono.just(buffer));
//        } catch (JsonProcessingException ex) {
//          ex.printStackTrace();
//        }
//
//      }
//
//
//
//      this.populateRequestWithHeaders(exchange, token);
//    }
//    return chain.filter(exchange);
//  }
//
//
//
//  /*PRIVATE*/
//
//  private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
//    log.info("Erro that got {}", err);
//    ServerHttpResponse response = exchange.getResponse();
//    response.setStatusCode(httpStatus);
//    return response.setComplete();
//  }
//
//  private String getAuthHeader(ServerHttpRequest request) {
//    return request.getHeaders().getOrEmpty("Authorization").get(0);
//  }
//
//  private boolean isAuthMissing(ServerHttpRequest request) {
//    return !request.getHeaders().containsKey("Authorization");
//  }
//
//  private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
//    Claims claims = jwtUtil.extractAllClaims(token);
//    exchange.getRequest().mutate()
//        .header("id", String.valueOf(claims.get("id")))
//        .header("role", String.valueOf(claims.get("role")))
//        .build();
//  }
//
//}
//
//
