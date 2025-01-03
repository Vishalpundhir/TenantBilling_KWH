import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExportKwhReportComponent } from './export-kwh-report.component';

describe('ExportKwhReportComponent', () => {
  let component: ExportKwhReportComponent;
  let fixture: ComponentFixture<ExportKwhReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExportKwhReportComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ExportKwhReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
