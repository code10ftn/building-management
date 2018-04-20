import { browser, by, element } from 'protractor';

import { ConfirmModalPage } from '../../modal/confirm-modal/confirm-modal.po';
import { NewBuildingPage } from '../new-building/new-building.po';

export class BuildingListPage {

    newBuildingPage = new NewBuildingPage();
    confirmModal = new ConfirmModalPage();

    navigateTo() {
        browser.get('buildings');
    }

    getRows() {
        return element.all(by.css('.datatable-body-row'));
    }

    getAddBuildingButton() {
        return element(by.id('addBuildingButton'));
    }

    getBuildingInfoButtons() {
        return element.all(by.css('.fa-info-circle'));
    }

    getBuildingDeleteButtons() {
        return element.all(by.css('.fa-trash'));
    }
}
