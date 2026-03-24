package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.RegistrationDTO;
import tn.esprit.esprit_market.modules.service.entity.Registration;
import tn.esprit.esprit_market.modules.service.entity.Workshop;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.RegistrationRepository;
import tn.esprit.esprit_market.modules.service.repository.WorkshopRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceModuleMapper mapper;

    @InjectMocks
    private RegistrationService registrationService;

    private Registration registration;
    private RegistrationDTO registrationDTO;
    private Workshop workshop;
    private User user;

    @BeforeEach
    void setUp() {
        workshop = new Workshop();
        workshop.setId(1L);
        workshop.setCapacity(10);
        workshop.setEnrolledCount(0);

        user = new User();
        user.setId(1L);

        registration = new Registration();
        registration.setId(1L);
        registration.setWorkshop(workshop);
        registration.setUser(user);

        registrationDTO = new RegistrationDTO();
        registrationDTO.setId(1L);
        registrationDTO.setWorkshopId(1L);
        registrationDTO.setUserId(1L);
    }

    @Test
    void getAll_ShouldReturnListOfRegistrationDTOs() {
        when(registrationRepository.findAll()).thenReturn(Arrays.asList(registration));
        when(mapper.toDto(any(Registration.class))).thenReturn(registrationDTO);

        List<RegistrationDTO> result = registrationService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getById_WhenExists_ShouldReturnRegistrationDTO() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(mapper.toDto(any(Registration.class))).thenReturn(registrationDTO);

        RegistrationDTO result = registrationService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void create_WhenWorkshopHasCapacity_ShouldSaveRegistrationAndIncrementCounter() {
        when(workshopRepository.findById(1L)).thenReturn(Optional.of(workshop));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);
        when(mapper.toDto(any(Registration.class))).thenReturn(registrationDTO);

        RegistrationDTO result = registrationService.create(registrationDTO);

        assertNotNull(result);
        assertEquals(1, workshop.getEnrolledCount());
        verify(workshopRepository, times(1)).save(workshop);
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    @Test
    void create_WhenWorkshopIsFull_ShouldThrowIllegalStateException() {
        workshop.setEnrolledCount(10); // Full capacity

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(workshopRepository.findById(1L)).thenReturn(Optional.of(workshop));

        assertThrows(IllegalStateException.class, () -> registrationService.create(registrationDTO));

        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void delete_WhenExists_ShouldDeleteRegistration() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));

        assertDoesNotThrow(() -> registrationService.delete(1L));
        verify(registrationRepository, times(1)).deleteById(1L);
    }
}
