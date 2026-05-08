import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../services/order.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <section class="pedidos-section">
      <h2 class="section-title">Seus Pedidos</h2>
      
      <div class="orders-list" *ngIf="pedidos.length > 0; else empty">
        <div class="card order-card" *ngFor="let p of pedidos">
          <div class="order-header">
            <span class="order-id">Pedido #{{ p.id }}</span>
            <span class="order-status" [class]="p.status">{{ p.status }}</span>
          </div>
          <div class="order-items">
            <p *ngFor="let item of p.itens">{{ item.quantidadeProduto }}x Produto #{{ item.idProduto }}</p>
          </div>
          <div class="order-footer">
             <span class="total">Total: R$ {{ p.valorTotal || 'N/A' }}</span>
             <div class="actions">
               <button *ngIf="p.status === 'CRIADO' || p.status === 'AGUARDANDO_PAGAMENTO'" 
                       class="btn btn-danger btn-sm me-2" 
                       (click)="cancelarPedido(p.id)">Cancelar</button>
               <button class="btn btn-primary btn-sm" (click)="irParaAcompanhamento(p.id)">Acompanhar</button>
             </div>
          </div>
        </div>
      </div>

      <ng-template #empty>
        <div class="empty-state">
          <p>Você ainda não fez nenhum pedido.</p>
          <button class="btn btn-primary" routerLink="/menu">Ver Cardápio</button>
        </div>
      </ng-template>
    </section>
  `,
  styles: [`
    .orders-list { display: grid; gap: 1.5rem; max-width: 800px; margin: 0 auto; }
    .order-card { padding: 1.5rem; }
    .order-header { display: flex; justify-content: space-between; margin-bottom: 1rem; }
    .order-status { padding: 0.3rem 0.8rem; border-radius: 50px; font-size: 0.8rem; font-weight: 700; text-transform: uppercase; }
    .order-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 1rem; border-top: 1px solid var(--glass-border); padding-top: 1rem; }
    .order-status.CRIADO { background: #f59e0b; color: #fff; }
    .order-status.AGUARDANDO_PAGAMENTO { background: #f59e0b; color: #fff; }
    .order-status.PAGO { background: #3b82f6; color: #fff; }
    .order-status.EM_PREPARO { background: #8b5cf6; color: #fff; }
    .order-status.PRONTO { background: #10b981; color: #fff; }
    .order-status.CANCELADO { background: #ef4444; color: #fff; }
    .actions { display: flex; align-items: center; }
    .me-2 { margin-right: 0.5rem; }
    .empty-state { text-align: center; padding: 5rem; }
    .empty-state p { margin-bottom: 2rem; color: var(--text-muted); }
  `]
})
export class PedidosComponent implements OnInit {
  pedidos: any[] = [];

  constructor(private orderService: OrderService, private router: Router) {}

  ngOnInit(): void {
    this.orderService.getOrders().subscribe(data => {
      this.pedidos = data;
    });
  }

  irParaAcompanhamento(id: number) {
    this.router.navigate(['/acompanhar', id]);
  }

  cancelarPedido(id: number) {
    if (confirm('Tem certeza que deseja cancelar este pedido?')) {
      this.orderService.cancelOrder(id).subscribe({
        next: () => {
          alert('Pedido cancelado com sucesso!');
          this.ngOnInit(); // Refresh list
        },
        error: (err) => alert('Erro ao cancelar pedido.')
      });
    }
  }
}
