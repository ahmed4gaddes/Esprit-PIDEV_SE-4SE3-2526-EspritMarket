package tn.esprit.esprit_market.modules.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.event.dto.ChatBanResponse;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageRequest;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageResponse;
import tn.esprit.esprit_market.modules.event.service.ChatMessageService;
import tn.esprit.esprit_market.modules.event.service.ChatModerationService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/live-sessions")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final ChatModerationService chatModerationService;
    private final IUserService userService;

    @PostMapping("/{liveSessionId}/chat")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long liveSessionId,
            @Valid @RequestBody ChatMessageRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        User user = userService.getUserByEmail(userEmail);

        ChatMessageResponse response = chatMessageService.sendMessage(liveSessionId, user.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{liveSessionId}/chat")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long liveSessionId) {
        List<ChatMessageResponse> responses = chatMessageService.getMessagesByLiveSession(liveSessionId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{liveSessionId}/chat/ban-status")
    public ResponseEntity<ChatBanResponse> getBanStatus(
            @PathVariable Long liveSessionId,
            Authentication authentication) {

        String userEmail = authentication.getName();
        User user = userService.getUserByEmail(userEmail);

        ChatBanResponse status = chatModerationService.getBanStatus(user.getId(), liveSessionId);
        return ResponseEntity.ok(status);
    }
}

