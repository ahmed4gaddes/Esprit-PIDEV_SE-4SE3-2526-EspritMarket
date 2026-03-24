package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.InternshipDTO;
import tn.esprit.esprit_market.modules.service.entity.Internship;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.InternshipRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceModuleMapper mapper;

    @InjectMocks
    private InternshipService internshipService;

    private Internship internship;
    private InternshipDTO internshipDTO;
    private User creator;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(1L);

        internship = new Internship();
        internship.setId(1L);
        internship.setTitle("Test Internship");
        internship.setCreator(creator);

        internshipDTO = new InternshipDTO();
        internshipDTO.setId(1L);
        internshipDTO.setTitle("Test Internship");
        internshipDTO.setCreatorId(1L);
    }

    @Test
    void getAll_ShouldReturnListOfInternshipDTOs() {
        when(internshipRepository.findAll()).thenReturn(Arrays.asList(internship));
        when(mapper.toDto(any(Internship.class))).thenReturn(internshipDTO);

        List<InternshipDTO> result = internshipService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(internshipRepository, times(1)).findAll();
    }

    @Test
    void getById_WhenExists_ShouldReturnInternshipDTO() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));
        when(mapper.toDto(any(Internship.class))).thenReturn(internshipDTO);

        InternshipDTO result = internshipService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void create_WhenCreatorExists_ShouldSaveAndReturnInternshipDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(internshipRepository.save(any(Internship.class))).thenReturn(internship);
        when(mapper.toDto(any(Internship.class))).thenReturn(internshipDTO);

        InternshipDTO result = internshipService.create(internshipDTO);

        assertNotNull(result);
        verify(internshipRepository, times(1)).save(any(Internship.class));
    }

    @Test
    void delete_WhenExists_ShouldDeleteInternship() {
        when(internshipRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> internshipService.delete(1L));
        verify(internshipRepository, times(1)).deleteById(1L);
    }
}
