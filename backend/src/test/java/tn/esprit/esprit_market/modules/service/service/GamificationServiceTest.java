package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.GamificationDTO;
import tn.esprit.esprit_market.modules.service.entity.Gamification;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.GamificationRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationServiceTest {

    @Mock
    private GamificationRepository gamificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceModuleMapper mapper;

    @InjectMocks
    private GamificationService gamificationService;

    private Gamification gamification;
    private GamificationDTO gamificationDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        gamification = new Gamification();
        gamification.setId(1L);
        gamification.setPoints(100);
        gamification.setLevel(2);
        gamification.setUser(user);

        gamificationDTO = new GamificationDTO();
        gamificationDTO.setId(1L);
        gamificationDTO.setPoints(100);
        gamificationDTO.setLevel(2);
        gamificationDTO.setUserId(1L);
    }

    @Test
    void getAll_ShouldReturnListOfGamificationDTOs() {
        when(gamificationRepository.findAll()).thenReturn(Arrays.asList(gamification));
        when(mapper.toDto(any(Gamification.class))).thenReturn(gamificationDTO);

        List<GamificationDTO> result = gamificationService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getById_WhenExists_ShouldReturnGamificationDTO() {
        when(gamificationRepository.findById(1L)).thenReturn(Optional.of(gamification));
        when(mapper.toDto(any(Gamification.class))).thenReturn(gamificationDTO);

        GamificationDTO result = gamificationService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void create_WhenUserExists_ShouldSaveAndReturnGamificationDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gamificationRepository.save(any(Gamification.class))).thenReturn(gamification);
        when(mapper.toDto(any(Gamification.class))).thenReturn(gamificationDTO);

        GamificationDTO result = gamificationService.create(gamificationDTO);

        assertNotNull(result);
        assertEquals(100, result.getPoints());
        verify(gamificationRepository, times(1)).save(any(Gamification.class));
    }

    @Test
    void delete_WhenExists_ShouldDeleteGamification() {
        when(gamificationRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> gamificationService.delete(1L));
        verify(gamificationRepository, times(1)).deleteById(1L);
    }
}
