import * as moment from 'moment';
import { browser, by } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import { DatetimePickerPage } from '../../shared/datetime-picker/datetime-picker.po';
import SecurityUtil from '../../util/security-util';
import { MeetingListPage } from '../meeting-list/meeting-list.po';
import { NewMeetingPage } from '../new-meeting/new-meeting.po';
import { MeetingDetailPage } from './meeting-detail.po';

describe('meeting-detail', () => {

    let navPage: NavPage;
    let meetingListPage: MeetingListPage;
    let newMeetingPage: NewMeetingPage;
    let meetingDetailPage: MeetingDetailPage;
    let datetimePickerPage: DatetimePickerPage;

    beforeAll(() => {
        SecurityUtil.authenticate('upravnik', 'upravnik');

        navPage = new NavPage();
        meetingListPage = new MeetingListPage();
        newMeetingPage = new NewMeetingPage();
        datetimePickerPage = new DatetimePickerPage();

        navPage.getMeetingsButton().click();
        meetingListPage.getScheduleMeetingButton().click();

        const date = moment().add(1, 'months');
        datetimePickerPage.getDatepickerInput().clear();
        datetimePickerPage.getDatepickerInput().sendKeys(date.format('MMM D, YYYY'));

        newMeetingPage.getTopicInput().sendKeys('Some meeting topic');
        newMeetingPage.getAddTopicButton().click();
        newMeetingPage.getTopicInput().sendKeys('Another meeting topic');
        newMeetingPage.getAddTopicButton().click();
        newMeetingPage.getScheduleButton().click();
    });

    afterAll(() => {
        SecurityUtil.signout();
    });

    beforeEach(() => {
        navPage = new NavPage();
        meetingListPage = new MeetingListPage();
        meetingDetailPage = new MeetingDetailPage();
        datetimePickerPage = new DatetimePickerPage();

        navPage.getMeetingsButton().click();
        meetingListPage.getMeetingInfoButtons().last().click();
    });

    it('should disable reschedule meeting for past date', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings\/[1-9]+');
        expect(meetingDetailPage.getRescheduleButton().isEnabled()).toBeFalsy();

        const date = moment();
        datetimePickerPage.getDatepickerInput().clear();
        datetimePickerPage.getDatepickerInput().sendKeys(date.format('MMM D, YYYY'));

        expect(meetingDetailPage.getRescheduleButton().isEnabled()).toBeFalsy();
    });

    it('should reschedule meeting with future date', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings\/[1-9]+');
        expect(meetingDetailPage.getRescheduleButton().isEnabled()).toBeFalsy();

        const date = moment().add(1, 'months');
        datetimePickerPage.getDatepickerInput().clear();
        datetimePickerPage.getDatepickerInput().sendKeys(date.format('MMM D, YYYY'));
        browser.wait(meetingDetailPage.getRescheduleButton().isEnabled(), 10000, 'Reschedule button never became enabled');

        meetingDetailPage.getRescheduleButton().click();

        expect(meetingDetailPage.getMeetingTitle().getText()).toContain(date.format('MMM D, YYYY'));
    });

    it('should add topic suggestion and remove it', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings\/[1-9]+');

        expect(meetingDetailPage.getRemoveTopicIcons().count()).toBe(2);

        meetingDetailPage.getTopicInput().sendKeys('Meeting topic that will be removed');
        meetingDetailPage.getAddTopicButton().click();

        expect(meetingDetailPage.getRemoveTopicIcons().count()).toBe(3);

        meetingDetailPage.getRemoveTopicIcons().first().click();

        expect(meetingDetailPage.getRemoveTopicIcons().count()).toBe(2);
    });

    it('should reject and accept topic suggestion', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings\/[1-9]+');

        expect(meetingDetailPage.getEnabledCheckboxes().count()).toBe(2);

        meetingDetailPage.getEnabledCheckboxes().first().click();

        expect(meetingDetailPage.getEnabledCheckboxes().count()).toBe(1);

        meetingDetailPage.getDisabledCheckboxes().first().click();

        expect(meetingDetailPage.getEnabledCheckboxes().count()).toBe(2);
    });

    it('should not show create report button when meeting has not passed yet', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings\/[1-9]+');
        expect(browser.isElementPresent(by.id('createReportButton'))).toBeFalsy();
    });

    it('should write report for passed meeting', () => {
        navPage.getMeetingsButton().click();
        meetingListPage.getMeetingInfoButtons().first().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings\/[1-9]+');
        expect(meetingDetailPage.getCreateReportButton().isDisplayed()).toBeTruthy();

        meetingDetailPage.getCreateReportButton().click();

        const topicComment = 'This topic was discussed.';
        meetingDetailPage.getCommentInputs().first().sendKeys(topicComment);

        const reportComment = 'The meeting was a success.';
        meetingDetailPage.getCommentInputs().last().sendKeys(reportComment);

        meetingDetailPage.getSaveReportButton().click();

        expect(meetingDetailPage.getReportComments().first().getText()).toContain(topicComment);
        expect(meetingDetailPage.getReportComments().last().getText()).toContain(reportComment);
    });
});
