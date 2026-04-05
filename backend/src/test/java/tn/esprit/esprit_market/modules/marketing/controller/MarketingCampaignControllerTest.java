package tn.esprit.esprit_market.modules.marketing.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.marketing.dto.MarketingCampaignDTO;
import tn.esprit.esprit_market.modules.marketing.service.IMarketingCampaignService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketingCampaignControllerTest {

    @Mock
    private IMarketingCampaignService marketingCampaignService;

    @InjectMocks
    private MarketingCampaignController controller;

    private MarketingCampaignDTO dto;

    @BeforeEach
    void setUp() {
        dto = new MarketingCampaignDTO();
        dto.setId(10L);
        dto.setName("Winter Sale");
        dto.setBudget(5000.0);
    }

    @Test
    void testCreateCampaign() {
        when(marketingCampaignService.createCampaign(any(MarketingCampaignDTO.class))).thenReturn(dto);

        ResponseEntity<MarketingCampaignDTO> response = controller.createCampaign(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Winter Sale", response.getBody().getName());
        verify(marketingCampaignService).createCampaign(any(MarketingCampaignDTO.class));
    }

    @Test
    void testUpdateCampaign() {
        when(marketingCampaignService.updateCampaign(eq(10L), any(MarketingCampaignDTO.class))).thenReturn(dto);

        ResponseEntity<MarketingCampaignDTO> response = controller.updateCampaign(10L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Winter Sale", response.getBody().getName());
        verify(marketingCampaignService).updateCampaign(eq(10L), any(MarketingCampaignDTO.class));
    }

    @Test
    void testGetCampaignById() {
        when(marketingCampaignService.getCampaignById(10L)).thenReturn(dto);

        ResponseEntity<MarketingCampaignDTO> response = controller.getCampaignById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
        verify(marketingCampaignService).getCampaignById(10L);
    }

    @Test
    void testGetAllCampaigns() {
        when(marketingCampaignService.getAllCampaigns()).thenReturn(Arrays.asList(dto));

        ResponseEntity<List<MarketingCampaignDTO>> response = controller.getAllCampaigns();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(marketingCampaignService).getAllCampaigns();
    }

    @Test
    void testDeleteCampaign() {
        doNothing().when(marketingCampaignService).deleteCampaign(10L);

        ResponseEntity<Void> response = controller.deleteCampaign(10L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(marketingCampaignService).deleteCampaign(10L);
    }
}
