import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddDeploymentFormComponent } from './add-deployment-form.component';

describe('AddDeploymentFormComponent', () => {
  let component: AddDeploymentFormComponent;
  let fixture: ComponentFixture<AddDeploymentFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddDeploymentFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddDeploymentFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
