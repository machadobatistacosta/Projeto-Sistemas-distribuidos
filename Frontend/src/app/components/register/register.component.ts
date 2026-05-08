import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <section class="login-section">
      <div class="card login-card">
        <h2>Criar Conta no MicroChefs</h2>
        <form (ngSubmit)="onRegister()">
          <div class="form-group">
            <label>Nome Completo</label>
            <input type="text" [(ngModel)]="user.nome" name="nome" required placeholder="Seu Nome">
          </div>
          <div class="form-group">
            <label>E-mail</label>
            <input type="email" [(ngModel)]="user.email" name="email" required placeholder="seu@email.com">
          </div>
          <div class="form-group">
            <label>Senha</label>
            <input type="password" [(ngModel)]="user.senha" name="senha" required placeholder="••••••••">
          </div>
          <button type="submit" class="btn btn-primary w-100" [disabled]="isSubmitting">
            {{ isSubmitting ? 'Registrando...' : 'Registrar' }}
          </button>
          
          <p class="mt-3 text-center">
            Já tem uma conta? <a routerLink="/login">Entre aqui</a>
          </p>
        </form>
      </div>
    </section>
  `,
  styles: [`
    .login-section { display: flex; justify-content: center; padding-top: 5rem; }
    .login-card { padding: 3rem; width: 100%; max-width: 450px; }
    h2 { margin-bottom: 2rem; text-align: center; }
    .form-group { margin-bottom: 1.5rem; }
    label { display: block; margin-bottom: 0.5rem; color: var(--text-muted); }
    input { width: 100%; padding: 0.8rem; background: var(--bg-dark); border: 1px solid var(--glass-border); border-radius: 12px; color: white; }
    .w-100 { width: 100%; margin-top: 1rem; }
    .mt-3 { margin-top: 1rem; }
    .text-center { text-align: center; }
    a { color: var(--primary); text-decoration: none; font-weight: 600; }
  `]
})
export class RegisterComponent {
  user = {
    nome: '',
    email: '',
    senha: ''
  };
  isSubmitting = false;

  constructor(private authService: AuthService, private router: Router) {}

  onRegister(): void {
    if (this.isSubmitting) return;

    this.isSubmitting = true;
    this.authService.register(this.user).subscribe({
      next: () => {
        this.isSubmitting = false;
        alert('Cadastro realizado com sucesso!');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.isSubmitting = false;
        const message = typeof err.error === 'string' ? err.error : 'Erro ao cadastrar usuário!';
        alert(message);
      }
    });
  }
}
