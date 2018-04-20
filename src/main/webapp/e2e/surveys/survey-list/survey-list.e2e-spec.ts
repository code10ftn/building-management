import { browser, by } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { SurveyListPage } from './survey-list.po';

describe('survey-list', () => {

    let surveyListPage: SurveyListPage;
    let navPage: NavPage;

    beforeAll(() => {
        SecurityUtil.authenticate('upravnik', 'upravnik');
    });

    afterAll(() => {
        SecurityUtil.signout();
    });

    beforeEach(() => {
        surveyListPage = new SurveyListPage();
        navPage = new NavPage();
        navPage.getSurveysButton().click();
    });

    it('should display surveys', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/surveys');
        expect(surveyListPage.getRows().count()).toBeGreaterThan(0);
    });

    it('should not display new survey button when user is not supervisor', () => {
        SecurityUtil.signout();
        SecurityUtil.authenticate('nemanja', 'nemanja');
        navPage.getSurveysButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/surveys');
        expect(browser.isElementPresent(by.id('newSurveyButton'))).toBeFalsy();

    });

    it('should display answer button when user is not tenant', () => {
        SecurityUtil.signout();
        SecurityUtil.authenticate('nemanja', 'nemanja');
        navPage.getSurveysButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/surveys');
        expect(browser.isElementPresent(by.css('.answerSurveyButton'))).toBeTruthy();

        SecurityUtil.signout();
    });
});
