package tn.esprit.esprit_market.modules.order.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import tn.esprit.esprit_market.modules.order.dto.CartItemRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.CartResponseDTO;
import tn.esprit.esprit_market.modules.order.service.ICartService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private ICartService cartService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CartController controller;

    private CartResponseDTO cartResponse;
    private final String TEST_EMAIL = "user@mail.com";

    @BeforeEach
    void setUp() {
        cartResponse = CartResponseDTO.builder()
                .id(1L)
                .userId(1L)
                .items(new ArrayList<>())
                .subtotal(100.0)
                .deliveryFee(7.0)
                .total(107.0)
                .build();
    }

    @Test
    void testGetCart() {
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        when(cartService.getCart(TEST_EMAIL)).thenReturn(cartResponse);

        ResponseEntity<CartResponseDTO> res = controller.getCart(authentication);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(107.0, res.getBody().getTotal());
    }

    @Test
    void testAddItemToCart() {
        CartItemRequestDTO request = CartItemRequestDTO.builder()
                .productId(1L)
                .quantity(2)
                .build();
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        when(cartService.addItemToCart(eq(TEST_EMAIL), any(CartItemRequestDTO.class))).thenReturn(cartResponse);

        ResponseEntity<CartResponseDTO> res = controller.addItemToCart(request, authentication);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testUpdateItemQuantity() {
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        when(cartService.updateItemQuantity(TEST_EMAIL, 1L, 5)).thenReturn(cartResponse);

        ResponseEntity<CartResponseDTO> res = controller.updateItemQuantity(1L, 5, authentication);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testRemoveItemFromCart() {
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        when(cartService.removeItemFromCart(TEST_EMAIL, 1L)).thenReturn(cartResponse);

        ResponseEntity<CartResponseDTO> res = controller.removeItemFromCart(1L, authentication);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testClearCart() {
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        when(cartService.clearCart(TEST_EMAIL)).thenReturn(cartResponse);

        ResponseEntity<CartResponseDTO> res = controller.clearCart(authentication);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }
}
