import { Apartment } from './apartment.model';
import { Tenant } from './user/tenant.model';

export class ResidentialRequest {

    id: number;

    tenant: Tenant;

    apartment: Apartment = new Apartment();
}
