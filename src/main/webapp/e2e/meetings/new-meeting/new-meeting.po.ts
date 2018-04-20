import { by, element } from 'protractor';

export class NewMeetingPage {

    getTopicInput() {
        return element(by.css('input[name="topic"]'));
    }

    getRemoveTopicIcon() {
        return element(by.css('i'));
    }

    getAddTopicButton() {
        return element(by.id('addTopicButton'));
    }

    getScheduleButton() {
        return element(by.id('scheduleButton'));
    }
}
