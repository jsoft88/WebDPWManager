import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router'
import {RequiredDeploymentsComponent} from './required-deployments/required-deployments.component';
import {AddAgentExecutionComponent} from './add-agent-execution/add-agent-execution.component';
import {AgentExecutionDetailsComponent} from './agent-execution-details/agent-execution-details.component';
import {AgentExecutionComponent} from './agent-execution/agent-execution.component';
import {HostListComponent} from './host-list/host-list.component';
import {AppComponent} from './app.component';

const routes: Routes = [
  {
    path: 'home',
    component: HostListComponent
  },

  {
    path: 'hosts',
    component: HostListComponent
  },

  {
    path: 'execs/:deployId',
    component: AgentExecutionComponent
  },

  {
    path: 'execs/details/:agentExecId',
    component: AgentExecutionDetailsComponent
  },

  {
    path: 'masters/details/:masterTypeId/:deployId',
    component: AgentExecutionDetailsComponent
  },

  {
    path: 'masters/add/:hostId',
    component: AddAgentExecutionComponent
  },

  {
    path: 'initial',
    component: RequiredDeploymentsComponent
  },

  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }
