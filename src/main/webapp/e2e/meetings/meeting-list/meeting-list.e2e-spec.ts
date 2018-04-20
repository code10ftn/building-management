import { browser } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { MeetingListPage } from './meeting-list.po';

describe('meeting-list', () => {

    let meetingListPage: MeetingListPage;
    let navPage: NavPage;

    beforeAll(() => {
        SecurityUtil.authenticate('upravnik', 'upravnik');
    });

    afterAll(() => {
        SecurityUtil.signout();
    });

    beforeEach(() => {
        meetingListPage = new MeetingListPage();
        navPage = new NavPage();
        navPage.getMeetingsButton().click();
    });

    it('should display meetings', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings');
        expect(meetingListPage.getRows().count()).toBeGreaterThan(0);
    });

    it('should show schedule meeting button, meeting info button and delete meeting button when user is supervisor', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings');
        expect(meetingListPage.getScheduleMeetingButton().isDisplayed()).toBeTruthy();
        expect(meetingListPage.getMeetingInfoButtons().first().isDisplayed()).toBeTruthy();
        expect(meetingListPage.getMeetingDeleteButtons().first().isDisplayed()).toBeTruthy();
    });

    it('should not display schedule meeting button and delete meeting button when user is not supervisor', () => {
        SecurityUtil.signout();
        SecurityUtil.authenticate('luka', 'luka');
        navPage.getMeetingsButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings');
        expect(meetingListPage.getMeetingInfoButtons().first().isDisplayed()).toBeTruthy();
        expect(meetingListPage.getMeetingDeleteButtons().count()).toBe(0);

        SecurityUtil.signout();
        SecurityUtil.authenticate('upravnik', 'upravnik');
    });

    it('should open meeting detail page on meeting info button click', () => {
        meetingListPage.getMeetingInfoButtons().first().click();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/meetings\/[1-9]+');
    });

    it('should open modal when delete button is clicked and then cancel modal', () => {
        meetingListPage.getRows().count().then(function (count) {
            expect(meetingListPage.getMeetingDeleteButtons().count()).toBe(count);
            meetingListPage.getMeetingDeleteButtons().last().click();

            expect(meetingListPage.confirmModal.getConfirmButton().isDisplayed()).toBeTruthy();
            expect(meetingListPage.confirmModal.getCancelButton().isDisplayed()).toBeTruthy();

            meetingListPage.confirmModal.getCancelButton().click();
            expect(meetingListPage.getRows().count()).toBe(count);
        });
    });

    it('should delete meeting when delete button is clicked and modal confirmed', () => {
        meetingListPage.getRows().count().then(function (count) {
            expect(meetingListPage.getMeetingDeleteButtons().count()).toBe(count);
            meetingListPage.getMeetingDeleteButtons().last().click();

            meetingListPage.confirmModal.getConfirmButton().click();
            expect(meetingListPage.getRows().count()).toBe(count - 1);
        });
    });
});
