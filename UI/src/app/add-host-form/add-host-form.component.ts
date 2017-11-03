import { Component, OnInit } from '@angular/core';
import {DeploymentsByRoles, Host} from '../host-list/shared/host.model';
import {DpwRoles} from '../dpwroles-list/shared/dpw-roles.model';
import {NgForm} from '@angular/forms';
import {HostService} from '../host.service';

@Component({
  selector: 'app-add-host-form',
  template: './add-host-form.component.html',
  styles: []
})
export class AddHostFormComponent implements OnInit {

  host: Host;

  moreOptionsClicked = false;
  selectedRole: DpwRoles;
  deployment: DeploymentsByRoles;
  hostAddError = '';
  addingHost = false;

  constructor(private hostService: HostService) {
    this.selectedRole = new DpwRoles();
    this.selectedRole.roleId = '';
    this.selectedRole.roleLabel = '';
  }

  ngOnInit() {
  }

  onRoleSelected(role: DpwRoles) {

  }

  onMoreOptionsClick() {
    this.moreOptionsClicked = true;
  }

  addHostSubmit(formData: NgForm) {
    this.addingHost = true;

    this.deployment.deployId = 0;
    this.deployment.role = this.selectedRole;
    this.deployment.componentId = 0;

    this.host.deployments = new Array(this.deployment);
    this.host.hostId = 0;

    this.hostService.addHost(this.host).subscribe(
      postResponse => {
        this.host.hostId = postResponse.hostId
      },
      error => this.hostAddError = error,
      () => this.addingHost = false
    );
  }
}
