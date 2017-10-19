import { TestBed, inject } from '@angular/core/testing';

import { DeploymentByRoleServiceService } from './deployment-by-role-service.service';

describe('DeploymentByRoleServiceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DeploymentByRoleServiceService]
    });
  });

  it('should be created', inject([DeploymentByRoleServiceService], (service: DeploymentByRoleServiceService) => {
    expect(service).toBeTruthy();
  }));
});
