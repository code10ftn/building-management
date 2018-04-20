import { by, element } from 'protractor';

export class SurveyCreatingPage {

    getCreateSurveyButton() {
        return element(by.id('createSurveyButton'));
    }

    getSurveyNameInput() {
        return element(by.css('input[name="surveyName"]'));
    }

    getQuestionTextInput() {
        return element(by.css('textarea[name="questionText"]'));
    }

    getAddAnswerButton() {
        return element(by.id('addAnswerButton'));
    }
}
