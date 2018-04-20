import { by, element } from 'protractor';

export class ConfirmModalPage {

    getConfirmButton() {
        return element(by.id('confirmButton'));
    }

    getCancelButton() {
        return element(by.id('cancelButton'));
    }
}
