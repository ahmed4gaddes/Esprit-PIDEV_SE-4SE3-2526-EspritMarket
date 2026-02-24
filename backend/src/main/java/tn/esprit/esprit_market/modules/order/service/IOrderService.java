package tn.esprit.esprit_market.modules.order.service;

import tn.esprit.esprit_market.modules.order.dto.CreateOrderRequest;
import tn.esprit.esprit_market.modules.order.dto.OrderResponse;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrderFromCart(Long userId, CreateOrderRequest request);
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getOrdersByUser(Long userId);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);
    OrderResponse cancelOrder(Long orderId, Long userId);
}