import { by, element, ElementArrayFinder } from 'protractor';

import { ConfirmModalPage } from '../../modal/confirm-modal/confirm-modal.po';

export class MeetingListPage {

    confirmModal = new ConfirmModalPage();

    getScheduleMeetingButton() {
        return element(by.id('scheduleMeetingButton'));
    }

    getRows(): ElementArrayFinder {
        return element.all(by.css('.datatable-body-row'));
    }

    getMeetingInfoButtons() {
        return element.all(by.css('.fa-info-circle'));
    }

    getMeetingDeleteButtons() {
        return element.all(by.css('.fa-trash'));
    }
}
