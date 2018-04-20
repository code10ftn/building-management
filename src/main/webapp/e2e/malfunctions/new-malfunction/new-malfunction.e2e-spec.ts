import { browser } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { MalfunctionListPage } from '../malfunction-list/malfunction-list.po';
import { NewMalfunctionPage } from './new-malfunction.po';

describe('new-malfunction', () => {

    let navPage: NavPage;
    let newMalfunctionPage: NewMalfunctionPage;
    let malfunctionListPage: MalfunctionListPage;

    beforeAll(() => {
        SecurityUtil.authenticate('upravnik', 'upravnik');
    });

    afterAll(() => {
        SecurityUtil.signout();
    });

    beforeEach(() => {
        navPage = new NavPage();
        newMalfunctionPage = new NewMalfunctionPage();
        malfunctionListPage = new MalfunctionListPage();

        navPage.getMalfunctionsButton().click();
        malfunctionListPage.getReportMalfunctionButton().click();
    });

    it('should create malfunction when form is valid', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/new');
        expect(newMalfunctionPage.getReportButton().isEnabled()).toBeFalsy();

        newMalfunctionPage.getDescriptionInput().sendKeys('test');

        expect(newMalfunctionPage.getReportButton().isEnabled()).toBeFalsy();

        newMalfunctionPage.getWorkAreaSelect().sendKeys('WATER');
        expect(newMalfunctionPage.getReportButton().isEnabled()).toBeTruthy();

        newMalfunctionPage.getReportButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');
    });
});
