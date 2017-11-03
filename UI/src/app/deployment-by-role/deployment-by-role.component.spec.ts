import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeploymentByRoleComponent } from './deployment-by-role.component';

describe('DeploymentByRoleComponent', () => {
  let component: DeploymentByRoleComponent;
  let fixture: ComponentFixture<DeploymentByRoleComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeploymentByRoleComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeploymentByRoleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
