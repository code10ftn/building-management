import { by, element } from 'protractor';

export class MalfunctionImagePage {

    getImageInput() {
        return element(by.css('input[type="file"]'));
    }

    getClearButton() {
        return element(by.id('clearImageButton'));
    }

    getSubmitButton() {
        return element(by.id('submitImageButton'));
    }
}
