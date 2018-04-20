import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AngularFontAwesomeModule } from 'angular-font-awesome';
import { CollapseModule } from 'ngx-bootstrap/collapse';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ModalModule } from 'ngx-bootstrap/modal';
import { TimepickerModule } from 'ngx-bootstrap/timepicker';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead';

import { ConfirmModalComponent } from './confirm-modal/confirm-modal.component';
import { DatetimePickerComponent } from './datetime-picker/datetime-picker.component';
import { WorkareaPipe } from './pipe/workarea.pipe';

@NgModule({
  imports: [
    AngularFontAwesomeModule,
    BsDatepickerModule.forRoot(),
    BsDropdownModule.forRoot(),
    CollapseModule.forRoot(),
    CommonModule,
    FormsModule,
    ModalModule.forRoot(),
    TimepickerModule.forRoot(),
    TypeaheadModule.forRoot()
  ],
  exports: [
    BsDatepickerModule,
    BsDropdownModule,
    CollapseModule,
    DatetimePickerComponent,
    FormsModule,
    TimepickerModule,
    WorkareaPipe
  ],
  declarations: [ConfirmModalComponent, DatetimePickerComponent, WorkareaPipe],
  entryComponents: [ConfirmModalComponent]
})
export class SharedModule { }
