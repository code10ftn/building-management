import { by, element } from 'protractor';

export class NewRepairmentPage {

    getpriceInput() {
        return element(by.id('priceInput'));
    }

    getSubmitButton() {
        return element(by.id('repairmentSubmitButton'));
    }
}
