import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, LoginDTO } from '../../services/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <section class="login-section">
      <div class="card login-card">
        <h2>Entrar no MicroChefs</h2>
        <form (submit)="onLogin()">
          <div class="form-group">
            <label>E-mail</label>
            <input type="email" [(ngModel)]="credentials.email" name="email" required placeholder="seu@email.com">
          </div>
          <div class="form-group">
            <label>Senha</label>
            <input type="password" [(ngModel)]="credentials.senha" name="senha" required placeholder="••••••••">
          </div>
          <button type="submit" class="btn btn-primary w-100">Entrar</button>
          
          <p class="mt-3 text-center">
            Não tem uma conta? <a routerLink="/register">Cadastre-se</a>
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
  `]
})
export class LoginComponent {
  credentials: LoginDTO = { email: '' };

  constructor(private authService: AuthService, private router: Router) {}

  onLogin(): void {
    this.authService.login(this.credentials).subscribe({
      next: () => this.router.navigate(['/menu']),
      error: () => alert('Erro no login!')
    });
  }
}
