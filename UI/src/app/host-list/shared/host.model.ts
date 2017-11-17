import {DpwRoles} from '../../dpwroles-list/shared/dpw-roles.model';
import {AgentExecution} from '../../agent-execution/shared/agent-execution.model';

export class Host {
  hostId: number;
  address: string;
  deployments: DeploymentsByRoles[];
  executions: AgentExecution[];
}

export class MasterType {
  masterTypeId: number;
  masterLabel: string;
}

export class MasterField {
  fieldId: number;
  fieldName: string;
  fieldDescription: string;
  fieldEnabled: boolean;
}

export class DeploymentsByRoles {
  deployId: number;
  actorSystemName: string;
  actorName: string;
  port: number;
  componentId: number;
  role: DpwRoles;
}
