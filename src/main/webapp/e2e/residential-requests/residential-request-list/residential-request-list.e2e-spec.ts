import { browser } from 'protractor';

import { BuildingDetailPage } from '../../buildings/building-detail/building-detail.po';
import { BuildingTenantsListPage } from '../../buildings/building-tenants-list/building-tenants-list.po';
import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { BuildingResidentialRequestPage } from '../building-residential-request/building-residential-request.po';
import { ResidentialRequestListPage } from './residential-request-list.po';

describe('residential-requests-list', () => {

    let residentialRequestListPage: ResidentialRequestListPage;
    let buildingTenantsListPage: BuildingTenantsListPage;
    let buildingResidentialRequestPage: BuildingResidentialRequestPage;
    let buildingDetailPage: BuildingDetailPage;
    let navPage: NavPage;

    beforeAll(() => {
        residentialRequestListPage = new ResidentialRequestListPage();
        buildingTenantsListPage = new BuildingTenantsListPage();
        buildingResidentialRequestPage = new BuildingResidentialRequestPage();
        navPage = new NavPage();
        buildingDetailPage = new BuildingDetailPage();
    });

    it('should display list of residential requests when user is admin', () => {
        // Login as tenant.
        SecurityUtil.authenticate('helena', 'helena');
        navPage.getBuildingsButton().click();
        buildingDetailPage.navigateToBuilding1();

        // Go to tenants list and count tenants.
        buildingDetailPage.getTenantsLink().click();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/tenants');
        buildingTenantsListPage.getRows().count().then(function (count) {
            // Request residence.
            buildingDetailPage.navigateToBuilding1();
            buildingResidentialRequestPage.getApartmentNumberInput().sendKeys(1);
            buildingResidentialRequestPage.getRequestResidenceButton().click();

            // Login as admin.
            SecurityUtil.signout();
            SecurityUtil.authenticate('admin', 'admin');

            navPage.getResidentialRequestsButton().click();
            expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/residentialRequests');
            expect(residentialRequestListPage.getResidentialRequestElements().count()).toBeGreaterThan(0);
            expect(residentialRequestListPage.getAcceptButtons().count()).toBeGreaterThan(0);
            expect(residentialRequestListPage.getDeclineButtons().count()).toBeGreaterThan(0);

            // Decline tenant's request.
            residentialRequestListPage.getDeclineButtons().first().click();
            buildingDetailPage.navigateToBuilding1();
            buildingDetailPage.getTenantsLink().click();

            expect(buildingTenantsListPage.getRows().count()).toBe(count);
        });
    });

    it('should display list of residential requests when user is admin', () => {
        // Login as tenant.
        SecurityUtil.signout();
        SecurityUtil.authenticate('helena', 'helena');
        navPage.getBuildingsButton().click();
        buildingDetailPage.navigateToBuilding1();

        // Go to tenants list and count tenants.
        buildingDetailPage.getTenantsLink().click();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/tenants');
        buildingTenantsListPage.getRows().count().then(function (count) {
            // Request residence.
            buildingDetailPage.navigateToBuilding1();
            buildingResidentialRequestPage.getApartmentNumberInput().sendKeys(1);
            buildingResidentialRequestPage.getRequestResidenceButton().click();

            // Login as admin.
            SecurityUtil.signout();
            SecurityUtil.authenticate('admin', 'admin');

            navPage.getResidentialRequestsButton().click();
            expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/residentialRequests');
            expect(residentialRequestListPage.getResidentialRequestElements().count()).toBeGreaterThan(0);
            expect(residentialRequestListPage.getAcceptButtons().count()).toBeGreaterThan(0);
            expect(residentialRequestListPage.getDeclineButtons().count()).toBeGreaterThan(0);

            // Accept tenant's request.
            residentialRequestListPage.getAcceptButtons().first().click();
            buildingDetailPage.navigateToBuilding1();
            buildingDetailPage.getTenantsLink().click();

            expect(buildingTenantsListPage.getRows().count()).toBe(count + 1);
        });
    });

    afterAll(() => {
        SecurityUtil.signout();
    });
});
