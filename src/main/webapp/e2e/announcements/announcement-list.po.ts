import { by, element } from 'protractor';

import { ConfirmModalPage } from '../modal/confirm-modal/confirm-modal.po';

export class AnnouncementListPage {

    confirmModal = new ConfirmModalPage();

    getAnnouncementInput() {
        return element(by.id('announcementInput'));
    }

    getAnnouncementButton() {
        return element(by.id('announcementPostButton'));
    }

    getAnnouncements() {
        return element.all(by.css('.datatable-body-row'));
    }

    getDeleteButtons() {
        return element.all(by.css('.fa-trash'));
    }
}
