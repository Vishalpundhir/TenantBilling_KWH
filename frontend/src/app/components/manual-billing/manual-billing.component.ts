// import { Component, OnInit } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { HttpClientModule } from '@angular/common/http';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { environment } from '../../../environments/environment';

// @Component({
//   selector: 'manual-billing',
//   standalone: true,
//   imports: [CommonModule, FormsModule, HttpClientModule],
//   templateUrl: './manual-billing.component.html',
//   styleUrl: './manual-billing.component.css'
// })
// export class ManualBillingComponent {

// }
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'manual-billing',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './manual-billing.component.html',
  styleUrl: './manual-billing.component.css'
})
export class ManualBillingComponent implements OnInit {
  tenants: any[] = [];
  selectedTenantId: number | null = null;
  startDate: string = '';
  endDate: string = '';
  showErrors: boolean = false;
  private apiBaseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

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

  exportManualBill(): void {
    this.showErrors = !this.formIsValid();

    if (this.showErrors) {
      console.log('Form is invalid. Fix errors before exporting.');
      return;
    }

    const endpoint = `${this.apiBaseUrl}/export-manual-bill?tenantId=${this.selectedTenantId}&fromDate=${this.startDate}&toDate=${this.endDate}`;

    this.http.get(endpoint, { responseType: 'blob' }).subscribe({
      next: (data) => {
        const blob = new Blob([data], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'manual-bill.pdf';
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Failed to export manual bill:', err);
      }
    });
  }

  formIsValid(): boolean {
    return !!this.selectedTenantId && !!this.startDate && !!this.endDate;
  }

  isInvalidDateRange(): boolean { 
      return (this.startDate && this.endDate && this.startDate) > this.endDate;
  }
}
