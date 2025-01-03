import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { ChangeDetectorRef } from '@angular/core';

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
  apiUrl = 'http://localhost:8080/bms-reports/v1';
  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.fetchEnergyMeters();
  }

  fetchEnergyMeters(): void {
    this.http.get<any[]>(`${this.apiUrl}/all-active-energy-meters`).subscribe({
      next: (data) => (this.energyMeters = data),
      error: (err) => console.error('Error fetching energy meters:', err),
    });
  }

  onAddEnergyMeter(): void {
    this.currentEnergyMeter = {};
    this.isEdit = false;
    this.showForm = true;
    this.showErrors = false;
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
      this.http.delete(`${this.apiUrl}/delete-energy-meter/${id}`).subscribe({
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
      this.http.put(`${this.apiUrl}/update-energy-meter`, this.currentEnergyMeter, { responseType: 'text' }).subscribe({
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
      this.http.post(`${this.apiUrl}/add-energy-meter`, this.currentEnergyMeter, { responseType: 'text' }).subscribe({
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