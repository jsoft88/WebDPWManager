import { Injectable } from '@angular/core';
import {DeploymentsByRoles, Host, MasterType} from './host-list/shared/host.model';
import {Observable} from 'rxjs/Observable';
import {Http, Response, Headers} from '@angular/http'
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import {AgentExecution} from './agent-execution/shared/agent-execution.model';
import {ConstantService} from './constant-service.service';
import {BaseService} from './base-service';

@Injectable()
export class HostService extends BaseService {

  // private baseUrl = 'http://localhost:9000/api';

  constructor(private http: Http, private constantsService: ConstantService) { super(); }

  getAll(): Observable<Host[]> {
    const hosts =
      this.http
        .get(`${this.constantsService.API_ENDPOINT}/hosts/all`, { headers: this.getHeaders() })
        .map(mapHosts)
        .catch((errResponse: Response) => {
          if (this.analyzeIfResponseIsLackOfRole(errResponse.json(), this.constantsService.LACK_OF_ROLE_ERROR_RANGE)) {
            return Observable.throw({ 'type': this.constantsService.ERROR_LACK_OF_ROLE, 'errorDescription': errResponse.statusText });
          }

          return Observable.throw({ 'type': this.constantsService.ERROR_OTHER, 'errorDescription': errResponse.statusText });
        });

    return hosts;
  }

  getHostsInCluster(actorSystemName: String): Observable<Host[]> {
    const hosts =
      this.http
        .get(`${ this.constantsService.API_ENDPOINT }/hosts/cluster/${ actorSystemName }`, this.getHeaders())
        .map( response => mapHosts(response))
        .catch( (errResponse: Response) => {
          if (this.analyzeIfResponseIsLackOfRole(errResponse.json(), this.constantsService.LACK_OF_ROLE_ERROR_RANGE)) {
            return Observable.throw({ 'type': this.constantsService.ERROR_LACK_OF_ROLE, 'errorDescription': errResponse.statusText });
          }

          return Observable.throw({ 'type': this.constantsService.ERROR_OTHER, 'errorDescription': errResponse.statusText });
        });

    return hosts;
  }

  getClusterSystems(): Observable<String[]> {
    const clusters: String[] = new Array();
    return this.http
      .get(`${ this.constantsService.API_ENDPOINT }/hosts/clusters`, this.getHeaders())
      .map(response => {
        response.json().forEach(e => clusters.push(e));
        return clusters;
      }).catch((error: Response) => {
        if (this.analyzeIfResponseIsLackOfRole(error.json(), this.constantsService.LACK_OF_ROLE_ERROR_RANGE)) {
          return Observable.throw({ 'type': this.constantsService.ERROR_LACK_OF_ROLE, 'errorDescription': error.statusText });
        }

        return Observable.throw({ 'type': this.constantsService.ERROR_OTHER, 'errorDescription': error.statusText });
      });
  }

  addHost(host: Host): Observable<Host> {
    const hostResponse =
      this.http
        .post(`${this.constantsService.API_ENDPOINT}/hosts/add`, JSON.stringify(host), this.getHeaders())
        .map(data => {
          if (data.json().error) {
            Observable.throw(data.json().message);
          } else {
            toHost(data.json());
          }
        }).catch((error: Response) => {
          if (this.analyzeIfResponseIsLackOfRole(error.json(), this.constantsService.LACK_OF_ROLE_ERROR_RANGE)) {
            return Observable.throw({ 'type': this.constantsService.ERROR_LACK_OF_ROLE, 'errorDescription': error.statusText });
          }

          return Observable.throw({ 'type': this.constantsService.ERROR_OTHER, 'errorDescription': error.statusText });
        });

    return hostResponse;
  }

  private getHeaders() {
    const headers = new Headers();
    headers.append('Accept', 'application/json');
    headers.append('Content-Type', 'application/json');

    return headers;
  }
}

function mapHosts(response: Response): Host[]  {
  const hosts: Host[] = new Array();
  response.json().forEach(h => hosts.push(toHost(h)));

  return hosts;
}

function toHost(r: any): Host {
  const host = <Host>({
    hostId: r.hostId,
    address: r.address,
    deployments: toDeploymentsByRoles(r.deployments),
    executions: toExecutions(r.executions)
  });

  return host;
}

function toDeploymentsByRoles(jsDeployments: any[]): DeploymentsByRoles[] {
  const deployments: DeploymentsByRoles[] = new Array();

  for (const deploy of jsDeployments) {
    deployments.push(<DeploymentsByRoles>({
      deployId: deploy.deployId,
      actorSystemName: deploy.actorSystemName,
      actorName: deploy.actorName,
      port: deploy.port,
      componentId: deploy.componentId,
      role: deploy.role
    }));
  }
  return deployments;
}

function toExecutions(jsExecutions: any[]) {
  const executions: AgentExecution[] = new Array();

  for (const exec of jsExecutions) {
    executions.push(<AgentExecution>({
      agentExecId: exec.agentExecId,
      command: exec.command,
      deployId: exec.deployId,
      masterType: <MasterType>({
        masterTypeId: exec.masterType.masterTypeId,
        masterLabel: exec.masterType.masterLabel
      }),
      executionTimestamp: exec.executionTimestamp,
      cleanStop: exec.cleanStop,
      agentExecutionDetails: []
    }));
    return executions;
  }
}

function handleHostListRetrieveError(error: any) {
  const errDesc = error.message || 'No error description is available';
  const errorMsg = `An error occurred while retrieving hosts. Description: ${ errDesc }.`;

  return Observable.throw(errorMsg);
}

function handleAddHostError(error: Response) {
  if (error.status === 500) {
    const errorMsg = `An error occurred while adding a new host to cluster. Description: ${ error.json().errors[0] }`;

    return Observable.throw(errorMsg);
  }
}
