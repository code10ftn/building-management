import { Question } from './question.model';
import { Tenant } from './user/tenant.model';

export class Survey {

    id: number;

    name: string;

    expirationDate: Date;

    questions: Array<Question> = new Array<Question>();

    answeredBy: Array<Tenant> = new Array<Tenant>();
}
