import { Component, OnInit } from '@angular/core';
import { Host } from './shared/host.model';
import {HostService} from '../host.service';
import {ConstantService} from '../constant-service.service';
import {DpwRolesService} from '../dpwroles-service.service';
import {DpwRoles} from '../dpwroles-list/shared/dpw-roles.model';

@Component({
  selector: 'app-host-list',
  template: `
    <p>
      host-list Works!
    </p>
  `,
  styleUrls: ['./host-list.component.css']
})
export class HostListComponent implements OnInit {

  hosts: Host[];
  errorMessage = '';
  isLoading = true;
  apiEndpoint = '';
  availableRoles: DpwRoles[];

  constructor(private hostService: HostService, private constants: ConstantService, private dpwRolesService: DpwRolesService) {
    this.apiEndpoint = this.constants.API_ENDPOINT;
  }

  ngOnInit() {
    this.hostService.getAll().subscribe(
      hl => {
        this.retrieveRoleForHost(hl);

      },
      e => this.errorMessage = e,
      () => this.isLoading = false
    );

    this.dpwRolesService.getDpwRoles().subscribe(
      rl => this.availableRoles = rl,
      e => this.availableRoles = this.dummyDpwRole(e)
    )
  }

  private dummyDpwRole(errorMessage: string): DpwRoles[] {
    const dummyR = new DpwRoles();
    dummyR.roleLabel = errorMessage;
    dummyR.roleId = 'dummy';
    return new Array(dummyR);
  }

  private retrieveRoleForHost(hosts: Host[]) {
    const errRole = new DpwRoles();
    errRole.roleLabel = 'Failed to Retrieve role';
    errRole.roleId = 'invalid';

    for (const host of hosts) {
      this.dpwRolesService.getDpwRole().subscribe(
        role => host.role = role,
        e => host.role = errRole
      )
    }
  }

  private retrieveMastersRunningOnHost(hosts: Host[]) {
    this.
  }

}
