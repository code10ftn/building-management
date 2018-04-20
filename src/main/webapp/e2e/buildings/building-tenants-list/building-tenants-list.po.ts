import { by, element } from 'protractor';

export class BuildingTenantsListPage {

    getRows() {
        return element.all(by.css('.datatable-body-row'));
    }
}
