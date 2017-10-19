import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddHostFormComponent } from './add-host-form.component';

describe('AddHostFormComponent', () => {
  let component: AddHostFormComponent;
  let fixture: ComponentFixture<AddHostFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddHostFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddHostFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
