import { by, element, ElementArrayFinder } from 'protractor';

export class MalfunctionListPage {

    getReportMalfunctionButton() {
        return element(by.id('reportMalfunctionButton'));
    }

    getDetailsButton(malfunctionId: string) {
        return element(by.id('details_' + malfunctionId));
    }

    getRows(): ElementArrayFinder {
        return element.all(by.css('.datatable-body-row'));
    }

    getMalfunctionInfoButtons() {
        return element.all(by.css('.fa-info-circle'));
    }
}
