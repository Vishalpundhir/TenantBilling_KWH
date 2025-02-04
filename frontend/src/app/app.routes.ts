import { Routes } from '@angular/router';
import { BarChartComponent } from './components/bar-chart/bar-chart.component';
import { TenantManagementComponent } from './components/tenant-management/tenant-management.component';
import { PieChartComponent } from './components/pie-chart/pie-chart.component';
import { ExportKwhReportComponent } from './components/export-kwh-report/export-kwh-report.component';
import { EnergyMeterManagementComponent } from './components/energy-meter-management/energy-meter-management.component';
import { HomeComponent } from './components/home/home.component';
import { BillingComponent } from './components/billing/billing.component';
import { LoginComponent } from './components/login/login.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    canActivate: [authGuard], 
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'export-kwh-report', component: ExportKwhReportComponent },
      { path: 'daily-visual-report', component: BarChartComponent },
      { path: 'tenant-management', component: TenantManagementComponent },
      { path: 'energy-meter-management', component: EnergyMeterManagementComponent },
      { path: 'pie-chart', component: PieChartComponent },
      { path: 'billing', component: BillingComponent },
      { path: '', redirectTo: '/home', pathMatch: 'full' }, 
    ],
  },
  { path: '**', component: NotFoundComponent }, 
];


