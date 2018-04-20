import * as moment from 'moment';
import { browser } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import { DatetimePickerPage } from '../../shared/datetime-picker/datetime-picker.po';
import SecurityUtil from '../../util/security-util';
import { MalfunctionDetailPage } from '../malfunction-detail/malfunction-details.po';
import { MalfunctionListPage } from '../malfunction-list/malfunction-list.po';
import { NewRepairmentPage } from './new-repairment.po';

describe('new-repairment', () => {

    let navPage: NavPage;
    let malfunctionListPage: MalfunctionListPage;
    let malfunctionDetailPage: MalfunctionDetailPage;
    let datetimePickerPage: DatetimePickerPage;
    let newRepairmentPage: NewRepairmentPage;

    beforeAll(() => {
        SecurityUtil.authenticate('gas', 'gas');
    });

    afterAll(() => {
        SecurityUtil.signout();
    });

    beforeEach(() => {
        navPage = new NavPage();
        malfunctionListPage = new MalfunctionListPage();
        malfunctionDetailPage = new MalfunctionDetailPage();
        datetimePickerPage = new DatetimePickerPage();
        newRepairmentPage = new NewRepairmentPage();

        navPage.getBuildingsButton().click();
        browser.get('buildings/1/malfunctions/1');

    });

    it('should disable form when date is in past', () => {
        malfunctionDetailPage.getRepairmentButton().click();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+\/repair');
        expect(newRepairmentPage.getSubmitButton().isEnabled()).toBeFalsy();

        newRepairmentPage.getpriceInput().sendKeys('100');

        const now = moment();
        datetimePickerPage.getDatepickerInput().clear();
        datetimePickerPage.getDatepickerInput().sendKeys(now.format('MMM D, YYYY'));

        expect(newRepairmentPage.getSubmitButton().isEnabled()).toBeFalsy();
    });

    it('should create repairment when form is valid', () => {
        malfunctionDetailPage.getRepairmentButton().click();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+\/repair');

        expect(newRepairmentPage.getSubmitButton().isEnabled()).toBeFalsy();

        const now = moment().add(1, 'months');
        datetimePickerPage.getDatepickerInput().clear();
        datetimePickerPage.getDatepickerInput().sendKeys(now.format('MMM D, YYYY'));

        newRepairmentPage.getpriceInput().sendKeys('100');

        expect(newRepairmentPage.getSubmitButton().isEnabled()).toBeTruthy();

        newRepairmentPage.getSubmitButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');
    });

    it('should hide repairment button when repairment is already submitted', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');

        expect(newRepairmentPage.getSubmitButton().isPresent()).toBeFalsy();
    });

    it('should hide update malfunction button when repairment is already submitted', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');

        expect(malfunctionDetailPage.getUpdateButton().isPresent()).toBeFalsy();
    });

    it('should disable malfunction inputs when repairment is already submitted', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');

        expect(malfunctionDetailPage.getDescriptionInput().isEnabled()).toBeFalsy();
        expect(malfunctionDetailPage.getWorkAreaSelect().isEnabled()).toBeFalsy();
    });


});
