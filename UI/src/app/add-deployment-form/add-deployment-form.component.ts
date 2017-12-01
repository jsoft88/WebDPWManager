import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {HostService} from '../host.service';
import {DpwRoles} from '../dpwroles-list/shared/dpw-roles.model';
import {DeploymentsByRoles} from '../host-list/shared/host.model';
import {Subject} from 'rxjs/Subject';
import {ConstantService} from '../constant-service.service';
import {isUndefined} from 'util';

@Component({
  selector: 'app-add-deployment-form',
  templateUrl: './add-deployment-form.component.html',
  styles: []
})
export class AddDeploymentFormComponent implements OnInit, OnDestroy {

  availableRoles: DpwRoles[];

  @Input() parentDeploymentByRole: DeploymentsByRoles =  new DeploymentsByRoles();
  @Input() parentEventSubject: Subject<any>;
  @Output() deploymentChangeEmitter = new EventEmitter();

  actorSystems: String[];

  clustersRetrieveError = '';

  selectedActorSystem = '-1';

  addingActorSystem = false;

  emptyHostPortError = '';

  emptyActorNameError = '';

  emptyActorSystemNameError = '';

  parentEvent = false;

  constructor(private hostService: HostService, private constantService: ConstantService) {
    this.parentDeploymentByRole.port = undefined;
  }

  ngOnInit() {
    this.hostService.getClusterSystems().subscribe(
      actorSystems => {
        this.actorSystems = actorSystems;
        this.addingActorSystem = this.actorSystems.length === 0;
      },
      err => {
        this.clustersRetrieveError = err;
        this.addingActorSystem = true;
      });

    this.parentEventSubject.subscribe(event => {
      this.parentEvent = true;
      if (event === this.constantService.TRIGGER_FIELD_VERIFICATION) {
        if (isUndefined(this.parentDeploymentByRole.port)) {
          this.emptyHostPortError = 'Add port number that the actor will be listening at';
        }
        if (isUndefined(this.parentDeploymentByRole.actorName) || this.parentDeploymentByRole.actorName === '') {
          this.emptyActorNameError = 'Set a name for this actor';
        }
        if (isUndefined(this.parentDeploymentByRole.actorSystemName) || this.parentDeploymentByRole.actorSystemName === '') {
          this.emptyActorSystemNameError = 'Set a name for the actor system this actor belongs to';
        }
      }
    });
  }

  ngOnDestroy() {
    this.parentEventSubject.unsubscribe();
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
