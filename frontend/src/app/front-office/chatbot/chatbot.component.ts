import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiService, ProductRecommendation, ChatResponse, BundleProduct } from '../../Services/ai.service';
import { CartService } from '../../core/services/cart.service';

interface Message {
  role:          'user' | 'bot';
  text?:         string;
  products?:     ProductRecommendation[];
  comparison?:   ChatResponse;
  bundle?:       { productName: string; bundles: BundleProduct[] }; // ✅ bundle
  loading?:      boolean;
  notFound?:     boolean;
  brandNotFound?: boolean;
}

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
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
      text: '👋 Bienvenue sur EspritMarket !\n\nJe suis votre assistant IA personnel 🤖, spécialisé dans :\n\n💻 Ordinateurs & Laptops\n📱 Smartphones & Tablettes\n\nDites-moi votre besoin et votre budget, je vous recommande le meilleur choix.\n\n📌 Exemples :\n"laptop étudiant budget 1200"\n"meilleur smartphone 2025 moins de 1500 TND"\n"compare Samsung et iPhone"\n\n✍️ Comment puis-je vous aider ?'
    }
  ];

  constructor(
    private aiService: AiService,
    private cartService: CartService
  ) {}

  addToCart(p: ProductRecommendation | undefined) {
    if (!p) return;
    if (!p.id) {
      alert("Erreur: ID du produit manquant. Veuillez redémarrer le backend IA (Python) pour appliquer les dernières modifications.");
      return;
    }
    this.cartService.addItem({ productId: p.id, quantity: 1 }).subscribe({
      next: () => alert(`"${p.name}" ajouté au panier !`),
      error: (err) => {
        const errorMsg = err.error?.error || err.error?.message || err.message;
        alert('Erreur : ' + errorMsg);
      }
    });
  }

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

        // ── Produit non trouvé ─────────────────────────────
        if (res.type === 'not_found') {
          this.messages.push({ role: 'bot', text: res.message, notFound: true });
          this.scrollToBottom();
          return;
        }
        // ✅ Budget trop bas
if (res.type === 'budget_too_low') {
  this.messages.push({
    role: 'bot',
    text: res.message,
    notFound: true
  });
  this.scrollToBottom();
  return;
}

        // ── Marque absente du catalogue ────────────────────
        if (res.type === 'brand_not_found') {
          this.messages.push({
            role: 'bot',
            text: `❌ Désolé, nous n'avons aucun produit de la marque **${res.brand}** dans notre catalogue.\n\n💡 Essayez une autre marque disponible : Asus,iPhone, Lenovo, MSI, Dell,...`,
            brandNotFound: true
          });
          this.scrollToBottom();
          return;
        }

        // ✅ Bundle intelligent ─────────────────────────────
        if (res.type === 'bundle') {
          this.messages.push({
            role:   'bot',
            bundle: { productName: res.productName!, bundles: res.bundles! }
          });
          this.scrollToBottom();
          return;
        }

        // ── Comparaison ────────────────────────────────────
        if (res.type === 'comparison') {
          this.messages.push({ role: 'bot', comparison: res });
        } else {
          // ── Recherche normale ──────────────────────────────
          let msgText = res.profile
            ? `🔍 Profil détecté : ${res.profile.toUpperCase()}`
            : '';

          // ✅ Affiche les filtres détectés
          if (res.filters) {
            msgText += `\n🎯 Filtres : ${res.filters}`;
          }

          if (res.warning) {
            msgText += `\n\n${res.warning}`;
          }

          this.messages.push({
            role:     'bot',
            text:     msgText,
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