import { HttpClientModule } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Params, Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { ToastrModule } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';

import { AuthService } from '../../core/http/auth.service';
import { BuildingService } from '../../core/http/building.service';
import { CommentService } from '../../core/http/comment.service';
import { MalfunctionService } from '../../core/http/malfunction.service';
import { TokenUtilsService } from '../../core/util/token-utils.service';
import { SharedModule } from '../../shared/shared.module';
import { MalfunctionDetailComponent } from './malfunction-detail.component';

describe('MalfunctionDetailsComponent', () => {
  let component: MalfunctionDetailComponent;
  let fixture: ComponentFixture<MalfunctionDetailComponent>;

  beforeEach(async(() => {
    const routerMock = {
      navigateByUrl: jasmine.createSpy('navigateByUrl')
    };

    const activatedRouteMock = {
      params: {
        subscribe: jasmine.createSpy('subscribe').and.returnValue(Observable.of(<Params>{ id: 1 }))
      }
    };

    TestBed.configureTestingModule({
      declarations: [MalfunctionDetailComponent],
      imports: [FormsModule, HttpClientModule, ReactiveFormsModule, SharedModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [AuthService, BuildingService, CommentService, MalfunctionService, TokenUtilsService,
        { provide: ActivatedRoute, useValue: activatedRouteMock }, { provide: Router, useValue: routerMock }],
        schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MalfunctionDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
