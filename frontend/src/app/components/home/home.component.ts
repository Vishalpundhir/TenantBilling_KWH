import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { OnInit } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'home',
  standalone: true,
  imports: [HttpClientModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  building: any = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.getBuildingDetails();
  }

  getBuildingDetails(): void {
    this.http
      .get('http://localhost:8080/bms-reports/v1/get-building-details/1')
      .subscribe({
        next: (response: any) => {
          this.building = response;
        },
        error: (err) => {
          console.error('Error fetching building details:', err);
        }
      });
  }
}
