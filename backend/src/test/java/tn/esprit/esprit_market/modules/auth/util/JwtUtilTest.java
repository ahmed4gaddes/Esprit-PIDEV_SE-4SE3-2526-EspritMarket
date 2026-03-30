package tn.esprit.esprit_market.modules.auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Set properties via reflection since no Spring context is running
        ReflectionTestUtils.setField(jwtUtil, "secret", "mySecretKeyForTestingWhichNeedsToBeAtLeast256BitsLong!!");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 3600000L); // 1 hour
    }

    @Test
    void testGenerateAndExtractToken() {
        String token = jwtUtil.generateToken("john_doe", "CUSTOMER");

        assertNotNull(token);

        String username = jwtUtil.extractUsername(token);
        assertEquals("john_doe", username);

        String role = jwtUtil.extractRole(token);
        assertEquals("CUSTOMER", role);
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken("john_doe", "CUSTOMER");
        UserDetails userDetails = new User("john_doe", "pass", new ArrayList<>());

        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void testValidateTokenWrongUser() {
        String token = jwtUtil.generateToken("john_doe", "CUSTOMER");
        UserDetails userDetails = new User("wrong_user", "pass", new ArrayList<>());

        assertFalse(jwtUtil.validateToken(token, userDetails));
    }
}
