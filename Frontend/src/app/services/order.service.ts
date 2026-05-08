import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

export interface ItemPedido {
  idProduto: number;
  quantidadeProduto: number;
  precoProduto: number;
}

export interface Pedido {
  clienteId: number;
  formaDePagamento: string; // "DEBITO", "CREDITO", etc.
  itens: ItemPedido[];
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8090/pedidos';

  constructor(private http: HttpClient) {}

  placeOrder(pedido: Pedido): Observable<any> {
    return this.http.post(`${this.apiUrl}/criar`, pedido, { headers: this.authHeaders() });
  }

  getOrders(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/exibir`, { headers: this.authHeaders() }).pipe(
      map(pedidos => pedidos.map(p => ({
        ...p,
        status: p.statusDoPedido || p.status_do_pedido // Handle both possible mappings
      })))
    );
  }

  updateOrderStatus(id: number, status: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/atualizar`, { id, statusPedido: status }, { headers: this.authHeaders() });
  }

  cancelOrder(id: number): Observable<any> {
    return this.updateOrderStatus(id, 'CANCELADO');
  }

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : new HttpHeaders();
  }
}
