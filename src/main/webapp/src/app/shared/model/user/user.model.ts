import { Building } from '../building.model';

export class User {

    id: number;

    username: string;

    password: string;

    email: string;

    phoneNumber: string;

    supervisingBuildings: Building[];
}
