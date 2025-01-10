import { Routes } from '@angular/router';
import { BarChartComponent } from './components/bar-chart/bar-chart.component';
import { TenantManagementComponent } from './components/tenant-management/tenant-management.component';
import { PieChartComponent } from './components/pie-chart/pie-chart.component';
import { ExportKwhReportComponent } from './components/export-kwh-report/export-kwh-report.component';
import { EnergyMeterManagementComponent } from './components/energy-meter-management/energy-meter-management.component';
import { HomeComponent } from './components/home/home.component';
import { CommonAreaManagementComponent } from './components/common-area-management/common-area-management.component';
import { NotFoundComponent } from './components/not-found/not-found.component';

export const routes: Routes = [
  {path:'home', component:HomeComponent},
  {path:'common-area', component:CommonAreaManagementComponent},
  { path: 'export-kwh-report', component: ExportKwhReportComponent },
  { path: 'daily-visual-report', component: BarChartComponent },
  { path: 'tenant-management', component: TenantManagementComponent },
  {path:'energy-meter-management', component: EnergyMeterManagementComponent},
  { path: 'pie-chart', component: PieChartComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' }, // Default route
  { path: '**', component: NotFoundComponent }, // Wildcard route
];

