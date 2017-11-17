import { Injectable } from '@angular/core';
import {ConstantService} from './constant-service.service';
import {Http, Response, Headers} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import {AgentExecutionDetails} from './agent-execution-details/shared/agent-execution-details.model';
import {MasterField} from './host-list/shared/host.model';

@Injectable()
export class AgentExecutionDetailsService {

  constructor(private http: Http, private constantService: ConstantService) { }

  getMastersAgentExecutionDetails(agentExecId: number): Observable<AgentExecutionDetails[]> {
    const executionDetails = this.http
      .get(`${this.constantService.API_ENDPOINT}/execs/details/${agentExecId}`, {headers: this.getHeaders()})
      .map(mapAgentExecutionDetails)
      .catch(handleExecutionDetailsRetrieveError);

    return executionDetails;
  }

  private getHeaders() {
    const headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('Content-Type', 'application/json');

    return headers;
  }
}

function handleExecutionDetailsRetrieveError(error: any) {
  const errorMsg = error.message || `Something went wrong while retrieving execution details.`;
  return Observable.throw(errorMsg);
}

function mapAgentExecutionDetails(response: Response): AgentExecutionDetails[] {
  return response.json().results.map(toAgentExecutionDetails);
}

function toAgentExecutionDetails(r: any): AgentExecutionDetails {
  const masterField = new MasterField();
  masterField.fieldId = Number.parseInt(r.masterField.fieldId);
  masterField.fieldName = '';
  masterField.fieldDescription = '';
  const agentExecutionDetails = <AgentExecutionDetails>({
    field: masterField,
    value: r.value.toString()
  });

  return agentExecutionDetails;
}
