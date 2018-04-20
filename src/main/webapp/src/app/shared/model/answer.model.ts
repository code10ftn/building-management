import { Tenant } from './user/tenant.model';

export class Answer {
    id: number;

    text: string;

    tenants: Array<Tenant> = new Array<Tenant>();
}
