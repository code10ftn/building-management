import { by, element } from 'protractor';

export class ResidentialRequestListPage {

    getResidentialRequestElements() {
        return element.all(by.css('.list-group-item'));
    }

    getDeclineButtons() {
        return element.all(by.css('.fa-times'));
    }

    getAcceptButtons() {
        return element.all(by.css('.fa-check'));
    }
}
