import { Injectable } from '@angular/core';

@Injectable()
export class ConstantService {

  public API_ENDPOINT: string;

  public LACK_OF_ROLE_ERROR_RANGE: number[];

  public ERROR_LACK_OF_ROLE = 'lckrole';

  public ERROR_OTHER = 'other';

  public TRIGGER_FIELD_VERIFICATION = 'field_verif';

  constructor() {
    this.API_ENDPOINT = 'http://localhost:9000/api';
    this.LACK_OF_ROLE_ERROR_RANGE = new Array();
    this.LACK_OF_ROLE_ERROR_RANGE.push(100, 200, 300, 400);
  }
}
