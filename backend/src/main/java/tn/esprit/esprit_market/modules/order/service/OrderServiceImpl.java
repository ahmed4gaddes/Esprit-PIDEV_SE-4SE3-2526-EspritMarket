package tn.esprit.esprit_market.modules.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.*;
import tn.esprit.esprit_market.modules.order.entity.*;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.repository.CartRepository;
import tn.esprit.esprit_market.modules.order.repository.OrderRepository;
import tn.esprit.esprit_market.modules.order.repository.PaymentRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ICartService cartService;

    @Override
    public OrderResponse createOrderFromCart(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("No cart found for user: " + userId));

        if (cart.getItems().isEmpty())
            throw new IllegalStateException("Cannot create order from empty cart");

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem ->
                OrderItem.builder()
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getUnitPrice())
                        .product(cartItem.getProduct())
                        .service(cartItem.getService())
                        .build()
        ).collect(Collectors.toList());

        double total = orderItems.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        Order order = Order.builder()
                .user(user)
                .deliveryAddress(request.getDeliveryAddress())
                .total(total)
                .status(OrderStatus.CREATED)
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        if (request.getPaymentMethod() != null) {
            Payment payment = Payment.builder()
                    .order(savedOrder)
                    .amount(total)
                    .method(request.getPaymentMethod())
                    .paymentDate(new Date())
                    .transactionRef(UUID.randomUUID().toString())
                    .build();
            paymentRepository.save(payment);
            savedOrder.setPayment(payment);
            savedOrder.setStatus(OrderStatus.PAID);
            orderRepository.save(savedOrder);
        }

        cartService.clearCart(userId);

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        order.setStatus(status);
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (!order.getUser().getId().equals(userId))
            throw new IllegalArgumentException("You are not authorized to cancel this order");
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED)
            throw new IllegalStateException("Cannot cancel an order that has been shipped or delivered");
        order.setStatus(OrderStatus.CANCELLED);
        return mapToResponse(orderRepository.save(order));
    }

    private OrderItemResponse mapOrderItemToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getUnitPrice() * item.getQuantity())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .serviceId(item.getService() != null ? item.getService().getId() : null)
                .serviceName(item.getService() != null ? item.getService().getTitle() : null)
                .build();
    }

    private PaymentResponse mapPaymentToResponse(Payment p) {
        if (p == null) return null;
        return PaymentResponse.builder()
                .id(p.getId())
                .amount(p.getAmount())
                .method(p.getMethod())
                .paymentDate(p.getPaymentDate())
                .transactionRef(p.getTransactionRef())
                .orderId(p.getOrder().getId())
                .build();
    }

    private DeliveryResponse mapDeliveryToResponse(Delivery d) {
        if (d == null) return null;
        return DeliveryResponse.builder()
                .id(d.getId())
                .trackingNumber(d.getTrackingNumber())
                .carrier(d.getCarrier())
                .status(d.getStatus())
                .estimatedDeliveryDate(d.getEstimatedDeliveryDate())
                .actualDeliveryDate(d.getActualDeliveryDate())
                .recipientName(d.getRecipientName())
                .recipientPhone(d.getRecipientPhone())
                .deliveryAddress(d.getDeliveryAddress())
                .deliveryNotes(d.getDeliveryNotes())
                .orderId(d.getOrder().getId())
                .build();
    }

    public OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .total(order.getTotal())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .deliveryAddress(order.getDeliveryAddress())
                .userId(order.getUser().getId())
                .items(order.getItems().stream().map(this::mapOrderItemToResponse).collect(Collectors.toList()))
                .payment(mapPaymentToResponse(order.getPayment()))
                .delivery(mapDeliveryToResponse(order.getDelivery()))
                .build();
    }
}