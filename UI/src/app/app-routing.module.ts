import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router'

import { MasterTypesComponent } from './master-types/master-types.component'

const routes: Routes = [
  {
    path: 'master-types',
    component: MasterTypesComponent,
  },
  {
    path: '',
    redirectTo: '/',
    pathMatch: 'full'
  }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }
