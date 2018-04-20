import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ToastrModule } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';
import { ResidentialRequestService } from '../../core/http/residential-request.service';
import { TokenUtilsService } from '../../core/util/token-utils.service';
import { Building } from '../../shared/model/building.model';
import { BuildingResidentialRequestComponent } from './building-residential-request.component';

describe('BuildingResidentialRequestComponent', () => {
  let component: BuildingResidentialRequestComponent;
  let fixture: ComponentFixture<BuildingResidentialRequestComponent>;

  beforeEach(async(() => {
    const routerMock = {
      navigateByUrl: jasmine.createSpy('navigateByUrl')
    };

    TestBed.configureTestingModule({
      declarations: [BuildingResidentialRequestComponent],
      imports: [BrowserAnimationsModule, HttpClientModule, FormsModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [AuthService, ResidentialRequestService, TokenUtilsService, { provide: Router, useValue: routerMock }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildingResidentialRequestComponent);
    component = fixture.componentInstance;
    component.setBuilding = new Building();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
