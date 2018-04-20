import { Building } from './building.model';

export class Apartment {

    id: number;

    number: number;

    building: Building = new Building();
}
