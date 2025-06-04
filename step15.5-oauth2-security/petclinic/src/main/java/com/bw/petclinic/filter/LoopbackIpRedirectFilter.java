package com.bw.petclinic.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ForwardedHeaderUtils;
import org.springframework.web.util.UriComponents;

import java.io.IOException;

/**
 * This filter ensures that the loopback IP <code>127.0.0.1</code> is used to access the
 * application so that the localhost works correctly, due to the fact that redirect URIs with
 * "localhost" are rejected by the Spring Authorization Server, because the OAuth 2.1
 * draft specification. Without this filter, we will get 'authorization_request_not_found'
 * error message after successful login.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoopbackIpRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getServerName().equals("localhost") && request.getHeader("host") != null) {
            ServletServerHttpRequest httpRequest = new ServletServerHttpRequest(request);
            UriComponents uri = ForwardedHeaderUtils
                    .adaptFromForwardedHeaders(httpRequest.getURI(), httpRequest.getHeaders())
                    .host("127.0.0.1")
                    .build();
            response.sendRedirect(uri.toUriString());
            return;
        }
        filterChain.doFilter(request, response);
    }
}
