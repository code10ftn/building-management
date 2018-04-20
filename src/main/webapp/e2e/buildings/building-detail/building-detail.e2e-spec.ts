import { browser, by, protractor } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { BuildingListPage } from '../building-list/building-list.po';
import { BuildingDetailPage } from './building-detail.po';

describe('building-details', () => {

    let buildingDetailPage: BuildingDetailPage;
    let navPage: NavPage;

    beforeAll(() => {
        navPage = new NavPage();
        buildingDetailPage = new BuildingDetailPage();
        SecurityUtil.authenticate('admin', 'admin');

        // Add another building.
        const buildingListPage = new BuildingListPage();
        navPage.getBuildingsButton().click();
        buildingListPage.getAddBuildingButton().click();
        buildingListPage.newBuildingPage.getAddressInput().sendKeys('New building');
        buildingListPage.newBuildingPage.getAptCountInput().sendKeys(25);
        buildingListPage.newBuildingPage.getCreateBuildingButton().click();

        navPage.getBuildingsButton().click();
        buildingDetailPage.navigateTo();
    });

    it('should display building details when authenticated as tenant and hide update button', () => {
        SecurityUtil.signout();
        SecurityUtil.authenticate('luka', 'luka');
        navPage.getBuildingsButton().click();
        buildingDetailPage.navigateTo();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+');

        expect(buildingDetailPage.getBuildingAddressInput().isDisplayed()).toBeTruthy();
        expect(buildingDetailPage.getBuildingAddressInput().isEnabled()).toBeFalsy();
        expect(buildingDetailPage.getApartmentCountLabel().isDisplayed()).toBeTruthy();
        expect(buildingDetailPage.getBuildingSupervisorInput().isDisplayed()).toBeTruthy();
        expect(buildingDetailPage.getBuildingSupervisorInput().isEnabled()).toBeFalsy();
        expect(browser.isElementPresent(by.id('updateBuildingButton'))).toBeFalsy();

        SecurityUtil.signout();
        SecurityUtil.authenticate('admin', 'admin');
        navPage.getBuildingsButton().click();
        buildingDetailPage.navigateTo();
    });

    it('should display update button when authenticated as admin', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+');
        expect(buildingDetailPage.getBuildingAddressInput().isDisplayed()).toBeTruthy();
        expect(buildingDetailPage.getApartmentCountLabel().isDisplayed()).toBeTruthy();
        expect(buildingDetailPage.getBuildingSupervisorInput().isDisplayed()).toBeTruthy();
        expect(buildingDetailPage.getUpdateBuildingButton().isDisplayed()).toBeTruthy();
    });

    it('should update building when supervisor and address are valid', () => {
        buildingDetailPage.getBuildingAddressInput().clear();
        buildingDetailPage.getBuildingAddressInput().sendKeys('New address');
        expect(browser.isElementPresent(by.id('addressTakenLabel'))).toBeFalsy();

        buildingDetailPage.getBuildingSupervisorInput().clear();
        buildingDetailPage.getBuildingSupervisorInput().sendKeys('hau');
        browser.actions().sendKeys(protractor.Key.ARROW_DOWN).perform();
        browser.actions().sendKeys(protractor.Key.ENTER).perform();
        buildingDetailPage.getUpdateBuildingButton().click();

        buildingDetailPage.navigateTo();
        expect(buildingDetailPage.getBuildingSupervisorInput().getAttribute('value')).toBe('Haus Majstor i Higijena');
        expect(buildingDetailPage.getBuildingAddressInput().getAttribute('value')).toBe('New address');
    });

    it('should display address taken when address is taken', () => {
        buildingDetailPage.getBuildingAddressInput().clear();
        buildingDetailPage.getBuildingAddressInput().sendKeys('Adresa 174c');

        expect(buildingDetailPage.getAddressTakenLabel().isDisplayed()).toBeTruthy();
        expect(buildingDetailPage.getUpdateBuildingButton().isEnabled()).toBeFalsy();
    });

    afterAll(() => {
        SecurityUtil.signout();
    });
});
