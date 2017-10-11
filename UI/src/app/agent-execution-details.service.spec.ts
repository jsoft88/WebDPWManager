import { TestBed, inject } from '@angular/core/testing';

import { AgentExecutionDetailsService } from './agent-execution-details.service';

describe('AgentExecutionDetailsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AgentExecutionDetailsService]
    });
  });

  it('should be created', inject([AgentExecutionDetailsService], (service: AgentExecutionDetailsService) => {
    expect(service).toBeTruthy();
  }));
});
