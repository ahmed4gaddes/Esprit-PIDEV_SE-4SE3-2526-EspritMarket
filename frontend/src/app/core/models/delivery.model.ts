export interface Delivery {
  id?: number;
  trackingNumber?: string;
  carrier?: string;
  status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED' | 'FAILED' | 'RETURNED';
  estimatedDeliveryDate?: string;
  actualDeliveryDate?: string;
  recipientName?: string;
  recipientPhone?: string;
  deliveryAddress: string;
  deliveryNotes?: string;
  orderId?: number;
}

export interface DeliveryRequest {
  deliveryAddress: string;
  recipientName?: string;
  recipientPhone?: string;
  deliveryNotes?: string;
}
