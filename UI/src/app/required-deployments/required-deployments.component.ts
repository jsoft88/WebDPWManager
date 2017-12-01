import { Component, OnInit } from '@angular/core';
import {DeploymentsByRoles, Host, MasterField, MasterType} from '../host-list/shared/host.model';
import {DeploymentByRoleService} from '../deployment-by-role-service.service';
import {HostService} from '../host.service';
import {ConstantService} from '../constant-service.service';
import {Subject} from 'rxjs/Subject';
import {AgentExecution} from '../agent-execution/shared/agent-execution.model';
import {DpwRoles} from '../dpwroles-list/shared/dpw-roles.model';
import {AgentExecutionDetails} from '../agent-execution-details/shared/agent-execution-details.model';

@Component({
  selector: 'app-required-deployments',
  templateUrl: './require-deployments.component.html',
  styles: []
})
export class RequiredDeploymentsComponent implements OnInit {

  deploymentByRole: DeploymentsByRoles;

  hosts: Host[];

  errorRetrievingHosts = '';

  address = '';

  selectedHost: Host;

  errorDeployingRole: string;

  addressError = '';

  parentEventSubject: Subject<any> = new Subject();

  constructor(
    private deploymentByRoleService: DeploymentByRoleService,
    private hostService: HostService,
    private constantsService: ConstantService) {

    this.deploymentByRole = new DeploymentsByRoles();
  }

  ngOnInit() {
    this.hostService.getAll().subscribe(
      hosts => this.hosts = hosts,
      err => {
        if (err.type === this.constantsService.ERROR_LACK_OF_ROLE) {
          this.hosts = new Array();
        } else if (err.type === this.constantsService.ERROR_OTHER) {
          this.errorRetrievingHosts = err;
        }
      }
    );
  }

  onDeploymentChange(event) {
    this.deploymentByRole = event;
  }

  onHostSelectChange(host: Host) {
    this.selectedHost = host;
  }

  onRoleDeploy() {
    this.parentEventSubject.next(this.constantsService.TRIGGER_FIELD_VERIFICATION);

    const ipPattern = /\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/i;

    if (this.hosts.length === 0) {
      if (this.address === '') {
        this.addressError = 'Please enter the ip address where persistence role will be deployed';
        return;
      }

      if (!ipPattern.test(this.address)) {
        this.addressError = 'Invalid ip address format';
        return;
      }

      this.selectedHost = new Host();
      this.selectedHost.address = this.address;
      this.selectedHost.hostId = 0;
      this.selectedHost.executions = new Array();

      const dummyExecution = new AgentExecution();
      dummyExecution.deployId = 0;
      dummyExecution.agentExecId = 0;
      dummyExecution.command = 'no_command';
      dummyExecution.executionTimestamp = 0;
      dummyExecution.cleanStop = false;

      const dummyMasterType = new MasterType();
      dummyMasterType.masterTypeId = 0;
      dummyMasterType.masterLabel = 'dummy';

      dummyExecution.masterType = dummyMasterType;

      dummyExecution.agentExecutionDetails = new Array();

      const dummyExecDetails = new AgentExecutionDetails();

      const dummyMasterField = new MasterField();
      dummyMasterField.fieldId = 0;
      dummyMasterField.fieldEnabled = false;
      dummyMasterField.fieldDescription = 'dummy';
      dummyMasterField.fieldName = 'dummy';

      dummyExecDetails.field = dummyMasterField;
      dummyExecDetails.value = 'no_value';

      dummyExecution.agentExecutionDetails.push(dummyExecDetails);
      this.selectedHost.executions.push(dummyExecution);
    }

    this.selectedHost.deployments = new Array();
    this.deploymentByRole.componentId = 0;
    this.deploymentByRole.role = new DpwRoles();
    this.deploymentByRole.role.roleId = 'persistence';
    this.deploymentByRole.role.roleLabel = 'dummy';
    this.deploymentByRole.role.roleDescription = 'dummy';
    this.selectedHost.deployments.push(this.deploymentByRole);

    this.deploymentByRoleService.addDeployment(this.selectedHost).subscribe(
      host => {
        this.hostService.addHost(host).subscribe(
          res => this.selectedHost.hostId = res.hostId,
          err => {
            return;
          }
        );
      },
      err => this.errorDeployingRole = err
    );
  }
}
