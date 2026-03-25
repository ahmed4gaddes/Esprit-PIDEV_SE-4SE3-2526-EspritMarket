package tn.esprit.esprit_market.modules.event.service;

import tn.esprit.esprit_market.modules.event.dto.LiveSessionRequest;
import tn.esprit.esprit_market.modules.event.dto.LiveSessionResponse;
import tn.esprit.esprit_market.modules.event.enums.LiveSessionStatus;

import java.util.List;

public interface ILiveSessionService {
    LiveSessionResponse createLiveSession(Long creatorId, Long eventId, LiveSessionRequest request);
    List<LiveSessionResponse> getAllLiveSessions();
    List<LiveSessionResponse> getLiveSessionsByEvent(Long eventId);
    List<LiveSessionResponse> getLiveSessionsByStore(Long storeId);
    List<LiveSessionResponse> getLiveSessionsByService(Long serviceId);
    List<LiveSessionResponse> getLiveSessionsByCreator(Long creatorId);
    LiveSessionResponse getLiveSessionById(Long id);
    LiveSessionResponse updateLiveSession(Long id, LiveSessionRequest request);
    LiveSessionResponse updateStatus(Long id, LiveSessionStatus status);
    void deleteLiveSession(Long id);
}
