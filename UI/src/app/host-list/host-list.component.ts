import { Component, OnInit } from '@angular/core';
import {DeploymentsByRoles, Host, MasterType} from './shared/host.model';
import {HostService} from '../host.service';
import {ConstantService} from '../constant-service.service';
import {DpwRolesService} from '../dpwroles-service.service';
import {DpwRoles} from '../dpwroles-list/shared/dpw-roles.model';
import {DeploymentByRoleService} from '../deployment-by-role-service.service';
import {AgentExecutionService} from '../agent-execution.service';
import {MasterService} from '../master.service';
import {AgentExecution} from '../agent-execution/shared/agent-execution.model';
import {Router} from '@angular/router';

@Component({
  selector: 'app-host-list',
  template: './host-list.component.html',
  styleUrls: ['./host-list.component.css']
})
export class HostListComponent implements OnInit {

  hosts: Host[];
  errorMessage = '';
  isLoading = true;
  apiEndpoint = '';
  availableRoles: DpwRoles[];
  selectedRoleToAssign: DpwRoles;
  selectedIndex: number;
  selectedAgentExecution: AgentExecution;
  modalForHost: Host;
  deploys: DeploymentsByRoles[];

  constructor(
    private hostService: HostService,
    private constants: ConstantService,
    private dpwRolesService: DpwRolesService,
    private deploymentsByRoleService: DeploymentByRoleService,
    private executionService: AgentExecutionService,
    private masterService: MasterService,
    private router: Router) {

    this.apiEndpoint = this.constants.API_ENDPOINT;
    this.selectedIndex = -1;
  }

  ngOnInit() {
    this.hostService.getAll().subscribe(
      hl => {
        this.retrieveDeployments(hl);
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

  modalRoleSelected(role: DpwRoles, index: number) {
    this.selectedRoleToAssign = role;
    if (this.selectedIndex === index) {
      this.selectedIndex = -1;
    } else {
      this.selectedIndex = index;
    }
  }

  private retrieveDeployments(hosts: Host[]) {
    const deploymentDetailErr = new DeploymentsByRoles();

    deploymentDetailErr.deployId = 0;
    deploymentDetailErr.actorName = 'unavailable';
    deploymentDetailErr.actorSystemName = 'unavailable';
    deploymentDetailErr.role = new DpwRoles();
    deploymentDetailErr.componentId = 0;

    for (const host of hosts) {
      host.deployments.forEach(d => this.deploymentsByRoleService.getDeploymentDetails(d.deployId).subscribe(
        dpd => {
          this.dpwRolesService.getDpwRole(dpd.role.roleId).subscribe(
            role => dpd.role = role,
            err => dpd.role = this.dummyDpwRole(err)[0],
            () => host.deployments.push(dpd)
          );
          this.executionService.getMastersAgentExecution(dpd.deployId).subscribe(
            execs => {
              execs.forEach( exec =>
                this.masterService.getMasterType(exec.masterType.masterTypeId).subscribe(
                  master => exec.masterType = master,
                  err => {
                    const masterErr = new MasterType();
                    masterErr.masterTypeId = 0;
                    masterErr.masterLabel = 'Not found';

                    exec.masterType = masterErr
                  }
                )
              )
            }
          )
        },
        err => host.deployments.push(deploymentDetailErr)
      ));
    }
  }

  onExecutionClick(execution: AgentExecution) {
    this.selectedAgentExecution = execution;
  }

  onDeploymentClick(deployment: DeploymentsByRoles) {
    this.router.navigateByUrl(`/execs/${ deployment.deployId }`);
  }

  onNewRoleClick(host: Host) {
    this.modalForHost = host;
  }

  onDeployRoleClicked() {
    this.modalForHost.deployments.push()
    this.availableRoles[this.selectedIndex]
  }
}
