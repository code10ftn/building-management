import { Malfunction } from './malfunction.model';
import { Survey } from './survey.model';

export class Report {

    malfunctions = new Array<Malfunction>();

    surveys = new Array<Survey>();

    comment: string;
}
