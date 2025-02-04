import { Component, EventEmitter, Output  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';


@Component({
  selector: 'side-bar',
  standalone: true,
  imports: [CommonModule,FormsModule, RouterModule],
  templateUrl: './side-bar.component.html',
  styleUrl: './side-bar.component.css'
})
// export class SideBarComponent {
//   userName: string = '';
//   role: string = '';
//   showDropdown: boolean = false;

//   @Output() logout = new EventEmitter<void>(); 

//   constructor(private router: Router) {}

//   ngOnInit(): void {
//     this.loadUserName();
//   }

//   loadUserName(): void {
//     this.userName = localStorage.getItem('username') || 'Guest';
//     this.role = localStorage.getItem('role') || '';
//   }

//   toggleDropdown(): void {
//     this.showDropdown = !this.showDropdown;
//   }

//   onSignOut(): void {
//     localStorage.clear(); 
//     this.logout.emit(); 
//     this.router.navigate(['/login']); 
//   }
// }
export class SideBarComponent {
  userName: string = '';
  role: string = '';
  showDropdown: boolean = false;

  @Output() logout = new EventEmitter<void>();

  constructor(private loginService: LoginService, private router: Router) {}

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    this.userName = this.loginService.getUsername() || 'Guest';
    this.role = this.loginService.getRole() || '';
  }

  toggleDropdown(): void {
    this.showDropdown = !this.showDropdown;
  }

  onSignOut(): void {
    this.loginService.clearUserData();
    this.logout.emit();
    this.router.navigate(['/login']);
  }
}
