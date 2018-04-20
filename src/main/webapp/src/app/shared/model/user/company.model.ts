import { WorkArea } from '../work-area.model';
import { User } from './user.model';

export class Company extends User {

    name: string;

    address: string;

    description: string;

    workArea: WorkArea;
}
