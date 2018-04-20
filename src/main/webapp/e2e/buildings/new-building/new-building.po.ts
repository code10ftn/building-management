import { by, element } from 'protractor';

export class NewBuildingPage {

    getAddressInput() {
        return element(by.css('input[name="address"]'));
    }

    getAptCountInput() {
        return element(by.css('input[name="aptCount"]'));
    }

    getCreateBuildingButton() {
        return element(by.id('createBuildingButton'));
    }

    getAddressTakenLabel() {
        return element(by.id('addressTakenLabel'));
    }
}
