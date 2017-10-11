import { Component, OnInit } from '@angular/core';
import { AgentExecution } from './shared/agent-execution.model';
import { AgentExecutionService } from '../agent-execution.service';
import {AgentExecutionDetailsService} from '../agent-execution-details.service';
import {MasterType} from '../host-list/shared/host.model';

@Component({
  selector: 'app-agent-execution',
  template: './agent-execution.component.html',
  styles: []
})
export class AgentExecutionComponent implements OnInit {

  agentExecutions: AgentExecution[] = [];

  errorMessage = '';

  executionDetailsRetrieveError = '';

  loadingExecutionDetails = false;

  constructor(private agentExecutionService: AgentExecutionService, private agentExecutionDetailsService: AgentExecutionDetailsService) { }

  ngOnInit() {
    this.agentExecutionService
      .getMastersAgentExecution()
      .subscribe(
        e => {
          this.agentExecutions = e
          this.postExecutionsRetrieve();
        },
        err => this.errorMessage = err
      );
  }

  private postExecutionsRetrieve() {
    this.loadingExecutionDetails = true;
    for (const exec of this.agentExecutions) {
      this.agentExecutionDetailsService
        .getMastersAgentExecutionDetails(exec.agentExecId)
        .subscribe(
          details => exec.agentExecutionDetails = details,
          err => this.executionDetailsRetrieveError = err,
          () => this.loadingExecutionDetails = false
        );
    }
  }

  clickMasterType(masterType: MasterType) {

  }
}
