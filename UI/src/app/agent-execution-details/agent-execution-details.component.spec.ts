import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AgentExecutionDetailsComponent } from './agent-execution-details.component';

describe('AgentExecutionDetailsComponent', () => {
  let component: AgentExecutionDetailsComponent;
  let fixture: ComponentFixture<AgentExecutionDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AgentExecutionDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgentExecutionDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
