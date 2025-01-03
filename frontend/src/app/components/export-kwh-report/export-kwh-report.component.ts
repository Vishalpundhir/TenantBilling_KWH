import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';

@Component({
  selector: 'export-kwh-report',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule, NgSelectModule],
  templateUrl: './export-kwh-report.component.html',
  styleUrls: ['./export-kwh-report.component.css']
})
export class ExportKwhReportComponent implements OnInit {
  reportType: string = '';
  selectedTenantId: number | null = null;
  selectedEnergyMeters: number[] = [];
  startDate: string = '';
  endDate: string = '';
  tenants: any[] = [];
  energyMeters: any[] = [];
  showErrors: boolean = false;
  submitted: boolean = false;
  startMonth: string = '';
  endMonth: string = '';
  selectedTenantName: string = ''; // Added to store tenant name

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchAllTenants();
  }

  fetchAllTenants(): void {
    this.http.get<any[]>('http://localhost:8080/bms-reports/v1/get-all-tenants').subscribe({
      next: (data) => {
        this.tenants = data;
      },
      error: (err) => {
        console.error('Failed to fetch tenants:', err);
      }
    });
  }

  // fetchEnergyMetersByTenant(): void {
  //   if (this.selectedTenantId) {
  //     this.http.get<any[]>(`http://localhost:8080/bms-reports/v1/all-active-energy-meters?tenantId=${this.selectedTenantId}`).subscribe({
  //       next: (data) => {
  //         this.energyMeters = data.filter((meter) => meter.tenantId === this.selectedTenantId);
  //         // Automatically check all energy meters
  //         this.selectedEnergyMeters = this.energyMeters.map((meter) => meter.id);
  //       },
  //       error: (err) => {
  //         console.error('Failed to fetch energy meters:', err);
  //       }
  //     });
  //   } else {
  //     this.energyMeters = [];
  //     this.selectedEnergyMeters = [];
  //   }
  // }

  fetchEnergyMetersByTenant(): void {
    if (this.selectedTenantId) {
      const selectedTenant = this.tenants.find((tenant) => tenant.id === this.selectedTenantId);
      this.selectedTenantName = selectedTenant?.name || ''; // Store tenant name
      this.http.get<any[]>(`http://localhost:8080/bms-reports/v1/all-active-energy-meters?tenantId=${this.selectedTenantId}`).subscribe({
        next: (data) => {
          this.energyMeters = data.filter((meter) => meter.tenantId === this.selectedTenantId);
          // Automatically check all energy meters
          this.selectedEnergyMeters = this.energyMeters.map((meter) => meter.id);
        },
        error: (err) => {
          console.error('Failed to fetch energy meters:', err);
        }
      });
    } else {
      this.energyMeters = [];
      this.selectedEnergyMeters = [];
    }
  }
  
  onTenantChange(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    this.selectedTenantId = Number(selectElement.value);
    this.fetchEnergyMetersByTenant();
  }

  exportReport(type: 'pdf' | 'excel'): void {
    this.submitted = true; 
    this.showErrors = !this.formIsValid();

    if (this.showErrors) {
      console.log('Form is invalid. Fix errors before exporting.');
      return;
    }

    console.log('Form is valid. Proceeding with export.');

    const tableNames = this.energyMeters
      .filter((meter) => this.selectedEnergyMeters.includes(meter.id))
      .map((meter) => meter.name)
      .join(',');

    const isMonthlyReport = this.reportType === 'monthly';
    const endpoint = isMonthlyReport
      ? type === 'pdf'
        ? `http://localhost:8080/bms-reports/v1/monthly-kwh-report-pdf-file`
        : `http://localhost:8080/bms-reports/v1/monthly-kwh-report-excel-file`
      : type === 'pdf'
        ? `http://localhost:8080/bms-reports/v1/daily-kwh-report-pdf-file`
        : `http://localhost:8080/bms-reports/v1/daily-kwh-report-excel-file`;
    
        console.log(encodeURIComponent(this.selectedTenantName));
    const queryParams = isMonthlyReport
      ? `?tableNames=${tableNames}&fromMonthYear=${this.startMonth}&toMonthYear=${this.endMonth}&tenantName=${encodeURIComponent(this.selectedTenantName)}`
      : `?tableNames=${tableNames}&fromDate=${this.startDate}&toDate=${this.endDate}&tenantName=${encodeURIComponent(this.selectedTenantName)}`;

    const fullUrl = endpoint + queryParams;

    console.log('Generated URL:', fullUrl);
    window.open(fullUrl, '_blank');
  }

  formIsValid(): boolean {
    if (this.reportType === 'monthly') {
      return !!this.selectedTenantId &&
             this.selectedEnergyMeters.length > 0 &&
             !!this.startMonth &&
             !!this.endMonth;
    } else {
      return !!this.selectedTenantId &&
             this.selectedEnergyMeters.length > 0 &&
             !!this.startDate &&
             !!this.endDate;
    }
  }

  onInputChange(): void {
    if (this.submitted) {
      this.showErrors = !this.formIsValid();
    }
  }

  toggleSelection(meterId: number): void {
    const index = this.selectedEnergyMeters.indexOf(meterId);
    if (index === -1) {
      this.selectedEnergyMeters.push(meterId);
    } else {
      this.selectedEnergyMeters.splice(index, 1);
    }
  }
}
