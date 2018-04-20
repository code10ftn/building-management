import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ToastrModule } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';

import { AuthService } from '../../core/http/auth.service';
import { MeetingService } from '../../core/http/meeting.service';
import { TokenUtilsService } from '../../core/util/token-utils.service';
import { MeetingListComponent } from './meeting-list.component';

describe('MeetingListComponent', () => {
  let component: MeetingListComponent;
  let fixture: ComponentFixture<MeetingListComponent>;

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
      declarations: [MeetingListComponent],
      imports: [BrowserAnimationsModule, BsDropdownModule.forRoot(), HttpClientModule, NgxDatatableModule,
        ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [AuthService, BsModalService, MeetingService, TokenUtilsService,
        { provide: ActivatedRoute, useValue: activatedRouteMock }, { provide: Router, useValue: routerMock }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MeetingListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
