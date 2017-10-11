import {RouterModule, Routes} from '@angular/router';
import { HostListComponent} from './host-list/host-list.component';
import { AgentExecutionComponent } from './agent-execution/agent-execution.component';
import {AgentExecutionDetailsComponent} from './agent-execution-details/agent-execution-details.component';

const routes: Routes = [
  {
    path: 'hosts',
    component: HostListComponent
  },

  {
    path: 'execs',
    component: AgentExecutionComponent
  },

  {
    path: 'execs/details/:agentExecId',
    component: AgentExecutionDetailsComponent
  },

  {
    path: '',
    redirectTo: '/hosts',
    pathMatch: 'full'
  }
];

export const appRouterModule = RouterModule.forRoot(routes);
