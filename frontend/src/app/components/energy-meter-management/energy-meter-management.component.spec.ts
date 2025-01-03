import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnergyMeterManagementComponent } from './energy-meter-management.component';

describe('EnergyMeterManagementComponent', () => {
  let component: EnergyMeterManagementComponent;
  let fixture: ComponentFixture<EnergyMeterManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnergyMeterManagementComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EnergyMeterManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
