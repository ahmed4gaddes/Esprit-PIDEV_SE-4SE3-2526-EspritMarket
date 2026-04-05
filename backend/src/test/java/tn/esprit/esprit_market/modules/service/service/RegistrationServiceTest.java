package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.service.dto.RegistrationDTO;
import tn.esprit.esprit_market.modules.service.entity.Registration;
import tn.esprit.esprit_market.modules.service.entity.Workshop;
import tn.esprit.esprit_market.modules.service.enums.RegistrationStatus;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.RegistrationRepository;
import tn.esprit.esprit_market.modules.service.repository.WorkshopRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private RegistrationDTO dto;
    private Workshop workshop;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);
        user.setEmail("user@mail.com");

        workshop = new Workshop();
        workshop.setId(5L);
        workshop.setCapacity(10);
        workshop.setEnrolledCount(0);

        registration = new Registration();
        registration.setId(1L);
        registration.setUser(user);
        registration.setWorkshop(workshop);
        registration.setStatus(RegistrationStatus.REGISTERED);

        dto = new RegistrationDTO();
        dto.setId(1L);
        dto.setUserId(10L);
        dto.setWorkshopId(5L);
        dto.setStatus(RegistrationStatus.REGISTERED);
    }

    @Test
    void testGetAll() {
        when(registrationRepository.findAll()).thenReturn(Arrays.asList(registration));
        when(mapper.toDto(registration)).thenReturn(dto);

        List<RegistrationDTO> result = registrationService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void testGetById() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(mapper.toDto(registration)).thenReturn(dto);

        RegistrationDTO result = registrationService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetByUserEmail() {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(registrationRepository.findByUserId(10L)).thenReturn(Arrays.asList(registration));
        when(mapper.toDto(registration)).thenReturn(dto);

        List<RegistrationDTO> result = registrationService.getByUserEmail("user@mail.com");

        assertEquals(1, result.size());
    }

    @Test
    void testCreate() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(workshopRepository.findById(5L)).thenReturn(Optional.of(workshop));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);
        when(mapper.toDto(registration)).thenReturn(dto);

        RegistrationDTO result = registrationService.create(dto);

        assertNotNull(result);
        assertEquals(1, workshop.getEnrolledCount());
        verify(workshopRepository).save(workshop);
        verify(registrationRepository).save(any(Registration.class));
    }

    @Test
    void testUpdateCancel() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        workshop.setEnrolledCount(1);
        
        RegistrationDTO updateDto = new RegistrationDTO();
        updateDto.setStatus(RegistrationStatus.CANCELLED);
        
        when(registrationRepository.save(registration)).thenReturn(registration);
        when(mapper.toDto(registration)).thenReturn(updateDto);

        RegistrationDTO result = registrationService.update(1L, updateDto);

        assertEquals(RegistrationStatus.CANCELLED, result.getStatus());
        assertEquals(0, workshop.getEnrolledCount());
        verify(workshopRepository).save(workshop);
    }

    @Test
    void testDelete() {
        workshop.setEnrolledCount(1);
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        doNothing().when(registrationRepository).deleteById(1L);

        registrationService.delete(1L);

        assertEquals(0, workshop.getEnrolledCount());
        verify(workshopRepository).save(workshop);
        verify(registrationRepository).deleteById(1L);
    }
}
