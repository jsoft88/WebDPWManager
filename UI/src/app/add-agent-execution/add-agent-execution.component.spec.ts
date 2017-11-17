import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddAgentExecutionComponent } from './add-agent-execution.component';

describe('AddAgentExecutionComponent', () => {
  let component: AddAgentExecutionComponent;
  let fixture: ComponentFixture<AddAgentExecutionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddAgentExecutionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddAgentExecutionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
