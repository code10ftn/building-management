import { by, element, ElementArrayFinder } from 'protractor';

import { ConfirmModalPage } from '../../modal/confirm-modal/confirm-modal.po';

export class MalfunctionCommentPage {

    confirmModal = new ConfirmModalPage();

    getCommentInput() {
        return element(by.id('commentText'));
    }

    getCommentButton() {
        return element(by.id('commentButton'));
    }

    getComments(): ElementArrayFinder {
        return element.all(by.css('li'));
    }

    getDeleteButtons(): ElementArrayFinder {
        return element.all(by.css('li i'));
    }

    getDeleteButton(commentId) {
        return element(by.id('delete_' + commentId));
    }
}
