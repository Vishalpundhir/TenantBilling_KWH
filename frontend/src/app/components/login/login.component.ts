import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { User } from '../../models/user';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';
import { HttpClient } from '@angular/common/http';
import { HttpClientModule } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  showErrors: boolean = false;

  private apiBaseUrl = environment.apiBaseUrl;

  constructor(private loginService: LoginService, private router: Router,private http: HttpClient) {}

  @Output() loginSuccess = new EventEmitter<void>();

  onLogin(): void {
    const loginDetails = { username: this.username, password: this.password };

    this.http.post<{ username: string; role: string }>(`${this.apiBaseUrl}/login`, loginDetails)
      .subscribe({
        next: (response) => {
          this.showErrors = false;
          this.loginService.setUserData(response.username, response.role);
          console.log('Login successful:', response);
          this.loginSuccess.emit();
          this.redirectBasedOnRole(response.role);
        },
        error: (error) => {
          this.showErrors = true;
          // this.errorMessage = 'Invalid username or password';
          console.error('Login failed:', error);
        }
      });
  }


  redirectBasedOnRole(role: string): void {
    if (role === 'admin') {
      this.router.navigate(['/home']);
    } else if (role === 'operator') {
      this.router.navigate(['/home']);
    }
  }
}
