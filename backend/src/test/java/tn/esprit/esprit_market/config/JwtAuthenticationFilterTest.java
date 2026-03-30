package tn.esprit.esprit_market.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tn.esprit.esprit_market.modules.auth.util.JwtUtil;
import tn.esprit.esprit_market.modules.user.service.CustomUserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_WhenAuthEndpoint_ShouldSkipJwtProcessing() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(jwtUtil, never()).extractUsername(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_WhenUserNotFound_ShouldClearStaleJwtCookie() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/users/me");
        request.setCookies(new jakarta.servlet.http.Cookie("jwt", "stale-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.extractUsername("stale-token")).thenReturn("ghost@esprit.tn");
        when(userDetailsService.loadUserByUsername("ghost@esprit.tn"))
                .thenThrow(new UsernameNotFoundException("missing"));

        filter.doFilter(request, response, filterChain);

        jakarta.servlet.http.Cookie cookie = response.getCookie("jwt");
        assertNotNull(cookie, "Cookie should not be null");
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertEquals(0, cookie.getMaxAge());
        verify(filterChain).doFilter(request, response);
    }
}
