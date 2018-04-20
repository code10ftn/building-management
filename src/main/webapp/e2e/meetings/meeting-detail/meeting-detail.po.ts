import { by, element } from 'protractor';

export class MeetingDetailPage {

    getRescheduleButton() {
        return element(by.id('rescheduleButton'));
    }

    getMeetingTitle() {
        return element(by.id('meetingTitle'));
    }

    getTopicInput() {
        return element(by.css('input[name="topic"]'));
    }

    getAddTopicButton() {
        return element(by.id('addTopicButton'));
    }

    getRemoveTopicIcons() {
        return element.all(by.css('i'));
    }

    getEnabledCheckboxes() {
        return element.all(by.css('input[type="checkbox"]:checked'));
    }

    getDisabledCheckboxes() {
        return element.all(by.css('input[type="checkbox"]:not(:checked)'));
    }

    getCreateReportButton() {
        return element(by.id('createReportButton'));
    }

    getSaveReportButton() {
        return element(by.id('saveReportButton'));
    }

    getCommentInputs() {
        return element.all(by.css('input[type="text"]'));
    }

    getReportComments() {
        return element.all(by.css('.comment'));
    }
}
