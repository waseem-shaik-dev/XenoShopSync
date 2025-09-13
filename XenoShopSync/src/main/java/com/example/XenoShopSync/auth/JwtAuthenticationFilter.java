package com.example.XenoShopSync.auth;


import com.example.XenoShopSync.authService.AppUserDetailsService;
import com.example.XenoShopSync.authService.JwtService;
import com.example.XenoShopSync.entity.User;
import com.example.XenoShopSync.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserDetailsService appUserDetailsService;
    private final UserRepository userRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {


            // If already authenticated, skip parsing
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = null;

            // 1. Try Authorization header first
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }

            // 2. If not found, check cookies
            if (token == null) {
                token = extractJwtFromCookies(request);
            }

            // 3. Validate & authenticate
            if (token != null) {

                var jws = jwtService.parseAndValidate(token);
                Claims claims = jws.getPayload();

                String email = claims.getSubject();
                if (email != null) {
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                    UserPrincipal userPrincipal = new UserPrincipal(user);
                    // Option A: Take authorities from DB (preferred in most real systems)
                    var auth = new UsernamePasswordAuthenticationToken(
                            userPrincipal,
                            null,
                            userPrincipal.getAuthorities()
                    );

                    // Option B: If you must rely on claim "role" instead:
                    // String role = (String) claims.get("role");
                    // var auth = new UsernamePasswordAuthenticationToken(
                    //        userDetails, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    // );

                    SecurityContextHolder.getContext().setAuthentication(auth);


                }
            }

            filterChain.doFilter(request, response);
        }
        catch (AuthenticationException exception){
            log.warn("Authentication failed for request [{}]: {}", request.getRequestURI(), exception.getMessage());
            handlerExceptionResolver.resolveException(request,response,null,exception);
        }
        catch (Exception e){
            log.error("Unexpected error in JWT filter for request [{}]", request.getRequestURI(), e);
            handlerExceptionResolver.resolveException(request,response,null,e);
        }


    }

    private String extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("jwt".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
