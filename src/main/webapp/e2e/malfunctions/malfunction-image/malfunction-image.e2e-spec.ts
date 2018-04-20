import { browser } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { MalfunctionDetailPage } from '../malfunction-detail/malfunction-details.po';
import { MalfunctionListPage } from '../malfunction-list/malfunction-list.po';
import { NewMalfunctionPage } from '../new-malfunction/new-malfunction.po';
import { MalfunctionImagePage } from './malfunction-image.po';

describe('malfunction-image', () => {

    let navPage: NavPage;
    let malfunctionListPage: MalfunctionListPage;
    let malfunctionDetailPage: MalfunctionDetailPage;
    let malfunctionImagePage: MalfunctionImagePage;
    let newMalfunctionPage: NewMalfunctionPage;

    beforeAll(() => {
        SecurityUtil.authenticate('luka', 'luka');
    });

    afterAll(() => {
        SecurityUtil.signout();
    });

    beforeEach(() => {
        navPage = new NavPage();
        malfunctionListPage = new MalfunctionListPage();
        malfunctionDetailPage = new MalfunctionDetailPage();
        malfunctionImagePage = new MalfunctionImagePage();
        newMalfunctionPage = new NewMalfunctionPage();
    });

    it('should show malfunction image inputs when creator is signed in', () => {
        navPage.getMalfunctionsButton().click();
        malfunctionListPage.getReportMalfunctionButton().click();
        newMalfunctionPage.getDescriptionInput().sendKeys('a');
        newMalfunctionPage.getWorkAreaSelect().sendKeys('WATER');
        newMalfunctionPage.getReportButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');

        expect(malfunctionImagePage.getImageInput().isDisplayed()).toBeTruthy();

        expect(malfunctionImagePage.getClearButton().isDisplayed()).toBeTruthy();

        expect(malfunctionImagePage.getSubmitButton().isDisplayed()).toBeTruthy();
    });

    it('should hide malfunction image inputs when creator is not signed in', () => {
        SecurityUtil.signout();
        SecurityUtil.authenticate('nemanja', 'nemanja');
        navPage.getMalfunctionsButton().click();
        malfunctionListPage.getMalfunctionInfoButtons().last().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');

        expect(malfunctionImagePage.getImageInput().isPresent()).toBeFalsy();

        expect(malfunctionImagePage.getClearButton().isPresent()).toBeFalsy();

        expect(malfunctionImagePage.getSubmitButton().isPresent()).toBeFalsy();

        SecurityUtil.signout();
        SecurityUtil.authenticate('luka', 'luka');
    });
});
