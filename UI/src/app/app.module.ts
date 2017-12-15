import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http'
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { AgentExecutionComponent } from './agent-execution/agent-execution.component';
import { AgentExecutionService } from './agent-execution.service';
import { HostListComponent } from './host-list/host-list.component';
import { DpwRolesListComponent } from './dpwroles-list/dpwroles-list.component';
import { AgentExecutionDetailsComponent } from './agent-execution-details/agent-execution-details.component';
import { AddHostFormComponent } from './add-host-form/add-host-form.component';
import { AddDeploymentFormComponent } from './add-deployment-form/add-deployment-form.component';
import { DeploymentByRoleComponent } from './deployment-by-role/deployment-by-role.component';
import { NotificationComponent } from './notification/notification.component';
import { AddAgentExecutionComponent } from './add-agent-execution/add-agent-execution.component';
import {ConstantService} from './constant-service.service';
import {AgentExecutionDetailsService} from './agent-execution-details.service';
import {DeploymentByRoleService} from './deployment-by-role-service.service';
import {DpwRolesService} from './dpwroles-service.service';
import {HostService} from './host.service';
import {MasterService} from './master.service';
import { RequiredDeploymentsComponent } from './required-deployments/required-deployments.component';
import { AddMasterClusterComponent } from './add-master-cluster/add-master-cluster.component';

@NgModule({
  declarations: [
    AppComponent,
    AgentExecutionComponent,
    HostListComponent,
    DpwRolesListComponent,
    AgentExecutionDetailsComponent,
    AddHostFormComponent,
    AddDeploymentFormComponent,
    DeploymentByRoleComponent,
    NotificationComponent,
    AddAgentExecutionComponent,
    RequiredDeploymentsComponent,
    AddMasterClusterComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    HttpModule,
  ],
  providers: [
    AgentExecutionService,
    ConstantService,
    AgentExecutionDetailsService,
    DeploymentByRoleService,
    DpwRolesService,
    HostService,
    MasterService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
