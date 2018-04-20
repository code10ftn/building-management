import { browser, by } from 'protractor';

import { BuildingDetailPage } from '../../buildings/building-detail/building-detail.po';
import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { BuildingResidentialRequestPage } from './building-residential-request.po';

describe('building-residential-requests', () => {

    let buildingResidentialRequestPage: BuildingResidentialRequestPage;
    let buildingDetailPage: BuildingDetailPage;
    let navPage: NavPage;

    beforeAll(() => {
        buildingResidentialRequestPage = new BuildingResidentialRequestPage();
        navPage = new NavPage();
        buildingDetailPage = new BuildingDetailPage();
        SecurityUtil.authenticate('helena', 'helena');

        navPage.getBuildingsButton().click();
        buildingDetailPage.navigateToBuilding1();
    });

    it('should display input and request button and hide cancel button when tenant doesn\'t live in the building', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+');
        expect(buildingResidentialRequestPage.getApartmentNumberInput().isDisplayed()).toBeTruthy();
        expect(buildingResidentialRequestPage.getApartmentNumberLabel().isDisplayed()).toBeTruthy();
        expect(buildingResidentialRequestPage.getRequestResidenceButton().isDisplayed()).toBeTruthy();
        expect(browser.isElementPresent(by.id('cancelResidentialRequestButton'))).toBeFalsy();
    });

    it('should show cancel button when residential request is made', () => {
        buildingResidentialRequestPage.getApartmentNumberInput().sendKeys(1);
        buildingResidentialRequestPage.getRequestResidenceButton().click();

        buildingDetailPage.navigateToBuilding1();
        expect(buildingResidentialRequestPage.getCancelRequestButton().isDisplayed()).toBeTruthy();
    });

    it('should show input and request button when request is canceled', () => {
        buildingResidentialRequestPage.getCancelRequestButton().click();

        buildingDetailPage.navigateToBuilding1();
        expect(buildingResidentialRequestPage.getApartmentNumberInput().isDisplayed()).toBeTruthy();
        expect(buildingResidentialRequestPage.getApartmentNumberLabel().isDisplayed()).toBeTruthy();
        expect(buildingResidentialRequestPage.getRequestResidenceButton().isDisplayed()).toBeTruthy();
        expect(browser.isElementPresent(by.id('cancelResidentialRequestButton'))).toBeFalsy();
    });

    it('should not show input and request and cancel buttons when user is admin', () => {
        SecurityUtil.signout();
        SecurityUtil.authenticate('admin', 'admin');
        navPage.getBuildingsButton().click();
        buildingDetailPage.navigateToBuilding1();

        expect(browser.isElementPresent(by.id('aptNumberLabel'))).toBeFalsy();
        expect(browser.isElementPresent(by.id('aptNumberInput'))).toBeFalsy();
        expect(browser.isElementPresent(by.id('requestResidenceButton'))).toBeFalsy();
        expect(browser.isElementPresent(by.id('cancelResidentialRequestButton'))).toBeFalsy();
    });

    afterAll(() => {
        SecurityUtil.signout();
    });
});
