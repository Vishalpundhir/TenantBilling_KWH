import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'common-area-management',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  templateUrl: './common-area-management.component.html',
  styleUrl: './common-area-management.component.css'
})
// export class CommonAreaManagementComponent {
//   commonAreas: any[] = [];
   
//     showAllDetails : boolean = false;
//     currentcommonArea: any = {
//        id: null, name: '', address: '', personOfContact: '', mobileNumber: '',
//         email: '',
//       unitAddress: '',
//       totalArea:''
//        };
//     constructor(private http: HttpClient) { }
  
//     ngOnInit(): void {
//       this.loadTenants();
//     }
  
//     loadTenants(): void {
//       this.http.get<any[]>('http://localhost:8080/bms-reports/v1/get-common-area').subscribe(
//         (data) => {
//           this.commonAreas = data;
//         },
//         (error) => {
//           console.error('Error fetching tenants:', error);
//         }
//       );
//     } 
// }
export class CommonAreaManagementComponent {
  commonAreas: any[] = [];
  showForm: boolean = false;
  currentCommonArea: any = {
    id: null,
    name: '',
    address: '',
    personOfContact: '',
    mobileNumber: '',
    email: '',
    unitAddress: '',
    totalArea: ''
  };
  
  showAllDetails : boolean = false;
  fields: any[] = [
    { name: 'name', label: 'Name', type: 'text', required: true },
    { name: 'address', label: 'Address', type: 'text', required: true },
    { name: 'personOfContact', label: 'Person of Contact', type: 'text', required: true },
    { name: 'mobileNumber', label: 'Mobile Number', type: 'text', required: true },
    { name: 'email', label: 'Email', type: 'email', required: true },
    { name: 'unitAddress', label: 'Unit Address', type: 'text', required: true },
    { name: 'totalArea', label: 'Total Area', type: 'number', required: true },
  ];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadCommonAreas();
  }

  loadCommonAreas(): void {
    this.http.get<any[]>('http://localhost:8080/bms-reports/v1/get-common-area').subscribe(
      (data) => (this.commonAreas = data),
      (error) => console.error('Error fetching common areas:', error)
    );
  }

  onEdit(commonArea: any): void {
    this.currentCommonArea = { ...commonArea };
    this.showForm = true;
  }

  onCancel(): void {
    this.showForm = false;
    this.currentCommonArea = {};
  }

  onSave(): void {
    this.http.post('http://localhost:8080/bms-reports/v1/update-common-area', this.currentCommonArea).subscribe(
      () => {
        this.loadCommonAreas(); // Reload the list
        this.showForm = false;
      },
      (error) => console.error('Error updating common area:', error)
    );
  }
}
