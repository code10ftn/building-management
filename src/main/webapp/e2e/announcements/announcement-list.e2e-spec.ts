import { browser } from 'protractor';

import { NavPage } from '../nav/nav.po';
import SecurityUtil from '../util/security-util';
import { AnnouncementListPage } from './announcement-list.po';

describe('announcement-list', () => {

    let announcementListPage: AnnouncementListPage;
    let navPage: NavPage;

    beforeAll(() => {
        navPage = new NavPage();
        announcementListPage = new AnnouncementListPage();
        SecurityUtil.authenticate('upravnik', 'upravnik');

        navPage.getAnnouncementsButton().click();
    });

    it('should open announcement list when user is supervisor', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/announcements');
        expect(announcementListPage.getAnnouncementInput().isDisplayed()).toBeTruthy();
        expect(announcementListPage.getAnnouncementButton().isDisplayed()).toBeTruthy();
        expect(announcementListPage.getAnnouncementButton().isEnabled()).toBeFalsy();
    });

    it('should enable add button and post announcement when user is supervisor', () => {
        announcementListPage.getAnnouncements().count().then(function (count) {
            expect(count).toBeGreaterThanOrEqual(0);

            announcementListPage.getAnnouncementInput().sendKeys('There will be water outage');
            expect(announcementListPage.getAnnouncementButton().isEnabled()).toBeTruthy();

            announcementListPage.getAnnouncementButton().click();
            expect(announcementListPage.getAnnouncements().count()).toBe(count + 1);
        });
    });

    it('should count stay the same when supervisor clicks delete and NO on modal', () => {
        announcementListPage.getAnnouncements().count().then(function (count) {
            expect(count).toBeGreaterThan(0);
            expect(announcementListPage.getDeleteButtons().count()).toBe(count);

            announcementListPage.getDeleteButtons().last().click();
            expect(announcementListPage.confirmModal.getConfirmButton().isDisplayed()).toBeTruthy();
            expect(announcementListPage.confirmModal.getCancelButton().isDisplayed()).toBeTruthy();

            announcementListPage.confirmModal.getCancelButton().click();
            expect(announcementListPage.getAnnouncements().count()).toBe(count);
        });
    });

    it('should count decrease when supervisor clicks delete and YES on modal', () => {
        announcementListPage.getAnnouncements().count().then(function (count) {
            expect(count).toBeGreaterThan(0);
            expect(announcementListPage.getDeleteButtons().count()).toBe(count);

            announcementListPage.getDeleteButtons().last().click();

            announcementListPage.confirmModal.getConfirmButton().click();
            expect(announcementListPage.getAnnouncements().count()).toBe(count - 1);
        });
    });

    it('should open announcements when user is tenant', () => {
        SecurityUtil.signout();
        SecurityUtil.authenticate('luka', 'luka');
        navPage.getAnnouncementsButton().click();

        expect(announcementListPage.getAnnouncementInput().isDisplayed()).toBeTruthy();
        expect(announcementListPage.getAnnouncementButton().isDisplayed()).toBeTruthy();
        expect(announcementListPage.getAnnouncementButton().isEnabled()).toBeFalsy();
    });

    it('should create announcement as tenant and display it', () => {
        announcementListPage.getAnnouncements().count().then(function (count) {
            expect(count).toBeGreaterThanOrEqual(0);

            announcementListPage.getAnnouncementInput().sendKeys('Power outage on MONDAY because I\'m fixing the wiring');
            announcementListPage.getAnnouncementButton().click();

            expect(announcementListPage.getAnnouncements().count()).toBe(count + 1);
            expect(announcementListPage.getDeleteButtons().count()).toBe(1);
        });
    });

    afterAll(() => {
        SecurityUtil.signout();
    });
});
