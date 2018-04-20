import { Apartment } from '../apartment.model';
import { User } from './user.model';

export class Tenant extends User {

    firstName: string;

    lastName: string;

    apartment: Apartment;
}
