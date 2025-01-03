import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'side-bar',
  standalone: true,
  imports: [CommonModule,FormsModule, RouterModule],
  templateUrl: './side-bar.component.html',
  styleUrl: './side-bar.component.css'
})
export class SideBarComponent {
  userName = 'John Doe'; 
  showDropdown = false; 

  toggleDropdown(): void {
    this.showDropdown = !this.showDropdown; 
  }  
}
