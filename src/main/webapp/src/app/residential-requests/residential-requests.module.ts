import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { BuildingResidentialRequestComponent } from './building-residential-request/building-residential-request.component';
import { ResidentialRequestsListComponent } from './residential-requests-list/residential-requests-list.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  declarations: [BuildingResidentialRequestComponent, ResidentialRequestsListComponent],
  exports: [BuildingResidentialRequestComponent]
})
export class ResidentialRequestsModule { }
