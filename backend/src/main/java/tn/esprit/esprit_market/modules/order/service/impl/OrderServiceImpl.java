package tn.esprit.esprit_market.modules.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.order.dto.OrderRequestDTO;
import tn.esprit.esprit_market.modules.order.dto.OrderResponseDTO;
import tn.esprit.esprit_market.modules.order.entity.Cart;
import tn.esprit.esprit_market.modules.order.entity.CartItem;
import tn.esprit.esprit_market.modules.order.entity.Order;
import tn.esprit.esprit_market.modules.order.entity.OrderItem;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;
import tn.esprit.esprit_market.modules.order.mapper.OrderMapper;
import tn.esprit.esprit_market.modules.order.repository.CartRepository;
import tn.esprit.esprit_market.modules.order.repository.OrderItemRepository;
import tn.esprit.esprit_market.modules.order.repository.OrderRepository;
import tn.esprit.esprit_market.modules.order.service.IOrderService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final tn.esprit.esprit_market.modules.store.service.IStockMovement stockMovementService;
    private final tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct productRepository;
    private final OrderMapper orderMapper;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    @Transactional
    public OrderResponseDTO createOrderFromCart(String userEmail, OrderRequestDTO request) {
        User user = getUser(userEmail);
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order from an empty cart");
        }

        double subtotal = cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
        double deliveryFee = subtotal > 0 ? 7.0 : 0.0;
        double totalAmount = subtotal + deliveryFee;

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .deliveryAddress(request.getShippingAddress())
                .total(totalAmount)
                .build();

        final Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .product(cartItem.getProduct())
                    .service(cartItem.getService())
                    .build();
            
            // Stock Management for Products
            if (cartItem.getProduct() != null) {
                tn.esprit.esprit_market.modules.store.entity.Product product = cartItem.getProduct();
                if (product.getStock() < cartItem.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
                }
                
                // Decrement stock in entity
                product.setStock(product.getStock() - cartItem.getQuantity());
                productRepository.save(product);
                
                // Create StockMovement record
                tn.esprit.esprit_market.modules.store.entity.StockMovement movement =
                    tn.esprit.esprit_market.modules.store.entity.StockMovement.builder()
                        .product(product)
                        .quantity(cartItem.getQuantity())
                        .type(tn.esprit.esprit_market.modules.store.enums.MovementType.OUT)
                        .date(new java.util.Date())
                        .reason("Order #" + savedOrder.getId())
                        .build();
                stockMovementService.addStock(movement);
            }
            
            return orderItemRepository.save(orderItem);
        }).collect(Collectors.toList());

        savedOrder.setItems(orderItems);

        // Clear cart after creating order
        cartRepository.delete(cart);

        return orderMapper.toOrderResponseDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long orderId, String userEmail) {
        User user = getUser(userEmail);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
                
        // Only allow user to see their own orders, unless they are admin (role check can be added here)
        if (!order.getUser().getId().equals(user.getId()) && user.getRole().name().equals("CUSTOMER")) {
            throw new IllegalArgumentException("Not authorized to view this order");
        }
        
        return orderMapper.toOrderResponseDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getUserOrders(String userEmail) {
        User user = getUser(userEmail);
        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId())
                .stream().map(orderMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream().map(orderMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        order.setStatus(status);
        return orderMapper.toOrderResponseDTO(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String userEmail) {
        User user = getUser(userEmail);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
                
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not authorized to cancel this order");
        }
        
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalArgumentException("Cannot cancel an order that has been shipped or delivered");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
