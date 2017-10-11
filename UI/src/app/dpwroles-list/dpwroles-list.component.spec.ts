import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DpwrolesListComponent } from './dpwroles-list.component';

describe('DpwrolesListComponent', () => {
  let component: DpwrolesListComponent;
  let fixture: ComponentFixture<DpwrolesListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DpwrolesListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DpwrolesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
