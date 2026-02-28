package tn.esprit.esprit_market.modules.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageRequest;
import tn.esprit.esprit_market.modules.event.dto.ChatMessageResponse;
import tn.esprit.esprit_market.modules.event.entities.ChatMessage;
import tn.esprit.esprit_market.modules.event.entities.LiveSession;
import tn.esprit.esprit_market.modules.event.repositories.ChatMessageRepository;
import tn.esprit.esprit_market.modules.event.repositories.LiveSessionRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final LiveSessionRepository liveSessionRepository;
    private final UserRepository userRepository;

    public ChatMessageResponse sendMessage(Long liveSessionId, Long userId, ChatMessageRequest request) {
        LiveSession liveSession = liveSessionRepository.findById(liveSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Live Session not found with id: " + liveSessionId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        ChatMessage message = ChatMessage.builder()
                .content(request.getContent())
                .liveSession(liveSession)
                .sender(user)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        return mapToResponse(savedMessage);
    }

    public List<ChatMessageResponse> getMessagesByLiveSession(Long liveSessionId) {
        // Optionnel : vérifier si la session existe, mais getAll retournera juste une
        // liste vide si pas de messages
        return chatMessageRepository.findByLiveSessionIdOrderBySentAtAsc(liveSessionId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ChatMessageResponse mapToResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderName(message.getSender() != null ? message.getSender().getName() : "Unknown")
                .senderRole(message.getSender() != null ? message.getSender().getRole().name() : null)
                .sentAt(message.getSentAt())
                .build();
    }
}
