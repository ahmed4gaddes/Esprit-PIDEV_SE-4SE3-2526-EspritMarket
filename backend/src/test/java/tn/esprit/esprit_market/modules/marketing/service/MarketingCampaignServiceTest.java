package tn.esprit.esprit_market.modules.marketing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.marketing.dto.MarketingCampaignDTO;
import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;
import tn.esprit.esprit_market.modules.marketing.mapper.MarketingMapper;
import tn.esprit.esprit_market.modules.marketing.repository.MarketingCampaignRepository;
import tn.esprit.esprit_market.modules.marketing.service.impl.MarketingCampaignServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MarketingCampaignServiceTest {

    @Mock
    private MarketingCampaignRepository repository;

    @Mock
    private MarketingMapper mapper;

    @InjectMocks
    private MarketingCampaignServiceImpl service;

    private MarketingCampaign campaign;
    private MarketingCampaignDTO campaignDTO;

    @BeforeEach
    void setUp() {
        campaign = MarketingCampaign.builder().id(1L).name("Summer Sale").budget(5000).build();
        campaignDTO = MarketingCampaignDTO.builder().id(1L).name("Summer Sale").budget(5000).build();
    }

    @Test
    void createCampaign_ShouldReturnSavedCampaign() {
        when(mapper.toMarketingCampaignEntity(any())).thenReturn(campaign);
        when(repository.save(any())).thenReturn(campaign);
        when(mapper.toMarketingCampaignDTO(any())).thenReturn(campaignDTO);

        MarketingCampaignDTO result = service.createCampaign(campaignDTO);

        assertNotNull(result);
        assertEquals("Summer Sale", result.getName());
        assertEquals(5000, result.getBudget());
        verify(repository, times(1)).save(any());
    }

    @Test
    void getCampaignById_ShouldReturnCampaign() {
        when(repository.findById(1L)).thenReturn(Optional.of(campaign));
        when(mapper.toMarketingCampaignDTO(campaign)).thenReturn(campaignDTO);

        MarketingCampaignDTO result = service.getCampaignById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCampaignById_NotFound_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getCampaignById(1L));
    }

    @Test
    void getAllCampaigns_ShouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(campaign));
        when(mapper.toMarketingCampaignDTO(any())).thenReturn(campaignDTO);

        List<MarketingCampaignDTO> list = service.getAllCampaigns();

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void deleteCampaign_ShouldDeleteWhenExists() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.deleteCampaign(1L));
        verify(repository, times(1)).deleteById(1L);
    }
}
