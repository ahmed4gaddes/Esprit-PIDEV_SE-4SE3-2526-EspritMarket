import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiService, ProductRecommendation, ChatResponse } from '../../Services/ai.service';

interface Message {
  role: 'user' | 'bot';
  text?: string;
  products?: ProductRecommendation[];
  comparison?: ChatResponse;
  loading?: boolean;
}

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule], // ✅ Add these two
  templateUrl: './chatbot.component.html',
  styleUrl: './chatbot.component.css'
})
export class ChatbotComponent {
  @ViewChild('messagesEnd') messagesEnd!: ElementRef;

  isOpen = false;
  query = '';
  messages: Message[] = [
    {
      role: 'bot',
      text: '👋 Bonjour ! Décris ce que tu cherches (ex: "pc pour étudiant", "smartphone samsung budget 800")'
    }
  ];

  constructor(private aiService: AiService) {}

  toggleChat() {
    this.isOpen = !this.isOpen;
  }

  sendMessage() {
    const q = this.query.trim();
    if (!q) return;

    this.messages.push({ role: 'user', text: q });
    this.query = '';

    const loadingIndex = this.messages.length;
    this.messages.push({ role: 'bot', loading: true });

    this.scrollToBottom();

    this.aiService.chat(q).subscribe({
      next: (res: ChatResponse) => {
        this.messages.splice(loadingIndex, 1);

        if (res.type === 'comparison') {
          this.messages.push({ role: 'bot', comparison: res });
        } else {
          this.messages.push({
            role: 'bot',
            text: `🔍 Profil détecté : ${res.profile?.toUpperCase()}`,
            products: res.products
          });
        }
        this.scrollToBottom();
      },
      error: () => {
        this.messages.splice(loadingIndex, 1);
        this.messages.push({
          role: 'bot',
          text: '❌ Erreur de connexion au serveur IA.'
        });
        this.scrollToBottom();
      }
    });
  }

  onEnter(event: KeyboardEvent) {
    if (event.key === 'Enter') this.sendMessage();
  }

  scrollToBottom() {
    setTimeout(() => {
      this.messagesEnd?.nativeElement?.scrollIntoView({ behavior: 'smooth' });
    }, 100);
  }
}