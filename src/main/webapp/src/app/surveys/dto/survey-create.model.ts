import { QuestionCreateDto } from './question-create.model';

export class SurveyCreateDto {

    name = '';

    questions = new Array<QuestionCreateDto>();

    expirationDate = new Date();

    constructor() {
        this.questions.push(new QuestionCreateDto());
    }
}
