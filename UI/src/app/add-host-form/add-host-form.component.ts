import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DeploymentsByRoles, Host} from '../host-list/shared/host.model';
import {DpwRoles} from '../dpwroles-list/shared/dpw-roles.model';
import {NgForm} from '@angular/forms';
import {HostService} from '../host.service';

@Component({
  selector: 'app-add-host-form',
  templateUrl: './add-host-form.component.html',
  styles: []
})
export class AddHostFormComponent implements OnInit {

  moreOptionsClicked = false;
  selectedRole: DpwRoles;
  deployment: DeploymentsByRoles;
  hostAddError = '';
  addingHost = false;
  // this will tell the form to display the button that allows to deploy a role to the host being added.
  @Input() showMoreOptions;
  @Input() host: Host;
  @Output() hostEmitter = new EventEmitter();

  constructor(private hostService: HostService) {
    this.selectedRole = new DpwRoles();
    this.selectedRole.roleId = '';
    this.selectedRole.roleLabel = '';
    this.deployment = new DeploymentsByRoles();
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
        this.host.hostId = postResponse.hostId;
        this.hostEmitter.emit({'error': false, 'errorDescription': '', host: this.host});
      },
      error => this.hostEmitter.emit({'error': true, 'errorDescription': error, host: this.host}),
      () => this.addingHost = false
    );
  }
}
