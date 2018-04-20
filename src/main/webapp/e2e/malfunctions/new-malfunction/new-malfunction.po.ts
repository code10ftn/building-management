import { by, element } from 'protractor';

export class NewMalfunctionPage {

    getDescriptionInput() {
        return element(by.id('descriptionInput'));
    }

    getReportButton() {
        return element(by.id('reportButton'));
    }

    getWorkAreaSelect() {
        return element(by.css('select[name="workArea"]'));
    }

}
