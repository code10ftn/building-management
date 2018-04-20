import { browser, by } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { BuildingListPage } from './building-list.po';

describe('building-list', () => {

    let buildingListPage: BuildingListPage;
    let navPage: NavPage;

    beforeAll(() => {
        SecurityUtil.authenticate('admin', 'admin');
    });

    beforeEach(() => {
        buildingListPage = new BuildingListPage();
        navPage = new NavPage();
        navPage.getBuildingsButton().click();
    });

    it('should display buildings', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings');
        expect(buildingListPage.getRows().count()).toBeGreaterThan(0);
    });

    it('should show add building button, building info button and delete building button when user is admin', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings');
        expect(buildingListPage.getAddBuildingButton().isDisplayed()).toBeTruthy();
        expect(buildingListPage.getBuildingInfoButtons().first().isDisplayed()).toBeTruthy();
        expect(buildingListPage.getBuildingDeleteButtons().first().isDisplayed()).toBeTruthy();
    });

    it('should not display add building button and delete building button when user is not admin', () => {
        SecurityUtil.signout();
        SecurityUtil.authenticate('upravnik', 'upravnik');
        navPage.getBuildingsButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings');
        expect(browser.isElementPresent(by.id('addBuildingButton'))).toBeFalsy();
        expect(buildingListPage.getBuildingInfoButtons().first().isDisplayed()).toBeTruthy();
        expect(buildingListPage.getBuildingDeleteButtons().count()).toBe(0);

        SecurityUtil.signout();
        SecurityUtil.authenticate('admin', 'admin');
    });

    it('should disable add building button when data is invalid', () => {
        buildingListPage.getAddBuildingButton().click();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/new');

        const addressInput = buildingListPage.newBuildingPage.getAddressInput();
        const aptCountInput = buildingListPage.newBuildingPage.getAptCountInput();
        const createBuildingButton = buildingListPage.newBuildingPage.getCreateBuildingButton();
        expect(addressInput.isDisplayed()).toBeTruthy();
        expect(aptCountInput.isDisplayed()).toBeTruthy();
        expect(createBuildingButton.isDisplayed()).toBeTruthy();
        expect(createBuildingButton.isEnabled()).toBeFalsy();

        addressInput.sendKeys('Adresa 174c');
        expect(createBuildingButton.isEnabled()).toBeFalsy();
        expect(buildingListPage.newBuildingPage.getAddressTakenLabel().isDisplayed()).toBeTruthy();

        aptCountInput.sendKeys(13);
        expect(createBuildingButton.isEnabled()).toBeFalsy();

        addressInput.sendKeys(`Valid address ${Date.now()}`);
        expect(createBuildingButton.isEnabled()).toBeTruthy();
        expect(browser.isElementPresent(by.id('addressTakenLabel'))).toBeFalsy();
    });

    it('should add building when data is valid', () => {
        buildingListPage.getRows().count().then(function (count) {
            buildingListPage.getAddBuildingButton().click();
            expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/new');

            buildingListPage.newBuildingPage.getAddressInput().sendKeys(`Valid address ${Date.now()}`);
            buildingListPage.newBuildingPage.getAptCountInput().sendKeys(13);
            buildingListPage.newBuildingPage.getCreateBuildingButton().click();

            expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings');
            expect(buildingListPage.getRows().count()).toBe(count + 1);
        });
    });

    it('should open modal when delete button is clicked and then cancel modal', () => {
        buildingListPage.getRows().count().then(function (count) {
            expect(buildingListPage.getBuildingDeleteButtons().count()).toBe(count);
            buildingListPage.getBuildingDeleteButtons().last().click();

            expect(buildingListPage.confirmModal.getConfirmButton().isDisplayed()).toBeTruthy();
            expect(buildingListPage.confirmModal.getCancelButton().isDisplayed()).toBeTruthy();

            buildingListPage.confirmModal.getCancelButton().click();
            expect(buildingListPage.getRows().count()).toBe(count);
        });
    });

    it('should delete building when delete button is clicked and modal confirmed', () => {
        buildingListPage.getRows().count().then(function (count) {
            expect(buildingListPage.getBuildingDeleteButtons().count()).toBe(count);
            buildingListPage.getBuildingDeleteButtons().last().click();

            buildingListPage.confirmModal.getConfirmButton().click();
            expect(buildingListPage.getRows().count()).toBe(count - 1);
        });
    });

    it('should open building detail page on building info button click', () => {
        buildingListPage.getBuildingInfoButtons().first().click();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+');
    });

    afterAll(() => {
        SecurityUtil.signout();
    });
});
