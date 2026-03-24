package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.ServiceDTO;
import tn.esprit.esprit_market.modules.service.entity.Service;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.ServiceRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceModuleMapper mapper;

    @InjectMocks
    private ServiceService serviceService;

    private Service serviceEntity;
    private ServiceDTO serviceDTO;

    @BeforeEach
    void setUp() {
        serviceEntity = new Service();
        serviceEntity.setId(1L);
        serviceEntity.setTitle("Base Service");

        serviceDTO = new ServiceDTO();
        serviceDTO.setId(1L);
        serviceDTO.setTitle("Base Service");
    }

    @Test
    void getAll_ShouldReturnListOfServiceDTOs() {
        when(serviceRepository.findAll()).thenReturn(Arrays.asList(serviceEntity));
        when(mapper.toDto(any(Service.class))).thenReturn(serviceDTO);

        List<ServiceDTO> result = serviceService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getById_WhenExists_ShouldReturnServiceDTO() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        when(mapper.toDto(any(Service.class))).thenReturn(serviceDTO);

        ServiceDTO result = serviceService.getById(1L);

        assertNotNull(result);
        assertEquals("Base Service", result.getTitle());
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        when(serviceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceService.getById(99L));
    }

    @Test
    void update_WhenExists_ShouldUpdateAndReturnServiceDTO() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceEntity));
        when(serviceRepository.save(any(Service.class))).thenReturn(serviceEntity);
        when(mapper.toDto(any(Service.class))).thenReturn(serviceDTO);

        ServiceDTO result = serviceService.update(1L, serviceDTO);

        assertNotNull(result);
        verify(serviceRepository, times(1)).save(any(Service.class));
    }

    @Test
    void delete_WhenExists_ShouldDeleteService() {
        when(serviceRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> serviceService.delete(1L));
        verify(serviceRepository, times(1)).deleteById(1L);
    }
}
