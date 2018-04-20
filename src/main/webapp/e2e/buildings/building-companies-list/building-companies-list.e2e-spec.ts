import { browser, by } from 'protractor';
import { protractor } from 'protractor/built/ptor';

import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { BuildingDetailPage } from '../building-detail/building-detail.po';

describe('building-companies-list', () => {

    let buildingDetailPage: BuildingDetailPage;
    let navPage: NavPage;

    beforeAll(() => {
        navPage = new NavPage();
        buildingDetailPage = new BuildingDetailPage();
        SecurityUtil.authenticate('upravnik', 'upravnik');

        navPage.getBuildingsButton().click();
        buildingDetailPage.navigateToBuilding1();
    });

    it('should open building id 1 and show building\'s companies and company input when user is supervisor', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+');

        expect(buildingDetailPage.buildingCompaniesPage.getCompaniesListItems().count()).toBeGreaterThan(0);
        expect(buildingDetailPage.buildingCompaniesPage.getCompanyInput().isDisplayed()).toBeTruthy();
        expect(buildingDetailPage.buildingCompaniesPage.getAddCompanyButton().isDisplayed()).toBeTruthy();
        expect(buildingDetailPage.buildingCompaniesPage.getDeleteCompanyButtons().count()).toBeGreaterThan(0);
    });

    it('should show only companies list and hide input and buttons when user is not supervisor', () => {
        SecurityUtil.signout();
        SecurityUtil.authenticate('luka', 'luka');
        navPage.getBuildingsButton().click();
        buildingDetailPage.navigateToBuilding1();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+');
        expect(buildingDetailPage.buildingCompaniesPage.getCompaniesListItems().count()).toBeGreaterThan(0);
        expect(browser.isElementPresent(by.id('companyInput'))).toBeFalsy();
        expect(browser.isElementPresent(by.id('addCompanyButton'))).toBeFalsy();
        expect(buildingDetailPage.buildingCompaniesPage.getDeleteCompanyButtons().count()).toBe(0);

        SecurityUtil.signout();
        SecurityUtil.authenticate('upravnik', 'upravnik');
        navPage.getBuildingsButton().click();
        buildingDetailPage.navigateToBuilding1();
    });

    it('should disable add company button when input is incorrect', () => {
        buildingDetailPage.buildingCompaniesPage.getCompanyInput().clear();
        buildingDetailPage.buildingCompaniesPage.getCompanyInput().sendKeys('asd');
        expect(buildingDetailPage.buildingCompaniesPage.getAddCompanyButton().isEnabled()).toBeFalsy();
    });

    it('should enable add company button and add company when input is correct', () => {
        buildingDetailPage.buildingCompaniesPage.getCompaniesListItems().count().then(function (count) {
            buildingDetailPage.buildingCompaniesPage.getCompanyInput().clear();
            buildingDetailPage.buildingCompaniesPage.getCompanyInput().sendKeys('a');

            expect(buildingDetailPage.buildingCompaniesPage.getAddCompanyButton().isEnabled()).toBeFalsy();

            browser.actions().sendKeys(protractor.Key.ARROW_DOWN).perform();
            browser.actions().sendKeys(protractor.Key.ENTER).perform();

            expect(buildingDetailPage.buildingCompaniesPage.getAddCompanyButton().isEnabled()).toBeTruthy();

            buildingDetailPage.buildingCompaniesPage.getAddCompanyButton().click();
            expect(buildingDetailPage.buildingCompaniesPage.getCompaniesListItems().count()).toBe(count + 1);
        });
    });

    it('should delete company when delete company button is pressed', () => {
        buildingDetailPage.buildingCompaniesPage.getCompaniesListItems().count().then(function (count) {
            expect(buildingDetailPage.buildingCompaniesPage.getDeleteCompanyButtons().count()).toBe(count);

            buildingDetailPage.buildingCompaniesPage.getDeleteCompanyButtons().last().click();
            expect(buildingDetailPage.buildingCompaniesPage.getCompaniesListItems().count()).toBe(count - 1);
            expect(buildingDetailPage.buildingCompaniesPage.getDeleteCompanyButtons().count()).toBe(count - 1);
        });
    });

    afterAll(() => {
        SecurityUtil.signout();
    });
});
