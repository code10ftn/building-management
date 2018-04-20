import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';

import { AppRoutingModule } from '../routes/app-routing.module';
import { SharedModule } from '../shared/shared.module';
import { MeetingDetailComponent } from './meeting-detail/meeting-detail.component';
import { MeetingListComponent } from './meeting-list/meeting-list.component';
import { NewMeetingComponent } from './new-meeting/new-meeting.component';

@NgModule({
  imports: [
    AppRoutingModule,
    CommonModule,
    NgxDatatableModule,
    SharedModule
  ],
  declarations: [MeetingDetailComponent, MeetingListComponent, NewMeetingComponent]
})
export class MeetingsModule { }
