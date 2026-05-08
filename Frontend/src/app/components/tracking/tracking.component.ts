import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { WebSocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-tracking',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <section class="tracking-section">
      <div class="card tracking-card" *ngIf="pedido; else loading">
        <h2>Acompanhando Pedido #{{ pedido.id }}</h2>
        
        <div class="status-stepper">
          <div class="step" [class.active]="isStatusAtLeast('CRIADO')">
            <div class="circle">1</div>
            <div class="label">Aguardando Pagamento</div>
          </div>
          <div class="line" [class.active]="isStatusAtLeast('PAGO')"></div>
          <div class="step" [class.active]="isStatusAtLeast('PAGO')">
            <div class="circle">2</div>
            <div class="label">Pago</div>
          </div>
          <div class="line" [class.active]="isStatusAtLeast('EM_PREPARO')"></div>
          <div class="step" [class.active]="isStatusAtLeast('EM_PREPARO')">
            <div class="circle">3</div>
            <div class="label">Na Cozinha</div>
          </div>
          <div class="line" [class.active]="isStatusAtLeast('PRONTO')"></div>
          <div class="step" [class.active]="isStatusAtLeast('PRONTO')">
            <div class="circle">4</div>
            <div class="label">Pronto</div>
          </div>
        </div>

        <!-- Painel de Simulação de Pagamento -->
        <div class="payment-simulation mt-5" *ngIf="pedido.status === 'CRIADO' || pedido.status === 'AGUARDANDO_PAGAMENTO'">
          <div class="simulation-card text-center">
            <h3>💳 Simulação de Pagamento</h3>
            <p>Seu pedido está aguardando o pagamento via <strong>{{ pedido.formaDePagamento }}</strong>.</p>
            <div class="mt-4">
              <button class="btn btn-primary btn-lg" (click)="simularPagamento()">Pagar Agora (Simular)</button>
            </div>
            <p class="mt-3 text-muted small">Este é um ambiente de demonstração acadêmica.</p>
          </div>
        </div>

        <div class="status-detail text-center mt-5">
            <h3 class="status-text">{{ getFriendlyStatus(pedido.status) }}</h3>
            <p *ngIf="pedido.status === 'PRONTO'">Seu pedido está pronto para ser retirado!</p>
            <p *ngIf="pedido.status === 'EM_PREPARO'">O Chef está preparando sua refeição com carinho.</p>
            <p *ngIf="pedido.status === 'PAGO'">Pagamento confirmado! Enviamos seu pedido para a cozinha.</p>
        </div>

        <button class="btn btn-secondary w-100 mt-4" routerLink="/pedidos">Voltar para meus pedidos</button>
      </div>

      <ng-template #loading>
        <div class="loading-state">
          <p>Carregando informações do pedido...</p>
        </div>
      </ng-template>
    </section>
  `,
  styles: [`
    .tracking-section { display: flex; justify-content: center; padding-top: 3rem; }
    .tracking-card { padding: 3rem; width: 100%; max-width: 600px; }
    h2 { text-align: center; margin-bottom: 3rem; }
    
    .status-stepper { display: flex; align-items: center; justify-content: space-between; position: relative; }
    .step { display: flex; flex-direction: column; align-items: center; z-index: 1; }
    .circle { width: 40px; height: 40px; border-radius: 50%; background: var(--bg-dark); border: 2px solid var(--glass-border); display: flex; align-items: center; justify-content: center; font-weight: bold; transition: all 0.3s; }
    .label { margin-top: 0.5rem; font-size: 0.75rem; color: var(--text-muted); font-weight: 600; }
    
    .line { flex: 1; height: 2px; background: var(--glass-border); margin-top: -1.2rem; }
    
    .step.active .circle { background: var(--primary); border-color: var(--primary); color: white; box-shadow: 0 0 15px var(--primary); }
    .step.active .label { color: var(--primary); }
    .line.active { background: var(--primary); }
    
    .status-text { color: var(--primary); font-size: 1.5rem; text-transform: uppercase; margin-bottom: 0.5rem; }
    .text-center { text-align: center; }
    .mt-4 { margin-top: 1.5rem; }
    .mt-5 { margin-top: 2.5rem; }
    .w-100 { width: 100%; }

    .simulation-card {
      background: rgba(255, 255, 255, 0.05);
      border: 1px dashed var(--primary);
      padding: 2rem;
      border-radius: 15px;
      backdrop-filter: blur(10px);
    }
    .simulation-card h3 { color: var(--primary); margin-bottom: 1rem; }
    .small { font-size: 0.8rem; }
  `]
})
export class TrackingComponent implements OnInit {
  pedido: any;
  statusHierarchy = ['CRIADO', 'AGUARDANDO_PAGAMENTO', 'PAGO', 'EM_PREPARO', 'PRONTO'];

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private webSocketService: WebSocketService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.orderService.getOrders().subscribe({
        next: (data) => {
          this.pedido = data.find((p: any) => p.id === +id);
          if (!this.pedido) console.error('Pedido não encontrado no banco:', id);
        },
        error: (err) => {
          console.error('Erro ao buscar pedidos:', err);
          alert('Erro ao carregar dados do pedido.');
        }
      });
      
      this.webSocketService.getStatusUpdates().subscribe((update: any) => {
        if (this.pedido && update.id === this.pedido.id) {
          console.log('Update de status recebido via WS:', update);
          
          // Validação sugerida pelo Elcio (Blindagem no Front)
          if (this.statusHierarchy.includes(update.statusPedido)) {
            this.pedido.status = update.statusPedido;
          } else {
            console.error('ALERTA: Status inválido recebido via WebSocket:', update.statusPedido);
            // Opcional: Mostrar um aviso sutil para o usuário ou apenas ignorar o dado sujo
          }
        }
      });
    }
  }

  simularPagamento(): void {
    if (this.pedido) {
      this.orderService.updateOrderStatus(this.pedido.id, 'PAGO').subscribe({
        next: () => {
          this.pedido.status = 'PAGO';
          alert('Pagamento simulado com sucesso!');
        },
        error: (err) => console.error('Erro ao simular pagamento:', err)
      });
    }
  }

  getFriendlyStatus(status: string): string {
    const labels: any = {
      'CRIADO': 'Aguardando Pagamento',
      'AGUARDANDO_PAGAMENTO': 'Aguardando Pagamento',
      'PAGO': 'Pago',
      'EM_PREPARO': 'Em Preparo',
      'PRONTO': 'Pronto',
      'CANCELADO': 'Cancelado'
    };
    return labels[status] || status;
  }

  isStatusAtLeast(status: string): boolean {
    if (!this.pedido) return false;
    const currentIdx = this.statusHierarchy.indexOf(this.pedido.status);
    const targetIdx = this.statusHierarchy.indexOf(status);
    return currentIdx >= targetIdx;
  }
}
