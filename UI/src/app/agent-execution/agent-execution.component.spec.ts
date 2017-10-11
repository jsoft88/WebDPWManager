import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AgentExecutionComponent } from './agent-execution.component';

describe('AgentExecutionComponent', () => {
  let component: AgentExecutionComponent;
  let fixture: ComponentFixture<AgentExecutionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AgentExecutionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgentExecutionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
