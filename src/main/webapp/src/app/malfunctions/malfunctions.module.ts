import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';

import { CoreModule } from '../core/core.module';
import { SharedModule } from '../shared/shared.module';
import { CommentListComponent } from './comment-list/comment-list.component';
import { ImageUploadComponent } from './image-upload/image-upload.component';
import { MalfunctionDetailComponent } from './malfunction-detail/malfunction-detail.component';
import { MalfunctionListComponent } from './malfunction-list/malfunction-list.component';
import { NewMalfunctionComponent } from './new-malfunction/new-malfunction.component';
import { NewRepairmentComponent } from './new-repairment/new-repairment.component';

@NgModule({
  imports: [
    CommonModule,
    CoreModule,
    NgxDatatableModule,
    ReactiveFormsModule,
    SharedModule
  ],
  declarations: [CommentListComponent, ImageUploadComponent, MalfunctionDetailComponent, MalfunctionListComponent, NewMalfunctionComponent,
    NewRepairmentComponent]
})
export class MalfunctionsModule { }
