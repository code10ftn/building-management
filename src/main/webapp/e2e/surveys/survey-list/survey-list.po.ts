import { by, element, ElementArrayFinder } from 'protractor';

export class SurveyListPage {

    getRows(): ElementArrayFinder {
        return element.all(by.css('.datatable-body-row'));
    }

    getNewSurveyButton() {
        return element(by.id('newSurveyButton'));
    }

    getAnswerSurveyButtons() {
        return element.all(by.css('.answerSurveyButton'));
    }
}
