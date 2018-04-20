import { Apartment } from './apartment.model';
import { Company } from './user/company.model';
import { Tenant } from './user/tenant.model';
import { User } from './user/user.model';

export class Building {

    id: number;

    address: string;

    supervisor = new User();

    apartments = new Array<Apartment>();

    companies: Company[];

    tenants: Tenant[];
}
