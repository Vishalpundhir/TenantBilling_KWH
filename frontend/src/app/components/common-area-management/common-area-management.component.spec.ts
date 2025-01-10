import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommonAreaManagementComponent } from './common-area-management.component';

describe('CommonAreaManagementComponent', () => {
  let component: CommonAreaManagementComponent;
  let fixture: ComponentFixture<CommonAreaManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonAreaManagementComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CommonAreaManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
