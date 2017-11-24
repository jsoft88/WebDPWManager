import {Component, Input, OnInit} from '@angular/core';
import {DeploymentsByRoles, Host} from '../host-list/shared/host.model';
import {DeploymentByRoleService} from '../deployment-by-role-service.service';
import {NgForm} from '@angular/forms';
import {HostService} from '../host.service';

@Component({
  selector: 'app-deployment-by-role',
  templateUrl: './deployment-by-role.component.html',
  styles: []
})
export class DeploymentByRoleComponent implements OnInit {

  @Input() deploymentByRole: DeploymentsByRoles;

  hostList: Host[];

  errorHostListRetrieve = '';

  host: Host;

  invalidHost: Host;

  deployedSuccessfully = false;

  submitted = false;

  deploymentError = '';

  constructor(private deploymentByRoleService: DeploymentByRoleService, private hostService: HostService) { }

  ngOnInit() {
    this.invalidHost = new Host();
    this.invalidHost.hostId = 0;
    this.invalidHost.address = '';
    this.invalidHost.deployments = [];
    this.invalidHost.executions = [];

    this.hostService.getAll().subscribe(
      hosts => this.hostList = hosts,
      error => this.errorHostListRetrieve = error
    );
  }

  addDeploymentSubmit(formData: NgForm) {
    this.deploymentByRoleService.addDeployment(this.host).subscribe(
      response => this.host = response,
      error => this.deploymentError = error
    );
  }
}
