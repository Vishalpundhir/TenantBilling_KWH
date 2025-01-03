import { HttpClient } from '@angular/common/http';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Component, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import {  ChartConfiguration, ChartType } from 'chart.js';
Chart.register(...registerables);

@Component({
  selector: 'pie-chart',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './pie-chart.component.html',
  styleUrl: './pie-chart.component.css'
})
export class PieChartComponent implements OnInit {
  @ViewChild('pieChartCanvas', { static: true }) pieChartCanvas!: ElementRef;

  tenants: Array<{ id: string; name: string }> = [];
  selectedTenantId: string = '';
  selectedMonthYear: string = '';
  showErrors: boolean = false;

  chartData = {
    labels: ['EM1', 'EM2', 'EM3', 'EM4'],
    values: [30, 20, 25, 25],  
    colors: ['#003A6C', '#A2C8E1', '#006FB6', '#81B9D6']
  };

  private tenantsEndpoint = 'http://localhost:8080/bms-reports/v1/get-all-tenants';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchTenants();
  }

  fetchTenants() {
    this.http.get<{ id: string; name: string }[]>(this.tenantsEndpoint).subscribe({
      next: (data) => {
        this.tenants = data;
      },
      error: (err) => {
        console.error('Error fetching tenants:', err);
      },
    });
  }

  handleShowPieChart(): void {
    if (!this.selectedTenantId || !this.selectedMonthYear) {
      this.showErrors = true;
      return;
    }

    this.showErrors = false;
    this.drawPieChart();
  }

  drawPieChart(): void {
    const canvas = this.pieChartCanvas.nativeElement;
    const ctx = canvas.getContext('2d');

    if (!ctx) {
      console.error('Unable to get canvas context');
      return;
    }

    const total = this.chartData.values.reduce((acc, value) => acc + value, 0);
    let startAngle = 0;
    let endAngle: number;
    const slices: any[] = []; 

    this.chartData.values.forEach((value, index) => {
      const sliceAngle = (value / total) * (Math.PI * 2); 
      endAngle = startAngle + sliceAngle;
      slices.push({ startAngle, endAngle, label: this.chartData.labels[index], value: this.chartData.values[index] });
      ctx.beginPath();
      ctx.moveTo(canvas.width / 2, canvas.height / 2);  
      ctx.arc(
        canvas.width / 2, canvas.height / 2, 
        canvas.width / 2, 
        startAngle, endAngle  
      );
      ctx.fillStyle = this.chartData.colors[index];  
      ctx.fill();
      
      startAngle = endAngle; 
    });

    canvas.addEventListener('mousemove', (event: { offsetX: any; offsetY: any; }) => {
      const mouseX = event.offsetX;
      const mouseY = event.offsetY;

      const distance = Math.sqrt(Math.pow(mouseX - canvas.width / 2, 2) + Math.pow(mouseY - canvas.height / 2, 2));
      const radius = canvas.width / 2;

      if (distance < radius) {
        slices.forEach((slice) => {
          const angle = Math.atan2(mouseY - canvas.height / 2, mouseX - canvas.width / 2);
          const normalizedAngle = angle < 0 ? angle + Math.PI * 2 : angle;

          if (normalizedAngle >= slice.startAngle && normalizedAngle <= slice.endAngle) {
            this.showTooltip(slice.label, slice.value);
          }
        });
      } else {
        this.hideTooltip();
      }
    });
  }

  showTooltip(label: string, value: number): void {
    const tooltipElement = document.getElementById('pieChartTooltip');
    if (tooltipElement) {
      tooltipElement.innerHTML = `${label}: ${value}%`;
      tooltipElement.style.display = 'block'; 
    }
  }

  hideTooltip(): void {
    const tooltipElement = document.getElementById('pieChartTooltip');
    if (tooltipElement) {
      tooltipElement.style.display = 'none';  // Hide tooltip
    }
  }
}
