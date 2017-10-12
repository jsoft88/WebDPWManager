import { TestBed, inject } from '@angular/core/testing';

import { AgentExecutionService } from './agent-execution.service';

describe('AgentExecutionService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AgentExecutionService]
    });
  });

  it('should be created', inject([AgentExecutionService], (service: AgentExecutionService) => {
    expect(service).toBeTruthy();
  }));
});
