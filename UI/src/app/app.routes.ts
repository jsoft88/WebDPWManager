import {RouterModule, Routes} from '@angular/router';
import { HostListComponent} from './host-list/host-list.component';
import { AgentExecutionComponent } from './agent-execution/agent-execution.component';
import {AgentExecutionDetailsComponent} from './agent-execution-details/agent-execution-details.component';
import {AddAgentExecutionComponent} from './add-agent-execution/add-agent-execution.component';
import {AppComponent} from './app.component';
import {RequiredDeploymentsComponent} from './required-deployments/required-deployments.component';

const routes: Routes = [
  {
    path: 'home',
    component: AppComponent
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
];

export const appRouterModule = RouterModule.forRoot(routes);
