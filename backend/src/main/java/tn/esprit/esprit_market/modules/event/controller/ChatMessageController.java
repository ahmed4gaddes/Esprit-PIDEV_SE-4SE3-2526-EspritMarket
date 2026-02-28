package tn.esprit.esprit_market.modules.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageRequest;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageResponse;
import tn.esprit.esprit_market.modules.event.service.ChatMessageService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/live-sessions")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    @PostMapping("/{liveSessionId}/chat")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long liveSessionId,
            @Valid @RequestBody ChatMessageRequest request,
            Authentication authentication) {

        // Extract user email from JWT token
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + userEmail));

        ChatMessageResponse response = chatMessageService.sendMessage(liveSessionId, user.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{liveSessionId}/chat")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long liveSessionId) {
        List<ChatMessageResponse> responses = chatMessageService.getMessagesByLiveSession(liveSessionId);
        return ResponseEntity.ok(responses);
    }
}
