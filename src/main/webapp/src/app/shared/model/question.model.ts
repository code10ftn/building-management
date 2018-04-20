import { Answer } from './answer.model';

export class Question {

    text: string;

    answers: Array<Answer> = new Array<Answer>();
}
