import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {HostService} from '../host.service';
import {DpwRoles} from '../dpwroles-list/shared/dpw-roles.model';
import {DeploymentsByRoles} from '../host-list/shared/host.model';

@Component({
  selector: 'app-add-deployment-form',
  templateUrl: './add-deployment-form.component.html',
  styles: []
})
export class AddDeploymentFormComponent implements OnInit {

  availableRoles: DpwRoles[];

  @Input() parentDeploymentByRole: DeploymentsByRoles;
  @Output() deploymentChangeEmitter = new EventEmitter();

  actorSystems: String[];
  clustersRetrieveError = '';

  selectedActorSystem = '-1';

  addingActorSystem = false;

  constructor(private hostService: HostService) { }

  ngOnInit() {
    this.hostService.getClusterSystems().subscribe(
      actorSystems => this.actorSystems = actorSystems,
      err => this.clustersRetrieveError = err);
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

  onActorSystemSelected(actorSystemName: string) {
    this.addingActorSystem = (actorSystemName === '-1');
    this.parentDeploymentByRole.actorSystemName = actorSystemName;
    this.onDeploymentChange();
  }
}
