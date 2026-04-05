package tn.esprit.esprit_market.modules.store.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.store.dto.StockMovementDTO;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;
import tn.esprit.esprit_market.modules.store.enums.MovementType;
import tn.esprit.esprit_market.modules.store.mapper.StockMovementMapper;
import tn.esprit.esprit_market.modules.store.service.IStockMovement;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestControllerStockTest {

    @Mock
    private IStockMovement iStockMovement;

    @Mock
    private StockMovementMapper stockMovementMapper;

    @InjectMocks
    private RestControllerStock controller;

    private StockMovement stockMovement;
    private StockMovementDTO stockMovementDTO;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setId(1L);

        stockMovement = new StockMovement();
        stockMovement.setId(1L);
        stockMovement.setQuantity(10);
        stockMovement.setType(MovementType.IN);
        stockMovement.setDate(new Date());
        stockMovement.setProduct(product);

        stockMovementDTO = new StockMovementDTO();
        stockMovementDTO.setId(1L);
        stockMovementDTO.setQuantity(10);
        stockMovementDTO.setType(MovementType.IN);
        stockMovementDTO.setProductId(1L);
    }

    @Test
    void testAddStock() {
        when(iStockMovement.addStock(any(StockMovement.class))).thenReturn(stockMovement);
        when(iStockMovement.getStockById(1L)).thenReturn(stockMovement);
        when(stockMovementMapper.toDTO(stockMovement)).thenReturn(stockMovementDTO);

        StockMovementDTO result = controller.addStock(stockMovementDTO);

        assertNotNull(result);
        assertEquals(10, result.getQuantity());
        verify(iStockMovement).addStock(any(StockMovement.class));
    }

    @Test
    void testUpdateStock() {
        when(iStockMovement.updateStock(any(StockMovement.class), eq(1L))).thenReturn(stockMovement);
        when(iStockMovement.getStockById(1L)).thenReturn(stockMovement);
        when(stockMovementMapper.toDTO(stockMovement)).thenReturn(stockMovementDTO);

        StockMovementDTO result = controller.updateStock(1L, stockMovementDTO);

        assertNotNull(result);
        verify(iStockMovement).updateStock(any(StockMovement.class), eq(1L));
    }

    @Test
    void testGetStockById() {
        when(iStockMovement.getStockById(1L)).thenReturn(stockMovement);
        when(stockMovementMapper.toDTO(stockMovement)).thenReturn(stockMovementDTO);

        StockMovementDTO result = controller.getStockById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetAllStock() {
        when(iStockMovement.getAllStock()).thenReturn(Arrays.asList(stockMovement));
        when(stockMovementMapper.toDTO(stockMovement)).thenReturn(stockMovementDTO);

        List<StockMovementDTO> results = controller.getAllStock();

        assertEquals(1, results.size());
    }

    @Test
    void testDeleteStock() {
        doNothing().when(iStockMovement).deleteStock(1L);

        controller.deleteStock(1L);

        verify(iStockMovement).deleteStock(1L);
    }
}
