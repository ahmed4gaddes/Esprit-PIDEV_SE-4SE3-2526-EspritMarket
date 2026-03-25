package tn.esprit.esprit_market.modules.store.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;
import tn.esprit.esprit_market.modules.store.enums.MovementType;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryStockMovement;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

    @Mock
    private IRepositoryStockMovement iRepositoryStockMovement;

    @Mock
    private IRepositoryProduct iRepositoryProduct;

    @InjectMocks
    private StockMovementService stockMovementService;

    private StockMovement fakeMovement;
    private Product fakeProduct;

    @BeforeEach
    void setUp() {
        fakeProduct = new Product();
        fakeProduct.setId(1L);
        fakeProduct.setName("Laptop ESPRIT");

        fakeMovement = new StockMovement();
        fakeMovement.setId(1L);
        fakeMovement.setQuantity(50);
        fakeMovement.setType(MovementType.IN);
        fakeMovement.setDate(new Date());
        fakeMovement.setProduct(fakeProduct);
    }

    // ==================== addStock ====================
    @Test
    void testAddStock_Success() {
        when(iRepositoryProduct.findById(1L)).thenReturn(Optional.of(fakeProduct));
        when(iRepositoryStockMovement.save(any(StockMovement.class))).thenReturn(fakeMovement);

        StockMovement result = stockMovementService.addStock(fakeMovement);

        assertNotNull(result);
        assertEquals(50, result.getQuantity());
        assertEquals(MovementType.IN, result.getType());
        verify(iRepositoryStockMovement, times(1)).save(any(StockMovement.class));
    }

    @Test
    void testAddStock_ProductNotFound_ThrowsException() {
        when(iRepositoryProduct.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stockMovementService.addStock(fakeMovement));
        verify(iRepositoryStockMovement, never()).save(any());
    }

    // ==================== getStockById ====================
    @Test
    void testGetStockById_Found() {
        when(iRepositoryStockMovement.findById(1L)).thenReturn(Optional.of(fakeMovement));

        StockMovement result = stockMovementService.getStockById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(50, result.getQuantity());
    }

    @Test
    void testGetStockById_NotFound_ThrowsException() {
        when(iRepositoryStockMovement.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> stockMovementService.getStockById(99L));
    }

    // ==================== getAllStock ====================
    @Test
    void testGetAllStock_ReturnsList() {
        StockMovement mov2 = new StockMovement();
        mov2.setId(2L);
        mov2.setQuantity(10);
        when(iRepositoryStockMovement.findAll()).thenReturn(Arrays.asList(fakeMovement, mov2));

        List<StockMovement> result = stockMovementService.getAllStock();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ==================== updateStock ====================
    @Test
    void testUpdateStock_Success() {
        StockMovement updatedData = new StockMovement();
        updatedData.setQuantity(100);
        updatedData.setType(MovementType.OUT);
        updatedData.setDate(new Date());
        updatedData.setProduct(fakeProduct);

        when(iRepositoryStockMovement.findById(1L)).thenReturn(Optional.of(fakeMovement));
        when(iRepositoryProduct.findById(1L)).thenReturn(Optional.of(fakeProduct));
        when(iRepositoryStockMovement.save(any(StockMovement.class))).thenReturn(fakeMovement);

        StockMovement result = stockMovementService.updateStock(updatedData, 1L);

        assertNotNull(result);
        verify(iRepositoryStockMovement, times(1)).save(any(StockMovement.class));
    }

    @Test
    void testUpdateStock_NotFound_ThrowsException() {
        when(iRepositoryStockMovement.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stockMovementService.updateStock(new StockMovement(), 99L));
    }

    // ==================== deleteStock ====================
    @Test
    void testDeleteStock_Success() {
        doNothing().when(iRepositoryStockMovement).deleteById(1L);

        stockMovementService.deleteStock(1L);

        verify(iRepositoryStockMovement, times(1)).deleteById(1L);
    }
}
