import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { User } from '../../models/user';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
// export class LoginComponent {

//   username: string = '';
//   password: string = '';
//   showErrors: boolean = false;

//   // Dummy users for authentication
//   users: User[] = [
//     { id: 1, username: 'adminUser', password: 'adminPass', role: 'admin' },
//     { id: 2, username: 'operatorUser', password: 'operatorPass', role: 'operator' },
//   ];

//   constructor(private router: Router) {}

//   @Output() loginSuccess = new EventEmitter<void>();

//   onLogin(): void {
//     const user = this.users.find(
//       (u) => u.username === this.username && u.password === this.password
//     );
  
//     if (!user) {
//       this.showErrors = true;
//       console.error('Invalid username or password');
//     } else {
//       this.showErrors = false;
  
//       // Store role and username in localStorage
//       localStorage.setItem('username', user.username);
//       localStorage.setItem('role', user.role);
  
//       console.log('Login successful:', { username: user.username, role: user.role });
  
//       // Emit login success event
//       this.loginSuccess.emit();
//     }
//   }
  

//   redirectBasedOnRole(role: string): void {
//     if (role === 'admin') {
//       console.log('Redirecting to admin dashboard...');
//       // Add redirection logic here (e.g., router.navigate)
//     } else if (role === 'operator') {
//       console.log('Redirecting to operator panel...');
//       // Add redirection logic here
//     }
//   }

// }
export class LoginComponent {
  username: string = '';
  password: string = '';
  showErrors: boolean = false;

  constructor(private loginService: LoginService, private router: Router) {}

  @Output() loginSuccess = new EventEmitter<void>();

  onLogin(): void {
    // Replace this with an API call to authenticate the user
    const dummyUsers = [
      { username: 'adminUser', password: 'adminPass', role: 'admin' },
      { username: 'operatorUser', password: 'operatorPass', role: 'operator' },
    ];

    const user = dummyUsers.find(
      (u) => u.username === this.username && u.password === this.password
    );

    if (!user) {
      this.showErrors = true;
      console.error('Invalid username or password');
    } else {
      this.showErrors = false;

      // Store the username and role in the LoginService
      this.loginService.setUserData(user.username, user.role);

      console.log('Login successful:', { username: user.username, role: user.role });

      // Emit login success event and redirect
      this.loginSuccess.emit();
      this.redirectBasedOnRole(user.role);
    }
  }

  redirectBasedOnRole(role: string): void {
    if (role === 'admin') {
      this.router.navigate(['/home']);
    } else if (role === 'operator') {
      this.router.navigate(['/home']);
    }
  }
}
