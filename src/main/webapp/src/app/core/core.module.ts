import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';

import { AppRoutingModule } from '.././routes/app-routing.module';
import { SharedModule } from '.././shared/shared.module';
import { AnnouncementService } from './http/announcement.service';
import { AuthService } from './http/auth.service';
import { BuildingService } from './http/building.service';
import { CommentService } from './http/comment.service';
import { CompanyService } from './http/company.service';
import { MalfunctionService } from './http/malfunction.service';
import { MeetingService } from './http/meeting.service';
import { ResidentialRequestService } from './http/residential-request.service';
import { SurveyService } from './http/survey.service';
import { TenantService } from './http/tenant.service';
import { TokenInterceptorService } from './http/token-interceptor.service';
import { TopicService } from './http/topic.service';
import { NavComponent } from './nav/nav.component';
import { TokenUtilsService } from './util/token-utils.service';

@NgModule({
  imports: [
    AppRoutingModule,
    BrowserAnimationsModule,
    CommonModule,
    HttpClientModule,
    SharedModule,
    ToastrModule.forRoot({
      preventDuplicates: true,
      positionClass: 'toast-position'
    }),
  ],
  declarations: [NavComponent],
  providers: [
    AnnouncementService,
    AuthService,
    BuildingService,
    CommentService,
    CompanyService,
    MalfunctionService,
    MeetingService,
    ResidentialRequestService,
    SurveyService,
    TenantService,
    TokenUtilsService,
    TopicService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptorService,
      multi: true
    }
  ],
  exports: [
    AppRoutingModule,
    NavComponent
  ]
})
export class CoreModule { }
