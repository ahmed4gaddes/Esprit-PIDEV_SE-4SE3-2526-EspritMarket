package tn.esprit.esprit_market.modules.store.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit.esprit_market.modules.store.dto.StoreDTO;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.mapper.StoreMapper;
import tn.esprit.esprit_market.modules.store.service.IserviceStore;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestControllerStoreTest {

    @Mock
    private IserviceStore iserviceStore;

    @Mock
    private StoreMapper storeMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private RestControllerStore controller;

    private Store store;
    private StoreDTO storeDTO;
    private final String TEST_EMAIL = "test@mail.com";

    @BeforeEach
    void setUp() {
        store = new Store();
        store.setId(1L);
        store.setName("My Store");

        storeDTO = new StoreDTO();
        storeDTO.setId(1L);
        storeDTO.setName("My Store DTO");

        // Mock Security Context for most of the methods
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testAddStore() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        when(iserviceStore.addStore(store, TEST_EMAIL)).thenReturn(store);
        when(storeMapper.toDTO(store)).thenReturn(storeDTO);

        StoreDTO result = controller.addStore(store);

        assertNotNull(result);
        assertEquals("My Store DTO", result.getName());
        verify(iserviceStore).addStore(store, TEST_EMAIL);
    }

    @Test
    void testGetStoreById() {
        when(iserviceStore.getStoreById(1L)).thenReturn(store);
        when(storeMapper.toDTO(store)).thenReturn(storeDTO);

        StoreDTO result = controller.getStoreById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(iserviceStore).getStoreById(1L);
    }

    @Test
    void testGetAllStores() {
        when(iserviceStore.getAllStores()).thenReturn(Arrays.asList(store));
        when(storeMapper.toDTO(store)).thenReturn(storeDTO);

        List<StoreDTO> results = controller.getAllStores();

        assertEquals(1, results.size());
        verify(iserviceStore).getAllStores();
    }

    @Test
    void testGetMyStores() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        when(iserviceStore.getMyStores(TEST_EMAIL)).thenReturn(Arrays.asList(store));
        when(storeMapper.toDTO(store)).thenReturn(storeDTO);

        List<StoreDTO> results = controller.getMyStores();

        assertEquals(1, results.size());
        verify(iserviceStore).getMyStores(TEST_EMAIL);
    }

    @Test
    void testUpdateStore() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        when(iserviceStore.updateStore(store, TEST_EMAIL)).thenReturn(store);
        when(storeMapper.toDTO(store)).thenReturn(storeDTO);

        StoreDTO result = controller.updateStore(store);

        assertNotNull(result);
        verify(iserviceStore).updateStore(store, TEST_EMAIL);
    }

    @Test
    void testDeleteStore() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        doNothing().when(iserviceStore).deleteStore(1L, TEST_EMAIL);

        controller.deleteStore(1L);

        verify(iserviceStore).deleteStore(1L, TEST_EMAIL);
    }
}
