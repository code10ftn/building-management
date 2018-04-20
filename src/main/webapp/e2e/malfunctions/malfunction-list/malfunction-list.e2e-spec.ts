import { browser } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { MalfunctionListPage } from '../malfunction-list/malfunction-list.po';

describe('malfunction-list', () => {

    let navPage: NavPage;
    let malfunctionListPage: MalfunctionListPage;

    beforeAll(() => {
        SecurityUtil.authenticate('upravnik', 'upravnik');
    });

    afterAll(() => {
        SecurityUtil.signout();
    });

    beforeEach(() => {
        navPage = new NavPage();
        malfunctionListPage = new MalfunctionListPage();

        navPage.getMalfunctionsButton().click();
    });

    it('should display buildings', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions');
        expect(malfunctionListPage.getRows().count()).toBeGreaterThan(0);
    });

    it('should open malfunction details when details button is clicked', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions');

        malfunctionListPage.getDetailsButton('1').click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');
    });

    it('should disable report malfunction button when non supervising company is signed in', () => {
        SecurityUtil.signout();
        SecurityUtil.authenticate('haus', 'haus');

        navPage.getCompanyMalfunctions().click();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/malfunctions');

        expect(malfunctionListPage.getReportMalfunctionButton().isPresent()).toBeFalsy();

        SecurityUtil.signout();
        SecurityUtil.authenticate('upravnik', 'upravnik');
    });
});
