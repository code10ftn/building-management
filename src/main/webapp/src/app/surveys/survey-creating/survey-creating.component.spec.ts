import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';

import { AuthService } from '../../core/http/auth.service';
import { SurveyService } from '../../core/http/survey.service';
import { TokenUtilsService } from '../../core/util/token-utils.service';
import { SharedModule } from '../../shared/shared.module';
import { SurveyCreatingComponent } from './survey-creating.component';

describe('SurveyCreatingComponent', () => {
  let component: SurveyCreatingComponent;
  let fixture: ComponentFixture<SurveyCreatingComponent>;

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
      declarations: [SurveyCreatingComponent],
      imports: [BrowserAnimationsModule, HttpClientModule, SharedModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [AuthService, SurveyService, ToastrService, TokenUtilsService,
        { provide: ActivatedRoute, useValue: activatedRouteMock }, { provide: Router, useValue: routerMock }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyCreatingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
