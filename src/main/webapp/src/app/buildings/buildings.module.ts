import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead/typeahead.module';

import { ResidentialRequestsModule } from '../residential-requests/residential-requests.module';
import { SharedModule } from '../shared/shared.module';
import { BuildingCompaniesListComponent } from './building-companies-list/building-companies-list.component';
import { BuildingDetailComponent } from './building-detail/building-detail.component';
import { BuildingListComponent } from './building-list/building-list.component';
import { BuildingTenantListComponent } from './building-tenant-list/building-tenant-list.component';
import { NewBuildingComponent } from './new-building/new-building.component';

@NgModule({
  imports: [
    CommonModule,
    NgxDatatableModule,
    ResidentialRequestsModule,
    RouterModule,
    SharedModule,
    TypeaheadModule,
  ],
  declarations: [BuildingCompaniesListComponent, BuildingDetailComponent, BuildingListComponent,
    BuildingTenantListComponent, NewBuildingComponent]
})
export class BuildingsModule { }
