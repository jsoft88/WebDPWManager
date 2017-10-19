import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Http, Response, Headers} from '@angular/http'
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import {DpwRoles} from './dpwroles-list/shared/dpw-roles.model';
import {ConstantService} from './constant-service.service';

@Injectable()
export class DpwRolesService {

  constructor(private http: Http, private constantService: ConstantService) { }

  getDpwRoles(): Observable<DpwRoles[]> {
    const roles = this.http
      .get(`${this.constantService.API_ENDPOINT}/hosts/roles`, this.getHeaders())
      .map(mapDpwRoles)
      .catch(handleDpwRolesRetrieveError)

    return roles;
  }

  getDpwRole(roleId: string): Observable<DpwRoles> {
    const role = this.http
      .get(`${this.constantService.API_ENDPOINT}/hosts/role/${ roleId }`, this.getHeaders())
      .map(mapDpwRoles[0])
      .catch(handleDpwRolesRetrieveError)

    return role;
  }

  private getHeaders() {
    const headers = new Headers();
    headers.append('Accept', 'application/json');

    return headers;
  }
}

function mapDpwRoles(response: Response): DpwRoles[] {
  return response.json().results.map(toDpwRoles);
}

function toDpwRoles(r: any): DpwRoles {
  const dpwRole = <DpwRoles>({
    roleId: r.roleId,
    roleLabel: r.roleLabel
  });

  return dpwRole;
}

function handleDpwRolesRetrieveError(error: any) {
  const errDesc = error.message || 'No error description is available';
  const errorMsg = `An error occurred while retrieving list of DPW roles. Error is ${ errDesc }.`;
  return Observable.throw(errorMsg);
}
