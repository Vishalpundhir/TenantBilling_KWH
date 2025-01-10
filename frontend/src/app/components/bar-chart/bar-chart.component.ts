import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'bar-chart',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.css'],
})
export class BarChartComponent implements OnInit, AfterViewInit {
  @ViewChild('barChartCanvas', { static: true }) barChartCanvas!: ElementRef<HTMLCanvasElement>;

  selectedTenantId: number | null = null;
  selectedEnergyMeterId: number | null = null;
  selectedMonthYear: string = '';
  tenants: any[] = [];
  energyMeters: any[] = [];
  showErrors: boolean = false;
  barChart!: Chart;
  currentChartType: 'bar' | 'line' = 'bar';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchTenants();
  }

  ngAfterViewInit(): void {
    this.initializeChart();
  }

  fetchTenants(): void {
    this.http.get<any[]>('http://localhost:8080/bms-reports/v1/get-all-tenants').subscribe({
      next: (data) => {
        this.tenants = data;
      },
      error: (err) => {
        console.error('Error fetching tenants:', err);
      },
    });
  }

  onTenantChange(): void {
    if (this.selectedTenantId) {
      this.fetchEnergyMeters();
    }
  }

  fetchEnergyMeters(): void {
    this.http
      .get<any[]>(`http://localhost:8080/bms-reports/v1/all-active-energy-meters?tenantId=${this.selectedTenantId}`)
      .subscribe({
        next: (data) => {
          console.log('Raw data:', data); // Check raw response
          console.log('Selected Tenant ID:', this.selectedTenantId); // Check selectedTenantId
  
          // Ensure consistent type for comparison
          const tenantIdToMatch = String(this.selectedTenantId);
          this.energyMeters = data.filter(
            (meter) => String(meter.tenantId) === tenantIdToMatch
          );
  
          console.log('Filtered Energy Meters:', this.energyMeters);
        },
        error: (err) => {
          console.error('Error fetching energy meters:', err);
        },
      });
  }
  
  formIsValid(): boolean {
    return !!this.selectedTenantId && !!this.selectedEnergyMeterId && !!this.selectedMonthYear;
  }

  handleShowBarChart(): void {
    this.showErrors = !this.formIsValid();

    if (this.formIsValid()) {
      this.currentChartType = 'bar';
      this.fetchChartData();
    } else {
      console.error('Form is invalid. Fix errors before proceeding.');
    }
  }

  handleShowLineChart(): void {
    this.showErrors = !this.formIsValid();

    if (this.formIsValid()) {
      this.currentChartType = 'line';
      this.fetchChartData();
    } else {
      console.error('Form is invalid. Fix errors before proceeding.');
    }
  }

  fetchChartData(): void {
    const [year, month] = this.selectedMonthYear.split('-');
    const yearNum = parseInt(year, 10);
    const monthNum = parseInt(month, 10);

    const fromDate = `${year}-${month}-01`;
    const toDate = `${year}-${month}-${new Date(yearNum, monthNum, 0).getDate()}`;

    const tableNames = [this.selectedEnergyMeterId];

    this.http
      .get<any[]>(`http://localhost:8080/bms-reports/v1/daily-kwh-data?tableNames=${tableNames.join(',')}&fromDate=${fromDate}&toDate=${toDate}`)
      .subscribe({
        next: (data) => {
          this.updateChart(data, month, year);
        },
        error: (err) => {
          console.error('Error fetching daily KWH data:', err);
        },
      });
  }

  initializeChart(): void {
    const ctx = this.barChartCanvas.nativeElement.getContext('2d');
    this.barChart = new Chart(ctx!, {
      type: this.currentChartType,
      data: {
        labels: [],
        datasets: [],
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: '',
          },
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Days',
            },
          },
          y: {
            title: {
              display: true,
              text: 'Daily KWH',
            },
          },
        },
      },
    });
  }

  updateChart(data: any[], month: string, year: string): void {
    // Format the dates to dd-mm-yyyy
    const days = data.map((item) => {
      const date = new Date(item.date);
      const day = date.getDate().toString().padStart(2, '0');
      const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Months are 0-based
      const year = date.getFullYear();
      return `${day}-${month}-${year}`;
    });
  
    const values = data.map((item) => item.dailyKwh);
  
    if (days.length === 0 || values.length === 0) {
      console.error('Labels or values are empty, cannot update chart.');
      return;
    }
  
    if (this.barChart) {
      this.barChart.destroy();
    }
  
    const ctx = this.barChartCanvas.nativeElement.getContext('2d');
    this.barChart = new Chart(ctx!, {
      type: this.currentChartType,
      data: {
        labels: days, // Updated labels
        datasets: [
          {
            label: 'Daily KWH',
            data: values,
            backgroundColor: this.currentChartType === 'bar' ? 'rgba(54, 162, 235, 0.5)' : undefined,
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 2,
            fill: this.currentChartType === 'bar',
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: `Chart for ${month} ${year}`,
          },
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Days',
            },
          },
          y: {
            title: {
              display: true,
              text: 'Daily KWH',
            },
          },
        },
      },
    });
  }
}
