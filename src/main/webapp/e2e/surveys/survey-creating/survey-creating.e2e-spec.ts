import * as moment from 'moment';
import { browser } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import { DatetimePickerPage } from '../../shared/datetime-picker/datetime-picker.po';
import SecurityUtil from '../../util/security-util';
import { SurveyListPage } from '../survey-list/survey-list.po';
import { SurveyCreatingPage } from './survey-creating.po';

describe('building-residential-requests', () => {

    let surveyCreatingPage: SurveyCreatingPage;
    let surveyListPage: SurveyListPage;
    let navPage: NavPage;
    let datetimePickerPage: DatetimePickerPage;

    beforeAll(() => {
        SecurityUtil.authenticate('upravnik', 'upravnik');
    });

    afterAll(() => {
        SecurityUtil.signout();
    });

    beforeEach(() => {
        surveyCreatingPage = new SurveyCreatingPage();
        surveyListPage = new SurveyListPage();
        navPage = new NavPage();
        datetimePickerPage = new DatetimePickerPage();

        navPage.getSurveysButton().click();
        surveyListPage.getNewSurveyButton().click();
    });

    it('should disable create survey button when name is invalid', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/surveys\/new');
        expect(surveyCreatingPage.getCreateSurveyButton().isEnabled()).toBeFalsy();

        const date = moment().add(1, 'months');
        datetimePickerPage.getDatepickerInput().clear();
        datetimePickerPage.getDatepickerInput().sendKeys(date.format('MMM D, YYYY'));

        expect(surveyCreatingPage.getCreateSurveyButton().isEnabled()).toBeFalsy();

        surveyCreatingPage.getSurveyNameInput().sendKeys('');

        surveyCreatingPage.getQuestionTextInput().sendKeys('First question text');

        surveyCreatingPage.getAddAnswerButton().click();
        surveyCreatingPage.getAddAnswerButton().click();

        expect(surveyCreatingPage.getCreateSurveyButton().isEnabled()).toBeFalsy();
    });

    it('should disable create survey button when question text length is invalid', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/surveys\/new');
        expect(surveyCreatingPage.getCreateSurveyButton().isEnabled()).toBeFalsy();

        const date = moment().add(1, 'months');
        datetimePickerPage.getDatepickerInput().clear();
        datetimePickerPage.getDatepickerInput().sendKeys(date.format('MMM D, YYYY'));

        expect(surveyCreatingPage.getCreateSurveyButton().isEnabled()).toBeFalsy();

        surveyCreatingPage.getSurveyNameInput().sendKeys('New Survey name');

        surveyCreatingPage.getQuestionTextInput().sendKeys('Short');

        surveyCreatingPage.getAddAnswerButton().click();
        surveyCreatingPage.getAddAnswerButton().click();

        expect(surveyCreatingPage.getCreateSurveyButton().isEnabled()).toBeFalsy();
    });

    it('should disable create survey button when question has less than 2 answers', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/surveys\/new');
        expect(surveyCreatingPage.getCreateSurveyButton().isEnabled()).toBeFalsy();

        const date = moment().add(1, 'months');
        datetimePickerPage.getDatepickerInput().clear();
        datetimePickerPage.getDatepickerInput().sendKeys(date.format('MMM D, YYYY'));

        expect(surveyCreatingPage.getCreateSurveyButton().isEnabled()).toBeFalsy();

        surveyCreatingPage.getSurveyNameInput().sendKeys('New Survey name');

        surveyCreatingPage.getQuestionTextInput().sendKeys('Long question text');

        surveyCreatingPage.getAddAnswerButton().click();

        expect(surveyCreatingPage.getCreateSurveyButton().isEnabled()).toBeFalsy();
    });

    it('should create survey when form is valid', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/surveys\/new');
        expect(surveyCreatingPage.getCreateSurveyButton().isEnabled()).toBeFalsy();

        const date = moment().add(1, 'months');
        datetimePickerPage.getDatepickerInput().clear();
        datetimePickerPage.getDatepickerInput().sendKeys(date.format('MMM D, YYYY'));

        expect(surveyCreatingPage.getCreateSurveyButton().isEnabled()).toBeFalsy();

        surveyCreatingPage.getSurveyNameInput().sendKeys('New Survey name');

        surveyCreatingPage.getQuestionTextInput().sendKeys('Long question text');

        surveyCreatingPage.getAddAnswerButton().click();
        surveyCreatingPage.getAddAnswerButton().click();

        expect(surveyCreatingPage.getCreateSurveyButton().isEnabled()).toBeTruthy();
        surveyCreatingPage.getCreateSurveyButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/surveys');
    });

});
