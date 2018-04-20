import { Report } from './report.model';
import { Topic } from './topic.model';

export class Meeting {

    id: number;

    date: Date = new Date();

    topics = new Array<Topic>();

    report: Report;
}
