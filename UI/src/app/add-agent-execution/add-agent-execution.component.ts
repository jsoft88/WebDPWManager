import {Component, OnDestroy, OnInit} from '@angular/core';
import {DeploymentsByRoles, Host, MasterType} from '../host-list/shared/host.model';
import {AgentExecution} from '../agent-execution/shared/agent-execution.model';
import {AgentExecutionDetails} from '../agent-execution-details/shared/agent-execution-details.model';
import {DeploymentByRoleService} from '../deployment-by-role-service.service';
import {isUndefined} from 'util';
import {MasterService} from '../master.service';
import {HostService} from '../host.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-add-agent-execution',
  template: './add-agent-execution.component.html',
  styles: []
})
export class AddAgentExecutionComponent implements OnInit, OnDestroy {

  deploymentByRole: DeploymentsByRoles = undefined;

  host: Host = undefined;

  hosts: Host[];

  masterType: MasterType;

  masterTypes: MasterType[];

  deploymentsInHost: DeploymentsByRoles[];

  agentExecution: AgentExecution;

  agentExecutionDetails: AgentExecutionDetails[];

  addHostError = '';

  addDeploymentError = '';

  masterTypesRetrieveError = '';

  hostsRetrieveError = '';

  deploymentsInHostError = '';

  addExecutionError = '';

  success = false;

  sub: any;

  constructor(
    private deploymentByRoleService: DeploymentByRoleService,
    private masterService: MasterService,
    private hostService: HostService,
    private route: ActivatedRoute) { }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  ngOnInit() {

    this.sub = this.route.params.subscribe(
      params => {
        const hostId = Number.parseInt(params['hostId']);
        this.masterService.getMasterTypes().subscribe(
          masterTypes => this.masterTypes = masterTypes,
          err => this.masterTypesRetrieveError = err
        );

        this.hostService.getAll().subscribe(
          hosts => {
            this.hosts = hosts;
            this.host = this.hosts.filter(h => h.hostId === hostId)[0];
          },
            err => this.hostsRetrieveError = err
        );
      }
    );
  }

  onHostChange(host: Host) {
    this.host = host;
    this.deploymentsInHost = this.host.deployments;
  }

  onDeploymentChange(deployment: DeploymentsByRoles) {
    this.deploymentByRole = deployment;
  }

  onMasterTypeChange(masterType: MasterType) {
    this.masterType = masterType;
  }

  onNewHostAdd(response) {
    if (response.error) {
      this.addHostError = response.errorDescription;
    } else {
      this.host = response.host;
    }
  }

  onDeploymentAdd() {
    if (isUndefined(this.host)) {
      this.addDeploymentError = 'No host has been set for the added deployment.';
    } else {
      this.host.deployments.push(this.deploymentByRole);
      this.deploymentByRoleService.addDeployment(this.host).subscribe(
        hostResponse => this.host = hostResponse,
        error => this.addDeploymentError = error
      );
    }
  }

  onExecutionDetailsSaved(response: any) {
    if (response.error) {
      this.addExecutionError = response.errorDescription;
    } else {
      this.agentExecution = response.agentExecution;
      this.success = true;
    }
  }
}
