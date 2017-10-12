import { Injectable } from '@angular/core';

@Injectable()
export class ConstantService {

  public API_ENDPOINT: string;

  constructor() {
    this.API_ENDPOINT = 'http://localhost:9000/api';
  }
}
