import {Component, Input, OnInit} from '@angular/core';
import {AgentExecutionDetails} from './shared/agent-execution-details.model';
import {AgentExecutionDetailsService} from '../agent-execution-details.service';
import {AgentExecution} from '../agent-execution/shared/agent-execution.model';
import {MasterService} from '../master.service';

@Component({
  selector: 'app-agent-execution-details',
  template: './agent-execution-details.component.html',
  styleUrls: ['./agent-execution-details.component.css']
})
export class AgentExecutionDetailsComponent implements OnInit {

  executionDetails: AgentExecutionDetails[];

  @Input() agentExecution: AgentExecution;

  executionDetailsRetrieveError = '';
  loadingDetails = true;
  loadingFieldInfo = false;
  fieldRetrieveError = '';

  constructor(
    private agentExecutionDetailsService: AgentExecutionDetailsService,
    private masterService: MasterService) { }

  ngOnInit() {
    this.agentExecutionDetailsService.getMastersAgentExecutionDetails(this.agentExecution.agentExecId)
      .subscribe(
        details => this.afterDetailsRetrieve(details),
        e => this.executionDetailsRetrieveError = e,
        () => this.loadingDetails = false
      );
  }

  private afterDetailsRetrieve(agentExecutionDetails: AgentExecutionDetails[]) {
    this.loadingFieldInfo = true;
    for (const detail of agentExecutionDetails) {
      if (this.fieldRetrieveError !== '') {
        break;
      }
      this.masterService.getSingleMasterField(detail.field.fieldId).subscribe(
        f => detail.field = f,
        e => {
          this.fieldRetrieveError = e;
          this.loadingFieldInfo = false;
        },
        () => this.loadingFieldInfo = false
      );
    }

    this.executionDetails = agentExecutionDetails;
  }

}
