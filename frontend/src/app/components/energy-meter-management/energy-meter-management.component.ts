import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { ChangeDetectorRef } from '@angular/core';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'energy-meter-management',
  standalone: true,
  imports: [FormsModule,CommonModule, HttpClientModule],
  templateUrl: './energy-meter-management.component.html',
  styleUrl: './energy-meter-management.component.css'
})
export class EnergyMeterManagementComponent implements OnInit {
  energyMeters: any[] = [];
  currentEnergyMeter: any = {};
  showForm = false;
  isEdit = false;
  showErrors = false;
  availableEnergyMeterNames: string[] = [];
  private apiBaseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.fetchEnergyMeters();
    this.fetchAvailableEnergyMeterNames();
  }

  fetchAvailableEnergyMeterNames(): void {
    this.http.get<string[]>(`${this.apiBaseUrl}/available-energy-meter-names`).subscribe({
      next: (names) => {
        const assignedMeters = this.energyMeters.map((meter) => meter.name);
        this.availableEnergyMeterNames = names.filter(
          (name) => !assignedMeters.includes(name)
        );
      },
      error: (err) => console.error('Error fetching available energy meter names:', err),
    });
  }

  fetchEnergyMeters(): void {
    this.http.get<any[]>(`${this.apiBaseUrl}/all-active-energy-meters`).subscribe({
      next: (data) => {
        this.energyMeters = data;
  
        // Fetch tenant details for each energy meter
        this.energyMeters.forEach((meter) => {
          this.http
            .get<any>(`${this.apiBaseUrl}/tenant-detail-by-id?tenantId=${meter.tenantId}`)
            .subscribe({
              next: (tenant) => {
                meter.tenantName = tenant.name; // Add tenant name to each energy meter
              },
              error: (err) =>
                console.error(`Error fetching tenant details for ID ${meter.tenantId}:`, err),
            });
        });
      },
      error: (err) => console.error('Error fetching energy meters:', err),
    });
  }  

  onAddEnergyMeter(): void {
    this.currentEnergyMeter = {};
    this.isEdit = false;
    this.showForm = true;
    this.showErrors = false;
    this.fetchAvailableEnergyMeterNames();
  }

  onEditEnergyMeter(id: number): void {
    const meter = this.energyMeters.find((m) => m.id === id);
    if (meter) {
      this.currentEnergyMeter = { ...meter };
      this.isEdit = true;
      this.showForm = true;
      this.showErrors = false;
    }
  }

  validateForm(): boolean {
    return !!this.currentEnergyMeter.name && !!this.currentEnergyMeter.tenantId;
  }

  onDeleteEnergyMeter(id: number): void {
    const confirmation = window.confirm('Are you sure you want to delete this energy meter?');
    if (confirmation) {
      this.http.delete(`${this.apiBaseUrl}/delete-energy-meter/${id}`).subscribe({
        next: (response: any) => {
          console.log('Response:', response);
  
          // Update the local energyMeters array
          this.energyMeters = this.energyMeters.filter((meter) => meter.id !== id);
  
          // Trigger change detection
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Error deleting energy meter:', err),
      });
    }
  }
  
  onSaveEnergyMeter(): void {
    this.showErrors = true;
  
    if (!this.validateForm()) {
      console.error('Form validation failed', this.currentEnergyMeter);
      return;
    }
  
    if (this.isEdit) {
      console.log('Updating energy meter:', this.currentEnergyMeter);
      this.http.put(`${this.apiBaseUrl}/update-energy-meter`, this.currentEnergyMeter, { responseType: 'text' }).subscribe({
        next: (response: string) => {
          console.log('Energy meter updated successfully:', response);
          const index = this.energyMeters.findIndex((m) => m.id === this.currentEnergyMeter.id);
          if (index !== -1) {
            this.energyMeters[index] = { ...this.currentEnergyMeter }; // Update local array
          }
          this.showForm = false; // Close the form on success
          this.fetchEnergyMeters(); // Refresh data
        },
        error: (err) => {
          console.error('Error updating energy meter:', err);
        },
      });
    } else {
      console.log('Adding new energy meter:', this.currentEnergyMeter);
      this.http.post(`${this.apiBaseUrl}/add-energy-meter`, this.currentEnergyMeter, { responseType: 'text' }).subscribe({
        next: (response: string) => {
          console.log('Energy meter added successfully:', response);
          this.energyMeters.push(this.currentEnergyMeter); // Add to local array
          this.showForm = false; // Close the form on success
          this.fetchEnergyMeters(); // Refresh data
        },
        error: (err) => {
          console.error('Error adding energy meter:', err);
        },
      });
    }
  }
   
  onCancel(): void {
    this.showForm = false;
  }
}