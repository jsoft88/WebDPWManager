import { Injectable } from '@angular/core';
import {Host} from './host-list/shared/host.model';
import {Observable} from 'rxjs/Observable';
import {Http, Response, Headers} from '@angular/http'
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';

@Injectable()
export class HostService {

  // private baseUrl = 'http://localhost:9000/api';

  constructor(private http: Http, private baseUrl: string) { }

  getAll(): Observable<Host[]> {
    const hosts = this.http.get(`${this.baseUrl}/hosts`, { headers: this.getHeaders() }).map(mapHosts).catch(handleHostListRetrieveError);

    return hosts;
  }

  private getHeaders() {
    const headers = new Headers();
    headers.append('Accept', 'application/json');

    return headers;
  }
}

function mapHosts(response: Response): Host[]  {
  return response.json().results.map(toHost);
}

function toHost(r: any): Host {
  const host = <Host>({
    name: r.name,
    address: r.address,
    port: Number.parseInt(r.port)
  });

  return host;
}

function handleHostListRetrieveError(error: any) {
  const errDesc = error.message || 'No error description is available'
  const errorMsg = `An error occurred while retrieving hosts. Description: ${ errDesc }.`;

  return Observable.throw(errorMsg);
}
