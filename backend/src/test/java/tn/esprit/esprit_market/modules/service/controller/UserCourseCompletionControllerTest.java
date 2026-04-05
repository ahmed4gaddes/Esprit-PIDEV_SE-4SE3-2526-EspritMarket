package tn.esprit.esprit_market.modules.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import tn.esprit.esprit_market.modules.service.dto.MarkCourseCompleteRequest;
import tn.esprit.esprit_market.modules.service.dto.UserCourseCompletionDTO;
import tn.esprit.esprit_market.modules.service.service.UserCourseCompletionService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCourseCompletionControllerTest {

    @Mock private UserCourseCompletionService userCourseCompletionService;
    @Mock private IUserService userService;
    @Mock private Authentication authentication;
    @InjectMocks private UserCourseCompletionController controller;
    private UserCourseCompletionDTO dto;
    private User user;

    @BeforeEach
    void setUp() {
        dto = new UserCourseCompletionDTO(); dto.setId(1L);
        user = new User(); user.setId(1L); user.setEmail("user@mail.com");
    }

    @Test void testMarkComplete() {
        when(authentication.getName()).thenReturn("user@mail.com");
        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);
        MarkCourseCompleteRequest request = new MarkCourseCompleteRequest();
        request.setCourseId(5L);
        when(userCourseCompletionService.markComplete(1L, 5L)).thenReturn(dto);
        ResponseEntity<UserCourseCompletionDTO> res = controller.markComplete(request, authentication);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testMyCompletions() {
        when(authentication.getName()).thenReturn("user@mail.com");
        when(userService.getUserByEmail("user@mail.com")).thenReturn(user);
        when(userCourseCompletionService.listForUser(1L)).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<UserCourseCompletionDTO>> res = controller.myCompletions(authentication);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }
}
