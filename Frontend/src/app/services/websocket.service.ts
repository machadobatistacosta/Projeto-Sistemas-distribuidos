import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private statusUpdates = new Subject<any>();

  constructor() {
    this.client = new Client({
      brokerURL: 'ws://localhost:8081/ws-status', // Default port for Produto-Service
      webSocketFactory: () => new SockJS('http://localhost:8081/ws-status'),
      debug: (str) => console.log(str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = (frame) => {
      this.client.subscribe('/topic/pedido-status', (message: Message) => {
        this.statusUpdates.next(JSON.parse(message.body));
      });
    };

    this.client.activate();
  }

  getStatusUpdates() {
    return this.statusUpdates.asObservable();
  }
}
