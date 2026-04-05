package tn.esprit.esprit_market.modules.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.dto.MarkCourseCompleteRequest;
import tn.esprit.esprit_market.modules.service.dto.UserCourseCompletionDTO;
import tn.esprit.esprit_market.modules.service.service.UserCourseCompletionService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/user-course-completions")
@RequiredArgsConstructor
public class UserCourseCompletionController {

    private final UserCourseCompletionService userCourseCompletionService;
    private final IUserService userService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserCourseCompletionDTO> markComplete(
            @Valid @RequestBody MarkCourseCompleteRequest body,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userCourseCompletionService.markComplete(user.getId(), body.getCourseId()));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserCourseCompletionDTO>> myCompletions(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(userCourseCompletionService.listForUser(user.getId()));
    }
}
