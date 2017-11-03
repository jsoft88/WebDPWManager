import {Component, OnDestroy, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ConstantService} from '../constant-service.service';
import {Http} from '@angular/http';

@Component({
  selector: 'app-notification',
  template: './notification.component.html',
  styles: []
})
export class NotificationComponent implements OnInit, OnDestroy {

  message = '';

  sub;

  constructor(private constantService: ConstantService, private http: Http) { }

  ngOnInit() {
    this.sub = new Observable(observer => {
      this.http.get(`${ this.constantService.API_ENDPOINT }`, this.getHeaders())
        .map(response => response.json().message)
        .subscribe(
          message => this.message = message
        )
    });
  }

  private getHeaders(): Headers {
    const headers = new Headers();
    headers.append('Accept', 'application/json');

    return headers;
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
