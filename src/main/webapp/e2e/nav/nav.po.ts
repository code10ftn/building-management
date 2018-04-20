import { by, element } from 'protractor';

export class NavPage {

    getBuildingsButton(): any {
        return element(by.css('#buildingsButton'));
    }

    getMeetingsButton(): any {
        return element(by.id('meetingsButton'));
    }

    getSignoutButton() {
        return element(by.id('signoutButton'));
    }

    getMalfunctionsButton() {
        return element(by.id('malfunctionsButton'));
    }

    getResidentialRequestsButton() {
        return element(by.id('requestsButton'));
    }

    getAnnouncementsButton() {
        return element(by.id('announcementsButton'));
    }

    getCompanyMalfunctions() {
        return element(by.id('companyMalfunctionsButton'));
    }

    getSurveysButton() {
        return element(by.id('surveysButton'));
    }
}
