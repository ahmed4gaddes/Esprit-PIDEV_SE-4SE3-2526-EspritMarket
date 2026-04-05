export interface OrderItem {
  id?: number;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  productId?: number;
  productName?: string;
  productImageUrl?: string;
  serviceId?: number;
  serviceName?: string;
}

export interface Order {
  id?: number;
  createdAt?: string;
  status: 'CREATED' | 'PAID' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  shippingAddress: string;
  paymentMethod: 'CARD' | 'WALLET' | 'STRIPE' | 'CASH';
  totalAmount: number;
  userId?: number;
  userName?: string;
  items: OrderItem[];
  payment?: any;
  delivery?: any;
}

export interface OrderRequest {
  paymentMethod: 'CARD' | 'WALLET' | 'STRIPE' | 'CASH';
  shippingAddress: string;
  recipientName?: string;
  recipientPhone?: string;
  deliveryNotes?: string;
}
