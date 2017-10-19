import {Component, OnDestroy, OnInit} from '@angular/core';
import { AgentExecution } from './shared/agent-execution.model';
import { AgentExecutionService } from '../agent-execution.service';
import {MasterType} from '../host-list/shared/host.model';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-agent-execution',
  template: './agent-execution.component.html',
  styles: []
})
export class AgentExecutionComponent implements OnInit, OnDestroy {

  agentExecutions: AgentExecution[] = [];

  errorMessage = '';

  sub: any;

  constructor(
    private agentExecutionService: AgentExecutionService,
    private activatedRoute: ActivatedRoute,
    private route: Router) { }

  ngOnInit() {
    this.sub = this.activatedRoute.params.subscribe(
      params => {
        const deployId = params['deployId'];
        this.agentExecutionService.getMastersAgentExecution(deployId).subscribe(
          execs => this.agentExecutions = execs,
          err => this.errorMessage = err
        );
      }
    );
  }
  clickMasterType(masterType: MasterType) {

  }

  clickAgentExecutionDetails(agentExecution: AgentExecution) {
    this.route.navigateByUrl(`/execs/details/${ agentExecution.agentExecId }`);
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
