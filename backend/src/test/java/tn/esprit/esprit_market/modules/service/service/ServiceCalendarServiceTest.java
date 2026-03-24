package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.ServiceCalendarDTO;
import tn.esprit.esprit_market.modules.service.entity.Service;
import tn.esprit.esprit_market.modules.service.entity.ServiceCalendar;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.ServiceCalendarRepository;
import tn.esprit.esprit_market.modules.service.repository.ServiceRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceCalendarServiceTest {

    @Mock
    private ServiceCalendarRepository calendarRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceModuleMapper mapper;

    @InjectMocks
    private ServiceCalendarService calendarService;

    private ServiceCalendar calendar;
    private ServiceCalendarDTO calendarDTO;
    private Service service;

    @BeforeEach
    void setUp() {
        service = new Service();
        service.setId(1L);

        calendar = new ServiceCalendar();
        calendar.setId(1L);
        calendar.setAvailable(true);
        calendar.setService(service);

        calendarDTO = new ServiceCalendarDTO();
        calendarDTO.setId(1L);
        calendarDTO.setAvailable(true);
        calendarDTO.setServiceId(1L);
    }

    @Test
    void getAll_ShouldReturnListOfServiceCalendarDTOs() {
        when(calendarRepository.findAll()).thenReturn(Arrays.asList(calendar));
        when(mapper.toDto(any(ServiceCalendar.class))).thenReturn(calendarDTO);

        List<ServiceCalendarDTO> result = calendarService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getById_WhenExists_ShouldReturnServiceCalendarDTO() {
        when(calendarRepository.findById(1L)).thenReturn(Optional.of(calendar));
        when(mapper.toDto(any(ServiceCalendar.class))).thenReturn(calendarDTO);

        ServiceCalendarDTO result = calendarService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void create_WhenServiceExists_ShouldSaveAndReturnServiceCalendarDTO() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(calendarRepository.save(any(ServiceCalendar.class))).thenReturn(calendar);
        when(mapper.toDto(any(ServiceCalendar.class))).thenReturn(calendarDTO);

        ServiceCalendarDTO result = calendarService.create(calendarDTO);

        assertNotNull(result);
        assertTrue(result.isAvailable());
        verify(calendarRepository, times(1)).save(any(ServiceCalendar.class));
    }

    @Test
    void delete_WhenExists_ShouldDeleteServiceCalendar() {
        when(calendarRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> calendarService.delete(1L));
        verify(calendarRepository, times(1)).deleteById(1L);
    }
}
