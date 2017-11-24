import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RequiredDeploymentsComponent } from './required-deployments.component';

describe('RequiredDeploymentsComponent', () => {
  let component: RequiredDeploymentsComponent;
  let fixture: ComponentFixture<RequiredDeploymentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RequiredDeploymentsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequiredDeploymentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
