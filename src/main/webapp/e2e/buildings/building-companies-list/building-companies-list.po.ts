import { by, element } from 'protractor';

export class BuildingCompaniesListPage {

    getCompaniesListItems() {
        return element.all(by.css('.list-group-item'));
    }

    getCompanyInput() {
        return element(by.id('companyInput'));
    }

    getAddCompanyButton() {
        return element(by.id('addCompanyButton'));
    }

    getDeleteCompanyButtons() {
        return element.all(by.css('.fa-times'));
    }
}
