import { Injectable } from '@angular/core';
import { AgentExecution } from './agent-execution/shared/agent-execution.model';
import { AgentExecutionDetails } from './agent-execution-details/shared/agent-execution-details.model';
import { Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import {ConstantService} from './constant-service.service';
import {MasterField, MasterType} from './host-list/shared/host.model';

@Injectable()
export class AgentExecutionService {

  constructor(private http: Http, private constantService: ConstantService) {
  }

  getMastersAgentExecution(deployId: number): Observable<AgentExecution[]> {
    const executions = this.http
      .get(`${this.constantService.API_ENDPOINT}/execs/${ deployId }`, {headers: this.getHeaders()})
      .map(mapAgentExecutions)
      .catch(handleMastersExecutionRetrieveError);

    return executions;
  }

  addAgentExecution(agentExecution: AgentExecution): Observable<AgentExecution> {
    return this.http
      .post(`${this.constantService.API_ENDPOINT}/master/execute`, agentExecution, this.getHeaders())
      .map(response => {
        if (response.json().errors.length === 0) {
          toAgentExecutionWithDetails(response.json());
        } else {
          Observable.throw(response.json().errors.join());
        }
      })
      .catch((err: Response) => {
         return Observable.throw(`An error occurred while deploying master: ${err.statusText.toString()}`);
      });
  }

  private getHeaders() {
    const headers = new Headers();
    headers.append('Accept', 'application/json');
    return headers;
  }
}

function handleMastersExecutionRetrieveError(error: any) {
  const errorMsg = error.message || `Something went wrong while retrieving masters executions from your hosts`;
  return Observable.throw(errorMsg);
}

function mapAgentExecutions(response: Response): AgentExecution[] {
  return response.json().map(toAgentExecution);
}

function toAgentExecution(r: any): AgentExecution {
  const masterType = new MasterType();
  masterType.masterLabel = '';
  masterType.masterTypeId = Number.parseInt(r.masterType.masterTypeId);
  const agentExecutionDetails = [];

  const agentExecution = <AgentExecution>({
    deployId: Number.parseInt(r.deployId),
    cleanStop: r.cleanStop,
    masterType: masterType,
    executionTimestamp: Number(r.executionTimestamp),
    command: r.command,
    agentExecId: Number.parseInt(r.agentExecId),
    agentExecutionDetails: agentExecutionDetails
  });

  return agentExecution;
}

function toAgentExecutionWithDetails(r: any): AgentExecution {
  const agentExecution = <AgentExecution>({
    deployId: Number.parseInt(r.deployId),
    cleanStop: r.cleanStop,
    masterType: r.masterType,
    executionTimestamp: Number(r.executionTimestamp),
    command: r.command,
    agentExecId: Number.parseInt(r.agentExecId),
    agentExecutionDetails: r.agentExecutionDetails
  });

  return agentExecution;
}
