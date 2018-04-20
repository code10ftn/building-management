import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead';
import { ToastrModule } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';

import { AuthService } from '../../core/http/auth.service';
import { BuildingService } from '../../core/http/building.service';
import { CompanyService } from '../../core/http/company.service';
import { ResidentialRequestService } from '../../core/http/residential-request.service';
import { TokenUtilsService } from '../../core/util/token-utils.service';
import { ResidentialRequestsModule } from '../../residential-requests/residential-requests.module';
import { BuildingCompaniesListComponent } from '../building-companies-list/building-companies-list.component';
import { BuildingDetailComponent } from './building-detail.component';

describe('BuildingDetailComponent', () => {
  let component: BuildingDetailComponent;
  let fixture: ComponentFixture<BuildingDetailComponent>;

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
      declarations: [BuildingCompaniesListComponent, BuildingDetailComponent],
      imports: [FormsModule, HttpClientModule, ResidentialRequestsModule,
        TypeaheadModule.forRoot(), ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [AuthService, BuildingService, CompanyService, ResidentialRequestService, TokenUtilsService,
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: Router, useValue: routerMock }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildingDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
