import { browser } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { SurveyListPage } from '../survey-list/survey-list.po';
import { SurveyAnsweringPage } from './survey-answering.po';

describe('building-residential-requests', () => {

    let surveyAnsweringPage: SurveyAnsweringPage;
    let surveyListPage: SurveyListPage;
    let navPage: NavPage;

    beforeAll(() => {
        SecurityUtil.authenticate('upravnik', 'upravnik');
    });

    afterAll(() => {
        SecurityUtil.signout();
    });

    beforeEach(() => {
        surveyAnsweringPage = new SurveyAnsweringPage();
        surveyListPage = new SurveyListPage();
        navPage = new NavPage();

        navPage.getSurveysButton().click();
        surveyListPage.getAnswerSurveyButtons().first().click();
    });

    it('answer survey should be disabled when answer to question is not chosen', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/surveys\/[1-9]+\/answer');

        expect(surveyAnsweringPage.getAnswerSurveyButton().isEnabled()).toBeFalsy();
    });

    it('answer survey should be enabled when answer to question is chosen', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/surveys\/[1-9]+\/answer');

        surveyAnsweringPage.getAsnwerRadioButtons().first().click();

        expect(surveyAnsweringPage.getAnswerSurveyButton().isEnabled()).toBeTruthy();
    });

    it('should answer to survey when answers are chosen', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/surveys\/[1-9]+\/answer');

        surveyAnsweringPage.getAsnwerRadioButtons().first().click();
        surveyAnsweringPage.getAnswerSurveyButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/surveys');

    });
});
