import { Component, OnInit } from '@angular/core';
import {Host} from '../host-list/shared/host.model';
import {DpwRoles} from '../dpwroles-list/shared/dpw-roles.model';

@Component({
  selector: 'app-add-host-form',
  template: `
    <p>
      add-host-form Works!
    </p>
  `,
  styles: []
})
export class AddHostFormComponent implements OnInit {

  host: Host;
  availableRoles: DpwRoles[];
  moreOptionsClicked = false;

  constructor() { }

  ngOnInit() {
  }

}
