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
    NotificationComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    HttpModule,
  ],
  providers: [AgentExecutionService],
  bootstrap: [AppComponent]
})
export class AppModule { }
