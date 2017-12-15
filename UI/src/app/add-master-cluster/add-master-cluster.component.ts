import { Component, OnInit } from '@angular/core';
import {DeploymentsByRoles, Host, MasterType} from '../host-list/shared/host.model';
import {DeploymentByRoleService} from '../deployment-by-role-service.service';
import {HostService} from '../host.service';
import {MasterService} from '../master.service';

@Component({
  selector: 'app-add-master-cluster',
  templateUrl: './add-master-cluster.component.html',
  styles: []
})
export class AddMasterClusterComponent implements OnInit {

  // actor systems names stored as deployments by roles.
  availableDeploymentsByRole: DeploymentsByRoles[];

  selectedDeploymentByRole: DeploymentsByRoles;

  availableHosts: Host[];

  selectedHost: Host;

  availableMasterTypes: MasterType[];

  selectedMasterType: MasterType;

  retrieveDeploymentsError = '';

  retrieveHostsError = '';

  retrieveMasterTypesError = '';

  constructor(private deploymentsByRoleService: DeploymentByRoleService, private hostService: HostService, private masterTypeService: MasterService) {
    this.availableDeploymentsByRole = new Array();
    this.availableHosts = new Array();
    this.availableMasterTypes = new Array();
  }

  ngOnInit() {
    this.deploymentsByRoleService.getActorSystems().subscribe(
      deployments => deployments.forEach(d => this.availableDeploymentsByRole.push(d)),
      err => this.retrieveDeploymentsError = err
    );
  }

  onActorSystemSelected(deploymentByRole: DeploymentsByRoles) {
    this.selectedDeploymentByRole = deploymentByRole;
    this.hostService.getHostsInCluster(this.selectedDeploymentByRole.actorSystemName).subscribe(
      hosts => hosts.filter(h => h.)
    );
  }
}
