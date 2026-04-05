package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.service.dto.WorkshopDTO;
import tn.esprit.esprit_market.modules.service.entity.Workshop;
import tn.esprit.esprit_market.modules.service.enums.ServiceType;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
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
    private WorkshopDTO dto;
    private User creator;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(10L);

        workshop = new Workshop();
        workshop.setId(5L);
        workshop.setTitle("Java Advanced");
        workshop.setType(ServiceType.WORKSHOP);

        dto = new WorkshopDTO();
        dto.setId(5L);
        dto.setTitle("Java Advanced DTO");
        dto.setCreatorId(10L);
    }

    @Test
    void testGetAll() {
        when(workshopRepository.findAll()).thenReturn(Arrays.asList(workshop));
        when(mapper.toDto(workshop)).thenReturn(dto);

        List<WorkshopDTO> result = workshopService.getAll();

        assertEquals(1, result.size());
        assertEquals("Java Advanced DTO", result.get(0).getTitle());
    }

    @Test
    void testGetById() {
        when(workshopRepository.findById(5L)).thenReturn(Optional.of(workshop));
        when(mapper.toDto(workshop)).thenReturn(dto);

        WorkshopDTO result = workshopService.getById(5L);

        assertNotNull(result);
        assertEquals(5L, result.getId());
    }

    @Test
    void testCreate() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(creator));
        
        doAnswer(invocation -> {
            Workshop w = invocation.getArgument(1);
            w.setTitle("Java Advanced DTO");
            return null;
        }).when(mapper).toEntity(eq(dto), any(Workshop.class));
        
        when(workshopRepository.save(any(Workshop.class))).thenReturn(workshop);
        when(mapper.toDto(workshop)).thenReturn(dto);

        WorkshopDTO result = workshopService.create(dto);

        assertNotNull(result);
        assertEquals(10L, result.getCreatorId());
        verify(workshopRepository).save(any(Workshop.class));
    }

    @Test
    void testDelete() {
        when(workshopRepository.existsById(5L)).thenReturn(true);
        doNothing().when(workshopRepository).deleteById(5L);

        workshopService.delete(5L);

        verify(workshopRepository).deleteById(5L);
    }
}
