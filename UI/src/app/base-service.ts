import {isUndefined} from 'util';

export class BaseService {
  analyzeIfResponseIsLackOfRole(jsonObj: any, errorCodes: number[]): boolean {
    return (!isUndefined(jsonObj.errorCode) &&
      errorCodes.filter(el => jsonObj.errorCode === el).length > 0);
  }
}
