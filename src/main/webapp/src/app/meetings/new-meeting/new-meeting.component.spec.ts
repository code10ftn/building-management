import 'rxjs/add/observable/of';

import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Params, Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { ToastrModule } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';

import { AuthService } from '../../core/http/auth.service';
import { MeetingService } from '../../core/http/meeting.service';
import { TokenUtilsService } from '../../core/util/token-utils.service';
import { SharedModule } from '../../shared/shared.module';
import { NewMeetingComponent } from './new-meeting.component';

describe('NewMeetingComponent', () => {
  let component: NewMeetingComponent;
  let fixture: ComponentFixture<NewMeetingComponent>;

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
      declarations: [NewMeetingComponent],
      imports: [HttpClientModule, SharedModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [AuthService, MeetingService, TokenUtilsService,
        { provide: ActivatedRoute, useValue: activatedRouteMock }, { provide: Router, useValue: routerMock }]
    })
      .compileComponents();

    this.router = TestBed.get(Router);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewMeetingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
