import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'billing',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './billing.component.html',
  styleUrl: './billing.component.css'
})
export class BillingComponent implements OnInit {
  tenants: any[] = [];
  selectedTenantId: number | null = null;
  selectedMonth: string = '';
  showErrors: boolean = false;
  private apiBaseUrl = environment.apiBaseUrl;
  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.fetchAllTenants();
  }

  fetchAllTenants(): void {
    this.http.get<any[]>(`${this.apiBaseUrl}/get-all-tenants`).subscribe({
      next: (data) => {
        this.tenants = data;
      },
      error: (err) => {
        console.error('Failed to fetch tenants:', err);
      }
    });
  }

  exportBill(): void {
    this.showErrors = !this.formIsValid();
  
    if (this.showErrors) {
      console.log('Form is invalid. Fix errors before exporting.');
      return;
    }
  
    const endpoint = `${this.apiBaseUrl}/export-bill?tenantId=${this.selectedTenantId}&month=${this.selectedMonth.split('-')[1]}&year=${this.selectedMonth.split('-')[0]}`;
  
    this.http.get(endpoint, { responseType: 'blob' }).subscribe({
      next: (data) => {
        const blob = new Blob([data], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'bill.pdf';
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Failed to export bill:', err);
      }
    });
  }
  
  formIsValid(): boolean {
    return !!this.selectedTenantId && !!this.selectedMonth;
  }
}

