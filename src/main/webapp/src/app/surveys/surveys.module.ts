import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';

import { SharedModule } from '../shared/shared.module';
import { SurveyAnsweringComponent } from './survey-answering/survey-answering.component';
import { SurveyCreatingComponent } from './survey-creating/survey-creating.component';
import { SurveyDetailsComponent } from './survey-details/survey-details.component';
import { SurveyListComponent } from './survey-list/survey-list.component';

@NgModule({
  imports: [
    CommonModule,
    NgxDatatableModule,
    SharedModule
  ],
  declarations: [SurveyAnsweringComponent, SurveyCreatingComponent, SurveyDetailsComponent, SurveyListComponent]
})
export class SurveysModule { }
