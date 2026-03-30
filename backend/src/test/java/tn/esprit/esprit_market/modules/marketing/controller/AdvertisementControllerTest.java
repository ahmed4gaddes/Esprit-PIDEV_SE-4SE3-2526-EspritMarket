package tn.esprit.esprit_market.modules.marketing.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.marketing.dto.AdvertisementDTO;
import tn.esprit.esprit_market.modules.marketing.service.IAdvertisementService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertisementControllerTest {

    @Mock
    private IAdvertisementService advertisementService;

    @InjectMocks
    private AdvertisementController controller;

    private AdvertisementDTO adDTO;

    @BeforeEach
    void setUp() {
        adDTO = AdvertisementDTO.builder()
                .id(1L)
                .title("Promo Hiver")
                .build();
    }

    @Test
    void testCreateAdvertisement() {
        when(advertisementService.createAdvertisement(any(AdvertisementDTO.class))).thenReturn(adDTO);

        ResponseEntity<AdvertisementDTO> res = controller.createAdvertisement(adDTO);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertNotNull(res.getBody());
    }

    @Test
    void testUpdateAdvertisement() {
        when(advertisementService.updateAdvertisement(eq(1L), any(AdvertisementDTO.class))).thenReturn(adDTO);

        ResponseEntity<AdvertisementDTO> res = controller.updateAdvertisement(1L, adDTO);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testGetAdvertisementById() {
        when(advertisementService.getAdvertisementById(1L)).thenReturn(adDTO);

        ResponseEntity<AdvertisementDTO> res = controller.getAdvertisementById(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1L, res.getBody().getId());
    }

    @Test
    void testGetAllAdvertisements() {
        when(advertisementService.getAllAdvertisements()).thenReturn(Arrays.asList(adDTO));

        ResponseEntity<List<AdvertisementDTO>> res = controller.getAllAdvertisements();

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetAdvertisementsByCampaign() {
        when(advertisementService.getAdvertisementsByCampaign(5L)).thenReturn(Arrays.asList(adDTO));

        ResponseEntity<List<AdvertisementDTO>> res = controller.getAdvertisementsByCampaign(5L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void testDeleteAdvertisement() {
        doNothing().when(advertisementService).deleteAdvertisement(1L);

        ResponseEntity<Void> res = controller.deleteAdvertisement(1L);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(advertisementService).deleteAdvertisement(1L);
    }
}
