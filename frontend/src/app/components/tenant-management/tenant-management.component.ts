import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'tenant-management',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  templateUrl: './tenant-management.component.html',
  styleUrl: './tenant-management.component.css'
})
export class TenantManagementComponent {

  tenants: any[] = [];
  showForm: boolean = false;
  isEdit: boolean = false;
  showAllDetails: boolean = false;
  currentTenant: any = {
    id: null, name: '', address: '', personOfContact: '', mobileNumber: '',
    email: '',
    areaOccupied: '',
    unitAddress: ''
  };
  showErrors: boolean = false;
  private apiBaseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.loadTenants();
  }

  loadTenants(): void {
    this.http.get<any[]>(`${this.apiBaseUrl}/get-all-tenants`).subscribe(
      (data) => {
        this.tenants = data;
      },
      (error) => {
        console.error('Error fetching tenants:', error);
      }
    );
  }

  onAddTenant(): void {
    this.isEdit = false;
    this.currentTenant = {
      id: null,
      name: '',
      address: '',
      personOfContact: '',
      mobileNumber: '',
      email: '',
      areaOccupied: '',
      unitAddress: '',
    };
    this.showForm = true;
    this.showErrors = false;
  }

  onCancel(): void {
    this.showForm = false;
    this.showErrors = false;
  }

  onEditTenant(tenantId: number): void {
    this.isEdit = true;
    this.currentTenant = { ...this.tenants.find((tenant) => tenant.id === tenantId) };
    this.showForm = true;
  }

  onSaveTenant(): void {
    this.showErrors = true;

    if (
      !this.currentTenant.name ||
      !this.currentTenant.address ||
      !this.currentTenant.personOfContact ||
      !this.currentTenant.mobileNumber ||
      !this.currentTenant.email ||
      !this.currentTenant.unitAddress ||
      !this.currentTenant.areaOccupied
    ) {
      console.log('Form validation failed');
      return;
    }

    this.showErrors = false;

    if (this.isEdit) {
      this.http.put<any>(`${this.apiBaseUrl}/update-tenant/${this.currentTenant.id}`, this.currentTenant).subscribe(
        (updatedTenant) => {
          console.log('Tenant updated successfully');
          const index = this.tenants.findIndex((t) => t.id === this.currentTenant.id);
          if (index > -1) {
            this.tenants[index] = { ...this.currentTenant };
          }
          this.showForm = false;
        },
        (error) => console.error('Error updating tenant:', error)
      );
    } else {
      this.http.post<any>(`${this.apiBaseUrl}/add-tenant`, this.currentTenant).subscribe(
        (newTenant) => {
          console.log('Tenant added successfully');
          this.loadTenants();
          this.showForm = false;
        },
        (error) => console.error('Error adding tenant:', error)
      );
    }
  }

  onDeleteTenant(tenantId: number): void {
    if (confirm('Are you sure you want to delete this tenant?')) {
      this.http
        .delete(`${this.apiBaseUrl}/delete-tenant/${tenantId}`, { responseType: 'text' }) // Specify responseType as 'text'
        .subscribe(
          (response) => {
            console.log(response); // Log the plain text response
            this.tenants = this.tenants.filter((tenant) => tenant.id !== tenantId); // Update the table immediately
          },
          (error) => {
            console.error('Error deleting tenant:', error);
          }
        );
    }
  }
}