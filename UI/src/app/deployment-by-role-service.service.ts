import { Injectable } from '@angular/core';
import {ConstantService} from './constant-service.service';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import {DeploymentsByRoles} from "./host-list/shared/host.model";
import {DpwRoles} from "./dpwroles-list/shared/dpw-roles.model";

@Injectable()
export class DeploymentByRoleService {

  constructor(private http: Http, private constantsService: ConstantService) { }

  getDeploymentDetails(deployId: number): Observable<DeploymentsByRoles> {
    const deployments =
      this.http
        .get(`${this.constantsService.API_ENDPOINT}/hosts/deployments/details/${ deployId }`, this.getHeaders())
        .map(data => toDeploymentsByRoles(data))
        .catch(deploymentDetailsRetrieveError);
    return deployments;

  }

  private getHeaders(): Headers {
    const headers = new Headers();
    headers.append('accept', 'application/json');
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

function deploymentDetailsRetrieveError(error: any) {
  const errorDesc = error.message || 'No error description is available';
  const errorMsg = `An error occurred while retrieving details of deployment: ${ errorDesc }`;
  return Observable.throw(errorMsg);
}
