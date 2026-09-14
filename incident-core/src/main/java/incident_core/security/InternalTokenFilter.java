package incident_core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalTokenFilter extends OncePerRequestFilter {

    @Value("${internal.service.token}")
    private String internalServiceToken;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        if (!request.getRequestURI().equals("/api/logs/ingest")) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedToken =
                request.getHeader("X-Internal-Token");

        if (providedToken == null ||
                !providedToken.equals(internalServiceToken)) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}