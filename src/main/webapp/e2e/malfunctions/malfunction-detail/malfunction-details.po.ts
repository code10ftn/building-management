import { by, element } from 'protractor';

export class MalfunctionDetailPage {

    getDescriptionInput() {
        return element(by.id('malDetailsDesc'));
    }

    getWorkAreaSelect() {
        return element(by.css('select[name="malDetailsWorkArea"]'));
    }

    getUpdateButton() {
        return element(by.id('updateButton'));
    }

    getForwardButton() {
        return element(by.id('forwardButton'));
    }

    getAssigneeSelect() {
        return element(by.css('select[name="assigneeSelect"]'));
    }

    getRepairmentButton() {
        return element(by.id('repairmentButton'));
    }
}
