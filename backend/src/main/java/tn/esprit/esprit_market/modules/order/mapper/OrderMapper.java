package tn.esprit.esprit_market.modules.order.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.esprit_market.modules.order.dto.*;
import tn.esprit.esprit_market.modules.order.entity.*;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.service.entity.Service;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public CartItemResponseDTO toCartItemResponseDTO(CartItem item) {
        if (item == null) return null;
        
        CartItemResponseDTO.CartItemResponseDTOBuilder builder = CartItemResponseDTO.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getQuantity() * item.getUnitPrice());

        if (item.getProduct() != null) {
            Product p = item.getProduct();
            builder.productId(p.getId())
                   .productName(p.getName());
        }

        if (item.getService() != null) {
            Service s = item.getService();
            builder.serviceId(s.getId())
                   .serviceName(s.getTitle());
        }

        return builder.build();
    }

    public CartResponseDTO toCartResponseDTO(Cart cart) {
        if (cart == null) return null;
        
        List<CartItemResponseDTO> itemsDto = cart.getItems() != null 
                ? cart.getItems().stream().map(this::toCartItemResponseDTO).collect(Collectors.toList())
                : List.of();
                
        double subtotal = itemsDto.stream().mapToDouble(CartItemResponseDTO::getTotalPrice).sum();
        double deliveryFee = subtotal > 0 ? 7.0 : 0.0; // Simulated delivery fee
        
        return CartResponseDTO.builder()
                .id(cart.getId())
                .createdAt(cart.getCreatedAt())
                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                .items(itemsDto)
                .subtotal(subtotal)
                .deliveryFee(deliveryFee)
                .total(subtotal + deliveryFee)
                .build();
    }

    public OrderItemResponseDTO toOrderItemResponseDTO(OrderItem item) {
        if (item == null) return null;
        
        OrderItemResponseDTO.OrderItemResponseDTOBuilder builder = OrderItemResponseDTO.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getQuantity() * item.getUnitPrice());

        if (item.getProduct() != null) {
            Product p = item.getProduct();
            builder.productId(p.getId())
                   .productName(p.getName());
        }

        if (item.getService() != null) {
            Service s = item.getService();
            builder.serviceId(s.getId())
                   .serviceName(s.getTitle());
        }

        return builder.build();
    }

    public OrderResponseDTO toOrderResponseDTO(Order order) {
        if (order == null) return null;
        
        List<OrderItemResponseDTO> itemsDto = order.getItems() != null 
                ? order.getItems().stream().map(this::toOrderItemResponseDTO).collect(Collectors.toList())
                : List.of();
                
        return OrderResponseDTO.builder()
                .id(order.getId())
                .createdAt(order.getOrderDate())
                .status(order.getStatus())
                .shippingAddress(order.getDeliveryAddress())
                .paymentMethod(order.getPayment() != null ? order.getPayment().getMethod() : null)
                .totalAmount(order.getTotal())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .userName(order.getUser() != null ? order.getUser().getName() : null)
                .items(itemsDto)
                .payment(toPaymentResponseDTO(order.getPayment()))
                .delivery(toDeliveryResponseDTO(order.getDelivery()))
                .build();
    }

    public PaymentResponseDTO toPaymentResponseDTO(Payment payment) {
        if (payment == null) return null;
        
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .paymentDate(payment.getPaymentDate())
                .transactionRef(payment.getTransactionRef())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .build();
    }

    public DeliveryResponseDTO toDeliveryResponseDTO(Delivery delivery) {
        if (delivery == null) return null;
        
        return DeliveryResponseDTO.builder()
                .id(delivery.getId())
                .trackingNumber(delivery.getTrackingNumber())
                .carrier(delivery.getCarrier())
                .status(delivery.getStatus())
                .estimatedDeliveryDate(delivery.getEstimatedDeliveryDate())
                .actualDeliveryDate(delivery.getActualDeliveryDate())
                .recipientName(delivery.getRecipientName())
                .recipientPhone(delivery.getRecipientPhone())
                .deliveryAddress(delivery.getDeliveryAddress())
                .deliveryNotes(delivery.getDeliveryNotes())
                .orderId(delivery.getOrder() != null ? delivery.getOrder().getId() : null)
                .build();
    }
}
