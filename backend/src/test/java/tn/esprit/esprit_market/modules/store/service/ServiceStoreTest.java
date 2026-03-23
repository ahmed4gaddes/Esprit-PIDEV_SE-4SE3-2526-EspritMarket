package tn.esprit.esprit_market.modules.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryStore;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceStoreTest {

    @Mock
    private IRepositoryStore iRepositoryStore;

    @InjectMocks
    private ServiceStore serviceStore;

    private Store fakeStore;

    @BeforeEach
    void setUp() {
        fakeStore = new Store();
        fakeStore.setId(1L);
        fakeStore.setName("Ma Boutique");
        fakeStore.setDescription("Une super boutique pour les étudiants.");
        fakeStore.setActive(true);
        fakeStore.setCreatedAt(new Date());
    }

    // ==================== addStore ====================
    @Test
    void testAddStore_Success() {
        // ARRANGE
        when(iRepositoryStore.save(fakeStore)).thenReturn(fakeStore);

        // ACT
        Store result = serviceStore.addStore(fakeStore);

        // ASSERT
        assertNotNull(result);
        assertEquals("Ma Boutique", result.getName());
        assertTrue(result.isActive());
        verify(iRepositoryStore, times(1)).save(fakeStore);
    }

    // ==================== getStoreById ====================
    @Test
    void testGetStoreById_Found() {
        // ARRANGE
        when(iRepositoryStore.findById(1L)).thenReturn(Optional.of(fakeStore));

        // ACT
        Store result = serviceStore.getStoreById(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ma Boutique", result.getName());
    }

    @Test
    void testGetStoreById_NotFound_ThrowsException() {
        // ARRANGE
        when(iRepositoryStore.findById(99L)).thenReturn(Optional.empty());

        // ASSERT — On s'attend à une exception si l'ID n'existe pas
        assertThrows(Exception.class, () -> serviceStore.getStoreById(99L));
    }

    // ==================== getAllStores ====================
    @Test
    void testGetAllStores_ReturnsList() {
        // ARRANGE
        Store store2 = new Store();
        store2.setId(2L);
        store2.setName("Boutique 2");
        when(iRepositoryStore.findAll()).thenReturn(Arrays.asList(fakeStore, store2));

        // ACT
        List<Store> result = serviceStore.getAllStores();

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Ma Boutique", result.get(0).getName());
    }

    // ==================== updateStore ====================
    @Test
    void testUpdateStore_Success() {
        // ARRANGE
        fakeStore.setName("Boutique Modifiée");
        when(iRepositoryStore.save(fakeStore)).thenReturn(fakeStore);

        // ACT
        Store result = serviceStore.updateStore(fakeStore);

        // ASSERT
        assertNotNull(result);
        assertEquals("Boutique Modifiée", result.getName());
        verify(iRepositoryStore, times(1)).save(fakeStore);
    }

    // ==================== deleteStore ====================
    @Test
    void testDeleteStore_Success() {
        // ARRANGE
        doNothing().when(iRepositoryStore).deleteById(1L);

        // ACT
        serviceStore.deleteStore(1L);

        // ASSERT
        verify(iRepositoryStore, times(1)).deleteById(1L);
    }
}
