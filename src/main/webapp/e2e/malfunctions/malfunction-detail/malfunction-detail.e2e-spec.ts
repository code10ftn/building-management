import { browser } from 'protractor';
import { Key } from 'selenium-webdriver';

import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { MalfunctionListPage } from '../malfunction-list/malfunction-list.po';
import { MalfunctionDetailPage } from './malfunction-details.po';

describe('malfunction-detail', () => {

    let navPage: NavPage;
    let malfunctionListPage: MalfunctionListPage;
    let malfunctionDetailPage: MalfunctionDetailPage;

    beforeAll(() => {
        SecurityUtil.authenticate('upravnik', 'upravnik');
    });

    afterAll(() => {
        SecurityUtil.signout();
    });

    beforeEach(() => {
        navPage = new NavPage();
        malfunctionListPage = new MalfunctionListPage();
        malfunctionDetailPage = new MalfunctionDetailPage();

        navPage.getMalfunctionsButton().click();
        malfunctionListPage.getDetailsButton('1').click();

    });

    it('should update malfunction when form is valid', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');

        malfunctionDetailPage.getDescriptionInput().clear();
        malfunctionDetailPage.getDescriptionInput().sendKeys('a');
        malfunctionDetailPage.getDescriptionInput().sendKeys(Key.BACK_SPACE);

        expect(malfunctionDetailPage.getUpdateButton().isEnabled()).toBeFalsy();

        malfunctionDetailPage.getDescriptionInput().sendKeys('new desc');

        malfunctionDetailPage.getWorkAreaSelect().sendKeys('WATER');
        expect(malfunctionDetailPage.getUpdateButton().isEnabled()).toBeTruthy();

        malfunctionDetailPage.getUpdateButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');
    });

    it('should show rapirment button when user is assignee', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');

        expect(malfunctionDetailPage.getRepairmentButton().isDisplayed()).toBeTruthy();
    });

    it('should forward malfunction when form is valid', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');

        malfunctionDetailPage.getAssigneeSelect().sendKeys('gas');
        expect(malfunctionDetailPage.getForwardButton().isEnabled()).toBeTruthy();

        malfunctionDetailPage.getForwardButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');
    });
});
