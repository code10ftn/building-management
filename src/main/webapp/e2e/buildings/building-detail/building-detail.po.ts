import { browser, by, element } from 'protractor';

import { BuildingCompaniesListPage } from '../building-companies-list/building-companies-list.po';

export class BuildingDetailPage {

    buildingCompaniesPage = new BuildingCompaniesListPage();

    navigateTo() {
        return browser.get('buildings/2');
    }

    navigateToBuilding1() {
        return browser.get('buildings/1');
    }

    getTenantsLink() {
        return element(by.id('tenantsLink'));
    }

    getAddressTakenLabel() {
        return element(by.id('addressTakenLabel'));
    }

    getBuildingAddressInput() {
        return element(by.id('addressInput'));
    }

    getBuildingSupervisorInput() {
        return element(by.id('supervisorInput'));
    }

    getUpdateBuildingButton() {
        return element(by.id('updateBuildingButton'));
    }

    getApartmentCountLabel() {
        return element(by.id('apartmentCountLabel'));
    }
}
