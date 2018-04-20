import { Building } from './building.model';
import { Repairment } from './repairment.model';
import { Tenant } from './user/tenant.model';
import { User } from './user/user.model';
import { WorkArea } from './work-area.model';

export class Malfunction {

    id: number;

    description: string;

    reportDate: Date;

    workArea: WorkArea;

    creator: User | Tenant;

    assignee: User;

    repairment: Repairment;

    building: Building;
}
