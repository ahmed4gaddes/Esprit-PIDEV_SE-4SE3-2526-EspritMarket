export interface Payment {
  id?: number;
  amount: number;
  method: 'CARD' | 'WALLET' | 'STRIPE' | 'CASH';
  paymentDate?: string;
  transactionRef?: string;
  orderId?: number;
}

export interface PaymentRequest {
  method: 'CARD' | 'WALLET' | 'STRIPE' | 'CASH';
  amount: number;
  transactionRef?: string;
}
