package tn.esprit.esprit_market.modules.order.service;

import tn.esprit.esprit_market.modules.order.dto.OrderRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.OrderResponseDTO;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;

import java.util.List;

public interface IOrderService {
    OrderResponseDTO createOrderFromCart(String userEmail, OrderRequestDTO request);
    OrderResponseDTO getOrderById(Long orderId, String userEmail);
    List<OrderResponseDTO> getUserOrders(String userEmail);
    List<OrderResponseDTO> getAllOrders();
    OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status);
    void cancelOrder(Long orderId, String userEmail);
}
