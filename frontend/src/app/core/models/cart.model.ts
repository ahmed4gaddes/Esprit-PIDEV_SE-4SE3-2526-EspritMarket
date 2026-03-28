export interface CartItemRequest {
  productId?: number;
  serviceId?: number;
  quantity: number;
}

export interface CartItemResponse {
  id: number;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  productId?: number;
  productName?: string;
  productImageUrl?: string;
  serviceId?: number;
  serviceName?: string;
}

export interface CartResponse {
  id: number;
  userId: number;
  items: CartItemResponse[];
  subtotal: number;
  deliveryFee: number;
  total: number;
}
