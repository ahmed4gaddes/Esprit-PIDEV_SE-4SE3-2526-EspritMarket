package tn.esprit.esprit_market.modules.marketing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.marketing.dto.AdvertisementDTO;
import tn.esprit.esprit_market.modules.marketing.entity.Advertisement;
import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;
import tn.esprit.esprit_market.modules.marketing.mapper.MarketingMapper;
import tn.esprit.esprit_market.modules.marketing.repository.AdvertisementRepository;
import tn.esprit.esprit_market.modules.marketing.repository.MarketingCampaignRepository;
import tn.esprit.esprit_market.modules.marketing.service.impl.AdvertisementServiceImpl;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.StoreRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceTest {

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private MarketingCampaignRepository marketingCampaignRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MarketingMapper marketingMapper;

    @InjectMocks
    private AdvertisementServiceImpl advertisementService;

    private Advertisement advertisement;
    private AdvertisementDTO advertisementDTO;
    private MarketingCampaign campaign;
    private Store store;

    @BeforeEach
    void setUp() {
        campaign = MarketingCampaign.builder().id(5L).name("Promo B").build();
        store = Store.builder().id(10L).name("Test Store").build();
        advertisement = Advertisement.builder().id(1L).title("Ad 1").campaign(campaign).stores(Set.of(store)).build();
        advertisementDTO = AdvertisementDTO.builder().id(1L).title("Ad 1").campaignId(5L).storeIds(Set.of(10L)).build();
    }

    @Test
    void createAdvertisement_ShouldReturnSavedAdvertisement() {
        when(marketingCampaignRepository.findById(5L)).thenReturn(Optional.of(campaign));
        when(storeRepository.findAllById(any())).thenReturn(List.of(store));
        when(marketingMapper.toAdvertisementEntity(any(), any(), any())).thenReturn(advertisement);
        when(advertisementRepository.save(any())).thenReturn(advertisement);
        when(marketingMapper.toAdvertisementDTO(any())).thenReturn(advertisementDTO);

        AdvertisementDTO result = advertisementService.createAdvertisement(advertisementDTO);

        assertNotNull(result);
        assertEquals("Ad 1", result.getTitle());
        assertEquals(5L, result.getCampaignId());
        verify(advertisementRepository, times(1)).save(any());
    }

    @Test
    void createAdvertisement_CampaignNotFound_ShouldThrowException() {
        when(marketingCampaignRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> advertisementService.createAdvertisement(advertisementDTO));
        verify(advertisementRepository, never()).save(any());
    }

    @Test
    void getAdvertisementById_ShouldReturnAdvertisement() {
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(advertisement));
        when(marketingMapper.toAdvertisementDTO(advertisement)).thenReturn(advertisementDTO);

        AdvertisementDTO result = advertisementService.getAdvertisementById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAllAdvertisements_ShouldReturnList() {
        when(advertisementRepository.findAll()).thenReturn(List.of(advertisement));
        when(marketingMapper.toAdvertisementDTO(any())).thenReturn(advertisementDTO);

        List<AdvertisementDTO> list = advertisementService.getAllAdvertisements();

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void deleteAdvertisement_ShouldDeleteWhenExists() {
        when(advertisementRepository.existsById(1L)).thenReturn(true);
        doNothing().when(advertisementRepository).deleteById(1L);

        assertDoesNotThrow(() -> advertisementService.deleteAdvertisement(1L));
        verify(advertisementRepository, times(1)).deleteById(1L);
    }
}
