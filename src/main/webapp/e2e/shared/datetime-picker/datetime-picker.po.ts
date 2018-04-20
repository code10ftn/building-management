import { by, element } from 'protractor';

export class DatetimePickerPage {

    getDatepickerInput() {
        return element(by.css('input[name="date"]'));
    }
}
