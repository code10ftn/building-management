import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { ToastrModule } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';

import { AuthService } from '../../core/http/auth.service';
import { CompanyService } from '../../core/http/company.service';
import { MalfunctionService } from '../../core/http/malfunction.service';
import { TokenUtilsService } from '../../core/util/token-utils.service';
import { MalfunctionListComponent } from './malfunction-list.component';

describe('MalfunctionListComponent', () => {
  let component: MalfunctionListComponent;
  let fixture: ComponentFixture<MalfunctionListComponent>;

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
      declarations: [MalfunctionListComponent],
      imports: [HttpClientModule, NgxDatatableModule, ReactiveFormsModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [AuthService, CompanyService, MalfunctionService, TokenUtilsService,
        { provide: ActivatedRoute, useValue: activatedRouteMock }, { provide: Router, useValue: routerMock }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MalfunctionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
