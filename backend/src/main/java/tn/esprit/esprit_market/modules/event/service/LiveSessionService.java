package tn.esprit.esprit_market.modules.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.LiveSessionRequest;
import tn.esprit.esprit_market.modules.event.dto.LiveSessionResponse;
import tn.esprit.esprit_market.modules.event.entities.Event;
import tn.esprit.esprit_market.modules.event.entities.LiveSession;
import tn.esprit.esprit_market.modules.event.enums.LiveSessionStatus;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.event.repositories.LiveSessionRepository;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.service.IserviceStore;
import tn.esprit.esprit_market.modules.service.service.IServiceService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiveSessionService implements ILiveSessionService {

    private static final String LIVE_SESSION_NOT_FOUND_MSG = "Live session not found with id: ";

    private final LiveSessionRepository liveSessionRepository;
    private final EventRepository eventRepository;
    private final IUserService userService;
    private final IserviceStore iserviceStore;
    private final IServiceService iserviceService;

    // ==================== CREATE ====================
    public LiveSessionResponse createLiveSession(Long creatorId, Long eventId, LiveSessionRequest request) {

        User creator = userService.getUserById(creatorId);

        Event event = null;
        if (eventId != null) {
            event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        }

        Store store = null;
        if (request.getStoreId() != null) {
            store = iserviceStore.getStoreById(request.getStoreId());
        }

        tn.esprit.esprit_market.modules.service.entity.Service service = null;
        if (request.getServiceId() != null) {
            service = iserviceService.getEntityById(request.getServiceId());
        }

        LiveSession liveSession = LiveSession.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .platform(request.getPlatform())
                .link(request.getLink())
                .scheduledAt(request.getScheduledAt())
                .status(LiveSessionStatus.SCHEDULED)
                .event(event)
                .store(store)
                .service(service)
                .creator(creator)
                .build();

        LiveSession savedSession = liveSessionRepository.save(liveSession);
        return mapToResponse(savedSession);
    }

    // ==================== READ ====================
    public List<LiveSessionResponse> getAllLiveSessions() {
        return liveSessionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<LiveSessionResponse> getLiveSessionsByEvent(Long eventId) {
        return liveSessionRepository.findByEventId(eventId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<LiveSessionResponse> getLiveSessionsByStore(Long storeId) {
        return liveSessionRepository.findByStoreId(storeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<LiveSessionResponse> getLiveSessionsByService(Long serviceId) {
        return liveSessionRepository.findByServiceId(serviceId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<LiveSessionResponse> getLiveSessionsByCreator(Long creatorId) {
        return liveSessionRepository.findByCreatorId(creatorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public LiveSessionResponse getLiveSessionById(Long id) {
        LiveSession liveSession = liveSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LIVE_SESSION_NOT_FOUND_MSG + id));
        return mapToResponse(liveSession);
    }

    // ==================== UPDATE ====================
    public LiveSessionResponse updateLiveSession(Long id, LiveSessionRequest request) {
        LiveSession liveSession = liveSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LIVE_SESSION_NOT_FOUND_MSG + id));

        liveSession.setTitle(request.getTitle());
        liveSession.setDescription(request.getDescription());
        liveSession.setPlatform(request.getPlatform());
        liveSession.setLink(request.getLink());
        liveSession.setScheduledAt(request.getScheduledAt());

        if (request.getStoreId() != null) {
            Store store = iserviceStore.getStoreById(request.getStoreId());
            liveSession.setStore(store);
        } else {
            liveSession.setStore(null);
        }

        if (request.getServiceId() != null) {
            tn.esprit.esprit_market.modules.service.entity.Service service = iserviceService.getEntityById(request.getServiceId());
            liveSession.setService(service);
        } else {
            liveSession.setService(null);
        }

        LiveSession updatedSession = liveSessionRepository.save(liveSession);
        return mapToResponse(updatedSession);
    }

    public LiveSessionResponse updateStatus(Long id, LiveSessionStatus status) {
        LiveSession liveSession = liveSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LIVE_SESSION_NOT_FOUND_MSG + id));

        liveSession.setStatus(status);
        if (status == LiveSessionStatus.ENDED || status == LiveSessionStatus.CANCELLED) {
            liveSession.setEndTime(new java.util.Date());
        }

        return mapToResponse(liveSessionRepository.save(liveSession));
    }

    // ==================== DELETE ====================
    public void deleteLiveSession(Long id) {
        LiveSession liveSession = liveSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(LIVE_SESSION_NOT_FOUND_MSG + id));
        liveSessionRepository.delete(liveSession);
    }

    // ==================== MAPPER ====================
    private LiveSessionResponse mapToResponse(LiveSession liveSession) {
        return LiveSessionResponse.builder()
                .id(liveSession.getId())
                .title(liveSession.getTitle())
                .description(liveSession.getDescription())
                .platform(liveSession.getPlatform())
                .status(liveSession.getStatus())
                .link(liveSession.getLink())
                .scheduledAt(liveSession.getScheduledAt())
                .endTime(liveSession.getEndTime())
                .thumbnailUrl(liveSession.getThumbnailUrl())
                .eventId(liveSession.getEvent() != null ? liveSession.getEvent().getId() : null)
                .eventTitle(liveSession.getEvent() != null ? liveSession.getEvent().getTitle() : null)
                .storeId(liveSession.getStore() != null ? liveSession.getStore().getId() : null)
                .storeName(liveSession.getStore() != null ? liveSession.getStore().getName() : null)
                .serviceId(liveSession.getService() != null ? liveSession.getService().getId() : null)
                .serviceTitle(liveSession.getService() != null ? liveSession.getService().getTitle() : null)
                .creatorId(liveSession.getCreator() != null ? liveSession.getCreator().getId() : null)
                .creatorName(liveSession.getCreator() != null ? liveSession.getCreator().getName() : null)
                .build();
    }
}
