export interface ChatMessage {
    id?: number;
  senderId?: number;

  content: string;
    senderName?: string;
    senderRole?: string;
    sentAt?: Date;
}

export interface ChatMessageRequest {
    content: string;
}
