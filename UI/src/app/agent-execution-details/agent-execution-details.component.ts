import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {AgentExecutionDetails} from './shared/agent-execution-details.model';
import {AgentExecutionDetailsService} from '../agent-execution-details.service';
import {MasterService} from '../master.service';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/concat';
import {MasterField, MasterType} from '../host-list/shared/host.model';
import {AgentExecutionService} from '../agent-execution.service';
import {AgentExecution} from "../agent-execution/shared/agent-execution.model";

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

  @Input() editing: boolean;
  @Input() agentExecution: AgentExecution;
  @Output() executionDetailsSubmitEmitter = new EventEmitter();

  constructor(private agentExecutionDetailsService: AgentExecutionDetailsService,
              private masterService: MasterService,
              private route: ActivatedRoute,
              private agentExecutionService: AgentExecutionService) {

    this.errorField = new MasterField();
    this.errorField.fieldId = 0;
    this.errorField.fieldDescription = 'Failed to retrieve master field';
    this.errorField.fieldName = 'Error';
    this.errorField.fieldEnabled = false;
  }

  ngOnInit() {

    if (!this.editing) {
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
    } else {
      this.executionDetails = new Array();
      this.masterService.getMasterFields(this.agentExecution.masterType.masterTypeId).subscribe(
        fields => fields.forEach(f => {
          const details = new AgentExecutionDetails();
          details.field = f;

          this.executionDetails.push(details);
        }),
        err => {
          const errDetails = new AgentExecutionDetails();
          errDetails.field = this.errorField;

          this.executionDetails.push(errDetails);
        }
      );
    }
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
    if (!this.editing) {
      this.sub.unsubscribe();
    }
  }

  onDeployClick(): void {
    if (this.agentExecution.command === '') {
      this.executionDetailsSubmitEmitter.emit(
        {'error': true, 'errorDescription:': 'Command field is empty.', 'agentExecution': this.agentExecution}
      );
      return;
    }

    this.agentExecution.agentExecutionDetails = this.executionDetails;
    this.agentExecutionService.addAgentExecution(this.agentExecution).subscribe(
      response => {
        this.agentExecution.agentExecId = response.agentExecId;
        this.executionDetailsSubmitEmitter.emit({
          'error': false,
          errorDescription: '',
          'agentExecution': this.agentExecution
        });
      },
      err => this.executionDetailsSubmitEmitter.emit(
        {'error': true, 'errorDescription': err, 'agentExecution': this.agentExecution})
    );
  }
}
