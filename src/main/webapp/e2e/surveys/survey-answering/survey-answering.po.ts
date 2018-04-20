import { by, element } from 'protractor';

export class SurveyAnsweringPage {

    getAsnwerRadioButtons() {
        return element.all(by.css('.answer-radio'));
    }

    getAnswerSurveyButton() {
        return element(by.id('answerSurveyButton'));
    }
}
