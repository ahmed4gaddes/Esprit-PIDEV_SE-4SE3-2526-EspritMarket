package tn.esprit.esprit_market.modules.administration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.administration.dto.CommissionDTO;
import tn.esprit.esprit_market.modules.administration.entity.Commission;
import tn.esprit.esprit_market.modules.administration.mapper.AdministrationMapper;
import tn.esprit.esprit_market.modules.administration.repository.CommissionRepository;
import tn.esprit.esprit_market.modules.administration.service.impl.CommissionServiceImpl;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.StoreRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommissionServiceTest {

    @Mock
    private CommissionRepository commissionRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private AdministrationMapper administrationMapper;

    @InjectMocks
    private CommissionServiceImpl commissionService;

    private Commission commission;
    private CommissionDTO commissionDTO;
    private Store store;

    @BeforeEach
    void setUp() {
        store = Store.builder().id(10L).name("Test Store").build();
        commission = Commission.builder().id(1L).rate(5.0).amount(50.0).store(store).build();
        commissionDTO = CommissionDTO.builder().id(1L).rate(5.0).amount(50.0).storeId(10L).storeName("Test Store").build();
    }

    @Test
    void createCommission_ShouldReturnSavedCommission() {
        when(storeRepository.findById(10L)).thenReturn(Optional.of(store));
        when(administrationMapper.toCommissionEntity(any(), any())).thenReturn(commission);
        when(commissionRepository.save(any())).thenReturn(commission);
        when(administrationMapper.toCommissionDTO(any())).thenReturn(commissionDTO);

        CommissionDTO result = commissionService.createCommission(commissionDTO);

        assertNotNull(result);
        assertEquals(5.0, result.getRate());
        assertEquals(10L, result.getStoreId());
        verify(commissionRepository, times(1)).save(any());
    }

    @Test
    void createCommission_StoreNotFound_ShouldThrowException() {
        when(storeRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commissionService.createCommission(commissionDTO));
        verify(commissionRepository, never()).save(any());
    }

    @Test
    void getCommissionById_ShouldReturnCommission() {
        when(commissionRepository.findById(1L)).thenReturn(Optional.of(commission));
        when(administrationMapper.toCommissionDTO(commission)).thenReturn(commissionDTO);

        CommissionDTO result = commissionService.getCommissionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCommissionByStore_ShouldReturnCommission() {
        when(commissionRepository.findByStore_Id(10L)).thenReturn(Optional.of(commission));
        when(administrationMapper.toCommissionDTO(commission)).thenReturn(commissionDTO);

        CommissionDTO result = commissionService.getCommissionByStore(10L);

        assertNotNull(result);
        assertEquals(10L, result.getStoreId());
    }

    @Test
    void getAllCommissions_ShouldReturnList() {
        when(commissionRepository.findAll()).thenReturn(List.of(commission));
        when(administrationMapper.toCommissionDTO(any())).thenReturn(commissionDTO);

        List<CommissionDTO> list = commissionService.getAllCommissions();

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void deleteCommission_ShouldDeleteWhenExists() {
        when(commissionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(commissionRepository).deleteById(1L);

        assertDoesNotThrow(() -> commissionService.deleteCommission(1L));
        verify(commissionRepository, times(1)).deleteById(1L);
    }
}
