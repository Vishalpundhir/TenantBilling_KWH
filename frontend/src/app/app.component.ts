import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ExportKwhReportComponent } from './components/export-kwh-report/export-kwh-report.component';
import { SideBarComponent } from './components/side-bar/side-bar.component';
import { BarChartComponent } from './components/bar-chart/bar-chart.component';
import { TenantManagementComponent } from './components/tenant-management/tenant-management.component';
import { PieChartComponent } from './components/pie-chart/pie-chart.component';
import { EnergyMeterManagementComponent } from './components/energy-meter-management/energy-meter-management.component';
import { HomeComponent } from './components/home/home.component';
import { BillingComponent } from './components/billing/billing.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { LoginComponent } from './components/login/login.component';
import { OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LoginService } from './services/login.service';
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule,LoginComponent,RouterOutlet, ExportKwhReportComponent, SideBarComponent, BarChartComponent, TenantManagementComponent, PieChartComponent, EnergyMeterManagementComponent,HomeComponent,BillingComponent, NotFoundComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
// export class AppComponent implements OnInit {
//   isLoggedIn: boolean = false;

//   constructor(private router: Router) {}

//   ngOnInit(): void {
//     this.checkLoginStatus();
//   }

//   checkLoginStatus(): void {
//     this.isLoggedIn = !!localStorage.getItem('role'); 
//   }

//   onLoginSuccess(): void {
//     this.isLoggedIn = true; 
//     this.router.navigate(['/home']); 
//   }

//   onLogout(): void {
//     this.isLoggedIn = false;
//   }
// }
export class AppComponent implements OnInit {
  isLoggedIn: boolean = false;

  constructor(private router: Router, private loginService: LoginService) {}

  ngOnInit(): void {
    this.checkLoginStatus();
  }

  checkLoginStatus(): void {
    this.isLoggedIn = !!this.loginService.getRole();
  }

  onLoginSuccess(): void {
    this.isLoggedIn = true;
    this.router.navigate(['/home']);
  }

  onLogout(): void {
    this.loginService.clearUserData();
    this.isLoggedIn = false;
    this.router.navigate(['/login']);
  }
}