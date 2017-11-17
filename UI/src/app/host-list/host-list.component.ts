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
  selectedAgentExecution: AgentExecution;
  modalForHost: Host;
  deploys: DeploymentsByRoles[];
  actorSystems: String[];
  clustersRetrieveError = '';
  roleDeploySelectedCluster = '';
  deploymentByRole: DeploymentsByRoles;
  deployNewRoleError = '';
  addHostError = '';

  constructor(
    private hostService: HostService,
    private constants: ConstantService,
    private dpwRolesService: DpwRolesService,
    private deploymentsByRoleService: DeploymentByRoleService,
    private executionService: AgentExecutionService,
    private masterService: MasterService,
    private router: Router) {

    this.apiEndpoint = this.constants.API_ENDPOINT;
    }

  ngOnInit() {
    this.getAllHosts();
  }

  private getAllHosts() {
    this.hostService.getAll().subscribe(
      hl => {
        this.retrieveDeployments(hl);
      },
      e => this.errorMessage = e,
      () => this.isLoading = false
    );
  }

  private dummyDpwRole(errorMessage: string): DpwRoles[] {
    const dummyR = new DpwRoles();
    dummyR.roleLabel = errorMessage;
    dummyR.roleId = 'dummy';

    return new Array(dummyR);
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
    const deployments: DeploymentsByRoles[] = new Array();
    deployments.push(this.deploymentByRole);

    this.modalForHost.deployments = deployments;
    this.deploymentsByRoleService.addDeployment(this.modalForHost).subscribe(
      response => {
        let deployment: DeploymentsByRoles = null;
        this.hosts
          .filter(h => h.hostId === response.hostId)
          .forEach(singleHost => deployment = singleHost.deployments.find(d => d.port === this.deploymentByRole.port));

        if (deployment !== null) {
          this.deploymentByRole.deployId = deployment.deployId;
          this.hosts.filter(h => h.hostId === this.modalForHost.hostId).forEach(h => h.deployments.push(this.deploymentByRole));
        }
      },
      err => this.deployNewRoleError = err
    );
  }

  onActorSystemSelected(actorSystemName: String) {
    if (actorSystemName === '-1') {
      this.getAllHosts();
      return;
    }

    this.hostService.getHostsInCluster(actorSystemName).subscribe(
      hosts => this.retrieveDeployments(hosts),
      err => {
        this.errorMessage = `An error occurred while filtering by cluster: ${ err }`;
        this.hosts = [];
      }
    );
  }

  onAddHostFormSubmit(response) {
    if (response.error) {
      this.addHostError = response.errorDescription;
    } else {
      this.hosts.push(response.host);
    }
  }
}
