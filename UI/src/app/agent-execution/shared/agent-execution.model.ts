import {AgentExecutionDetails} from '../../agent-execution-details/shared/agent-execution-details.model';
import {MasterType} from '../../host-list/shared/host.model';

export class AgentExecution {
  agentExecId: number;
  command: string;
  agentId: number;
  masterType: MasterType;
  executionTimestamp: number;
  cleanStop: boolean;
  agentExecutionDetails: AgentExecutionDetails[];
}
