import { Component, OnInit } from '@angular/core';
import {DeploymentsByRoles, Host} from '../host-list/shared/host.model';
import {DeploymentByRoleService} from '../deployment-by-role-service.service';
import {HostService} from '../host.service';
import {ConstantService} from '../constant-service.service';

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

  constructor(
    private deploymentByRoleService: DeploymentByRoleService,
    private hostService: HostService,
    private constantsService: ConstantService) { }

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

  onHostSelectChange(host: Host) {
    this.selectedHost = host;
  }

  onRoleDeploy() {
    if (this.hosts.length === 0) {
      this.selectedHost = new Host();
      this.selectedHost.address = this.address;
    }
    this.selectedHost.deployments = new Array();
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
