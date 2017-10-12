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

@NgModule({
  declarations: [
    AppComponent,
    AgentExecutionComponent,
    HostListComponent,
    DpwRolesListComponent,
    AgentExecutionDetailsComponent
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
