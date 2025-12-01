package project.tracker.Tracking.system.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import project.tracker.Tracking.system.Service.CustomUserDetailsService;
import project.tracker.Tracking.system.util.JwtUtils;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Get the Authorization Header from the request
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String username = null;

            // 2. Check if the header starts with "Bearer "
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7); // Remove "Bearer " prefix

                // 3. SAFE CHECK: Try to extract username.
                // We wrap this in a try-catch so if the token is garbage, we don't crash.
                try {
                    username = jwtUtils.getUserNameFromJwtToken(token);
                } catch (Exception e) {
                    // Just log it and continue. The request will proceed as "Unauthenticated".
                    logger.warn("Invalid JWT Token in header: " + e.getMessage());
                }
            }

            // 4. If we found a username and the user is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Validate the token
                if (jwtUtils.validateJwtToken(token)) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 6. Set the authentication in the Security Context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Catch any other unexpected errors to prevent 500/403 crashes
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        // 7. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}