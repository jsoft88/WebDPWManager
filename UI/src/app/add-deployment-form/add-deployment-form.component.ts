import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {HostService} from '../host.service';
import {DpwRoles} from '../dpwroles-list/shared/dpw-roles.model';
import {DeploymentsByRoles} from '../host-list/shared/host.model';

@Component({
  selector: 'app-add-deployment-form',
  template: './add-deployment-form.component.html',
  styles: []
})
export class AddDeploymentFormComponent implements OnInit {

  availableRoles: DpwRoles[];

  @Input() parentDeploymentByRole: DeploymentsByRoles;
  @Output() deploymentChangeEmitter = new EventEmitter();

  constructor(private hostService: HostService) { }

  ngOnInit() {
  }

  onDeploymentChange() {
    this.deploymentChangeEmitter.emit(this.parentDeploymentByRole);
  }

  onRoleSelected(role: DpwRoles) {
    if (this.parentDeploymentByRole.role.roleId === role.roleId) {
      this.parentDeploymentByRole.role.roleId = '';
      this.parentDeploymentByRole.role.roleLabel = '';
    } else {
      this.parentDeploymentByRole.role = role;
    }
    this.deploymentChangeEmitter.emit(this.parentDeploymentByRole);
  }
}
