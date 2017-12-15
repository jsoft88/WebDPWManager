import { Injectable } from '@angular/core';
import {ConstantService} from './constant-service.service';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import {DeploymentsByRoles, Host} from './host-list/shared/host.model';
import {DpwRoles} from './dpwroles-list/shared/dpw-roles.model';

@Injectable()
export class DeploymentByRoleService {

  host: Host;

  constructor(private http: Http, private constantsService: ConstantService) { }

  getDeploymentDetails(deployId: number): Observable<DeploymentsByRoles> {
    const deployments =
      this.http
        .get(`${this.constantsService.API_ENDPOINT}/hosts/deployments/details/${ deployId }`, this.getHeaders())
        .map(data => toDeploymentsByRoles(data))
        .catch(deploymentDetailsRetrieveError);
    return deployments;

  }

  getActorSystems(): Observable<DeploymentsByRoles[]> {
    return this.http
      .get(`${this.constantsService.API_ENDPOINT}/api/hosts/clusters`, this.getHeaders())
      .map(data => {
        data.json().map(d => toDeploymentsByRoles(d));
      })
      .catch((error: Response) => Observable.throw(error.statusText));
  }

  addDeployment(host: Host): Observable<Host> {
    const savedDeployment =
      this.http
        .post(`${this.constantsService.API_ENDPOINT}/hosts/deployments/add`, host, this.getHeaders())
        .map(response => {
          host.deployments.push(toDeploymentsByRoles(response.json().host.deployments))
        })
        .catch(handleAddDeploymentError);

    return savedDeployment;
  }

  getDeploymentsForHost(host: Host): Observable<DeploymentsByRoles[]> {
    return this
      .http
      .get(`${ this.constantsService.API_ENDPOINT }/hosts/roles/${ host.hostId }`, this.getHeaders())
      .map(response => {
        response.json().map(d => toDeploymentsByRoles(d));
      })
      .catch((error: Response) => {
        return Observable.throw(error.statusText);
      });
  }

  private getHeaders(): Headers {
    const headers = new Headers();
    headers.append('accept', 'application/json');
    headers.append('Content-Type', 'application/json');
    return headers;
  }
}

function toDeploymentsByRoles(r: any) {
  const deploymentsByRoles = <DeploymentsByRoles>({
    deployId: r.deployId,
    actorSystemName: r.actorSystemName,
    actorName: r.actorName,
    port: r.port,
    componentId: r.componentId,
    role: <DpwRoles>({
      roleId: r.role.roleId,
      roleLabel: r.role.roleLabel
    })
  });
  return deploymentsByRoles;
}

function handleAddDeploymentError(response: Response) {
  return Observable.throw(`An error occurred while doing deployment: ${response.json().errors[0]}`);
}

function deploymentDetailsRetrieveError(error: any) {
  const errorDesc = error.message || 'No error description is available';
  const errorMsg = `An error occurred while retrieving details of deployment: ${ errorDesc }`;
  return Observable.throw(errorMsg);
}
