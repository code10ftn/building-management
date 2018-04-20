import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';

import { AuthService } from '../../core/http/auth.service';
import { MeetingService } from '../../core/http/meeting.service';
import { TopicService } from '../../core/http/topic.service';
import { TokenUtilsService } from '../../core/util/token-utils.service';
import { SharedModule } from '../../shared/shared.module';
import { MeetingDetailComponent } from './meeting-detail.component';

describe('MeetingDetailComponent', () => {
  let component: MeetingDetailComponent;
  let fixture: ComponentFixture<MeetingDetailComponent>;

  beforeEach(async(() => {
    const activatedRouteMock = {
      params: {
        subscribe: jasmine.createSpy('subscribe').and.returnValue(Observable.of(<Params>{ id: 1 }))
      }
    };

    const routerMock = {
      navigateByUrl: jasmine.createSpy('navigateByUrl')
    };

    TestBed.configureTestingModule({
      declarations: [MeetingDetailComponent],
      imports: [BrowserAnimationsModule, HttpClientModule, SharedModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [AuthService, MeetingService, ToastrService, TokenUtilsService, TopicService,
        { provide: ActivatedRoute, useValue: activatedRouteMock }, { provide: Router, useValue: routerMock }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MeetingDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
