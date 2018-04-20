import { by, element } from 'protractor';

export class BuildingResidentialRequestPage {

    getApartmentNumberLabel() {
        return element(by.id('aptNumberLabel'));
    }

    getApartmentNumberInput() {
        return element(by.id('aptNumberInput'));
    }

    getRequestResidenceButton() {
        return element(by.id('requestResidenceButton'));
    }

    getCancelRequestButton() {
        return element(by.id('cancelResidentialRequestButton'));
    }
}
