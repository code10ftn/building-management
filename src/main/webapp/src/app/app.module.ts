import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { NgxDatatableModule } from '@swimlane/ngx-datatable';

import { AuthModule } from './auth/auth.module';
import { CoreModule } from './core/core.module';
import { AppComponent } from './app.component';
import { MeetingsModule } from './meetings/meetings.module';
import { MalfunctionsModule } from './malfunctions/malfunctions.module';
import { BuildingsModule } from './buildings/buildings.module';
import { SurveysModule } from './surveys/surveys.module';
import { AnnouncementsModule } from './announcements/announcements.module';
import { ResidentialRequestsModule } from './residential-requests/residential-requests.module';

@NgModule({
  imports: [
    BrowserModule,
    AuthModule,
    CoreModule,
    BuildingsModule,
    AnnouncementsModule,
    MeetingsModule,
    MalfunctionsModule,
    SurveysModule,
    NgxDatatableModule,
    ResidentialRequestsModule
  ],
  declarations: [
    AppComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
