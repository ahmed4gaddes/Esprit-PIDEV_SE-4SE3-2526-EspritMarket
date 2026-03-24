package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.WorkshopDTO;
import tn.esprit.esprit_market.modules.service.entity.Workshop;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
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
class WorkshopServiceTest {

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceModuleMapper mapper;

    @InjectMocks
    private WorkshopService workshopService;

    private Workshop workshop;
    private WorkshopDTO workshopDTO;
    private User creator;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(1L);

        workshop = new Workshop();
        workshop.setId(1L);
        workshop.setTitle("Test Workshop");
        workshop.setCreator(creator);

        workshopDTO = new WorkshopDTO();
        workshopDTO.setId(1L);
        workshopDTO.setTitle("Test Workshop");
        workshopDTO.setCreatorId(1L);
    }

    @Test
    void getAll_ShouldReturnListOfWorkshopDTOs() {
        when(workshopRepository.findAll()).thenReturn(Arrays.asList(workshop));
        when(mapper.toDto(any(Workshop.class))).thenReturn(workshopDTO);

        List<WorkshopDTO> result = workshopService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Workshop", result.get(0).getTitle());
        verify(workshopRepository, times(1)).findAll();
    }

    @Test
    void getById_WhenExists_ShouldReturnWorkshopDTO() {
        when(workshopRepository.findById(1L)).thenReturn(Optional.of(workshop));
        when(mapper.toDto(any(Workshop.class))).thenReturn(workshopDTO);

        WorkshopDTO result = workshopService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(workshopRepository, times(1)).findById(1L);
    }

    @Test
    void getById_WhenNotExists_ShouldThrowResourceNotFoundException() {
        when(workshopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workshopService.getById(99L));
        verify(workshopRepository, times(1)).findById(99L);
    }

    @Test
    void create_WhenCreatorExists_ShouldSaveAndReturnWorkshopDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(workshopRepository.save(any(Workshop.class))).thenReturn(workshop);
        when(mapper.toDto(any(Workshop.class))).thenReturn(workshopDTO);

        WorkshopDTO result = workshopService.create(workshopDTO);

        assertNotNull(result);
        assertEquals("Test Workshop", result.getTitle());
        verify(userRepository, times(1)).findById(1L);
        verify(workshopRepository, times(1)).save(any(Workshop.class));
    }

    @Test
    void create_WhenCreatorNotExists_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workshopService.create(workshopDTO));
        verify(userRepository, times(1)).findById(1L);
        verify(workshopRepository, never()).save(any(Workshop.class));
    }

    @Test
    void update_WhenExists_ShouldUpdateAndReturnWorkshopDTO() {
        when(workshopRepository.findById(1L)).thenReturn(Optional.of(workshop));
        when(workshopRepository.save(any(Workshop.class))).thenReturn(workshop);
        when(mapper.toDto(any(Workshop.class))).thenReturn(workshopDTO);
        
        // Simulating the user change check in update() method
        // If creatorId matches the existing one, it won't fetch from userRepository

        WorkshopDTO result = workshopService.update(1L, workshopDTO);

        assertNotNull(result);
        verify(workshopRepository, times(1)).save(any(Workshop.class));
    }

    @Test
    void delete_WhenExists_ShouldDeleteWorkshop() {
        when(workshopRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> workshopService.delete(1L));
        verify(workshopRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowResourceNotFoundException() {
        when(workshopRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> workshopService.delete(99L));
        verify(workshopRepository, never()).deleteById(anyLong());
    }
}
