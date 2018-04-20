import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Params } from '@angular/router';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { ToastrModule } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';

import { BuildingService } from '../../core/http/building.service';
import { BuildingTenantListComponent } from './building-tenant-list.component';

describe('BuildingTenantListComponent', () => {
  let component: BuildingTenantListComponent;
  let fixture: ComponentFixture<BuildingTenantListComponent>;

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
      declarations: [BuildingTenantListComponent],
      imports: [BrowserAnimationsModule, HttpClientModule, NgxDatatableModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [BuildingService, { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: Router, useValue: routerMock }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildingTenantListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
