import {Component, OnDestroy, OnInit} from '@angular/core';
import {AgentExecutionDetails} from './shared/agent-execution-details.model';
import {AgentExecutionDetailsService} from '../agent-execution-details.service';
import {MasterService} from '../master.service';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/concat';
import {MasterField} from '../host-list/shared/host.model';

@Component({
  selector: 'app-agent-execution-details',
  template: './agent-execution-details.component.html',
  styleUrls: ['./agent-execution-details.component.css']
})
export class AgentExecutionDetailsComponent implements OnInit, OnDestroy {

  executionDetails: AgentExecutionDetails[];

  executionDetailsRetrieveError = '';
  loadingDetails = true;
  loadingFieldInfo = false;

  errorField: MasterField;

  sub: any;

  constructor(
    private agentExecutionDetailsService: AgentExecutionDetailsService,
    private masterService: MasterService,
    private route: ActivatedRoute) {

    this.errorField = new MasterField();
    this.errorField.fieldId = 0;
    this.errorField.fieldDescription = 'Failed to retrieve master field';
    this.errorField.fieldName = 'Error';
  }

  ngOnInit() {

    this.sub = this.route.params.subscribe(
      params => {
        const agentExecId = Number.parseInt(params['agentExecId']);
        this.agentExecutionDetailsService.getMastersAgentExecutionDetails(agentExecId)
          .subscribe(
            details => this.afterDetailsRetrieve(details),
            e => this.executionDetailsRetrieveError = e,
            () => this.loadingDetails = false
          );
      }
    );
  }

  private afterDetailsRetrieve(agentExecutionDetails: AgentExecutionDetails[]) {
    this.loadingFieldInfo = true;
    const batchRetrievals: Observable<MasterField>[] = [];

    agentExecutionDetails.forEach(
      detail => batchRetrievals.push(this.masterService.getSingleMasterField(detail.field.fieldId))
    );

    Observable.concat(...batchRetrievals).subscribe(
      field => agentExecutionDetails.filter(d => d.field.fieldId === field.fieldId)[0].field = field,
      errField => agentExecutionDetails.filter(d => d.field.fieldId === errField.fieldId)[0].field = errField,
      () => this.loadingFieldInfo = false
    );
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
