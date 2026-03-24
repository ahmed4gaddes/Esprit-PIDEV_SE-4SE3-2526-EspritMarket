package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.GamificationDTO;
import tn.esprit.esprit_market.modules.service.entity.Gamification;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.GamificationRepository;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GamificationService implements IGamificationService {
    private final GamificationRepository gamificationRepository;
    private final UserRepository userRepository;
    private final ServiceModuleMapper mapper;

    @Transactional(readOnly = true)
    public List<GamificationDTO> getAll() {
        return gamificationRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GamificationDTO getById(Long id) {
        Gamification gamification = gamificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gamification not found with id: " + id));
        return mapper.toDto(gamification);
    }

    @Transactional(readOnly = true)
    public GamificationDTO getByUserId(Long userId) {
        Gamification gamification = gamificationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Gamification not found for user id: " + userId));
        return mapper.toDto(gamification);
    }

    public GamificationDTO create(GamificationDTO dto) {
        Gamification gamification = new Gamification();
        mapper.toEntity(dto, gamification);

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));
            gamification.setUser(user);
        }

        return mapper.toDto(gamificationRepository.save(gamification));
    }

    public GamificationDTO update(Long id, GamificationDTO dto) {
        Gamification gamification = gamificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gamification not found with id: " + id));

        mapper.toEntity(dto, gamification);

        if (dto.getUserId() != null
                && (gamification.getUser() == null || !gamification.getUser().getId().equals(dto.getUserId()))) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));
            gamification.setUser(user);
        }

        return mapper.toDto(gamificationRepository.save(gamification));
    }

    public void delete(Long id) {
        if (!gamificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Gamification not found with id: " + id);
        }
        gamificationRepository.deleteById(id);
    }
}