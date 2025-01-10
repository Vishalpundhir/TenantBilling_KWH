import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ExportKwhReportComponent } from './components/export-kwh-report/export-kwh-report.component';
import { SideBarComponent } from './components/side-bar/side-bar.component';
import { BarChartComponent } from './components/bar-chart/bar-chart.component';
import { TenantManagementComponent } from './components/tenant-management/tenant-management.component';
import { PieChartComponent } from './components/pie-chart/pie-chart.component';
import { EnergyMeterManagementComponent } from './components/energy-meter-management/energy-meter-management.component';
import { HomeComponent } from './components/home/home.component';
import { CommonAreaManagementComponent } from './components/common-area-management/common-area-management.component';
import { NotFoundComponent } from './components/not-found/not-found.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ExportKwhReportComponent, SideBarComponent, BarChartComponent, TenantManagementComponent, PieChartComponent, EnergyMeterManagementComponent,HomeComponent,CommonAreaManagementComponent, NotFoundComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'KWH-Frontend';
}
