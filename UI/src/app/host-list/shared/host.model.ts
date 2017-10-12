import {DpwRoles} from '../../dpwroles-list/shared/dpw-roles.model';

export class Host {
  hostId: number;
  address: string;
  port: number;
  name: string;
  agentId: number;
  masters: MasterType[];
  role: DpwRoles;
}

export class MasterType {
  masterTypeId: number;
  masterLabel: string;
}

export class MasterField {
  fieldId: number;
  fieldName: string;
  fieldDescription: string;
}
