import { TestBed, inject } from '@angular/core/testing';

import { DpwrolesServiceService } from './dpwroles-service.service';

describe('DpwrolesServiceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DpwrolesServiceService]
    });
  });

  it('should be created', inject([DpwrolesServiceService], (service: DpwrolesServiceService) => {
    expect(service).toBeTruthy();
  }));
});
