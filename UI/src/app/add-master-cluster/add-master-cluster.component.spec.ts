import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddMasterClusterComponent } from './add-master-cluster.component';

describe('AddMasterClusterComponent', () => {
  let component: AddMasterClusterComponent;
  let fixture: ComponentFixture<AddMasterClusterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddMasterClusterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddMasterClusterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
