import * as moment from 'moment';
import { browser } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import { DatetimePickerPage } from '../../shared/datetime-picker/datetime-picker.po';
import SecurityUtil from '../../util/security-util';
import { MeetingListPage } from '../meeting-list/meeting-list.po';
import { NewMeetingPage } from './new-meeting.po';

describe('new-meeting', () => {

    let navPage: NavPage;
    let meetingListPage: MeetingListPage;
    let newMeetingPage: NewMeetingPage;
    let datetimePickerPage: DatetimePickerPage;

    beforeAll(() => {
        SecurityUtil.authenticate('upravnik', 'upravnik');
    });

    afterAll(() => {
        SecurityUtil.signout();
    });

    beforeEach(() => {
        navPage = new NavPage();
        meetingListPage = new MeetingListPage();
        newMeetingPage = new NewMeetingPage();
        datetimePickerPage = new DatetimePickerPage();

        navPage.getMeetingsButton().click();
        meetingListPage.getScheduleMeetingButton().click();
    });

    it('should disable form when date is in past', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings\/new');
        expect(newMeetingPage.getScheduleButton().isEnabled()).toBeFalsy();

        const date = moment();
        datetimePickerPage.getDatepickerInput().clear();
        datetimePickerPage.getDatepickerInput().sendKeys(date.format('MMM D, YYYY'));

        expect(newMeetingPage.getScheduleButton().isEnabled()).toBeFalsy();
    });

    it('should add meeting topic and remove it', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings\/new');
        expect(newMeetingPage.getScheduleButton().isEnabled()).toBeFalsy();

        newMeetingPage.getTopicInput().sendKeys('Meeting topic that will be removed');
        newMeetingPage.getAddTopicButton().click();

        newMeetingPage.getRemoveTopicIcon().click();

        expect(newMeetingPage.getRemoveTopicIcon().isPresent()).toBeFalsy();
    });

    it('should create meeting when form is valid', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings\/new');
        expect(newMeetingPage.getScheduleButton().isEnabled()).toBeFalsy();

        const date = moment().add(1, 'months');
        datetimePickerPage.getDatepickerInput().clear();
        datetimePickerPage.getDatepickerInput().sendKeys(date.format('MMM D, YYYY'));

        expect(newMeetingPage.getAddTopicButton().isEnabled()).toBeFalsy();

        newMeetingPage.getTopicInput().sendKeys('Some meeting topic');
        newMeetingPage.getAddTopicButton().click();

        newMeetingPage.getTopicInput().sendKeys('Another meeting topic');
        newMeetingPage.getAddTopicButton().click();

        expect(newMeetingPage.getScheduleButton().isEnabled()).toBeTruthy();
        newMeetingPage.getScheduleButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings\/[1-9]+');
    });
});
