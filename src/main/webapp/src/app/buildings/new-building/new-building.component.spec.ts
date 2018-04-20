import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ToastrModule } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';
import { BuildingService } from '../../core/http/building.service';
import { TokenUtilsService } from '../../core/util/token-utils.service';
import { NewBuildingComponent } from './new-building.component';

describe('NewBuildingComponent', () => {
  let component: NewBuildingComponent;
  let fixture: ComponentFixture<NewBuildingComponent>;

  beforeEach(async(() => {
    const routerMock = {
      navigateByUrl: jasmine.createSpy('navigateByUrl')
    };

    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule, FormsModule, HttpClientModule, ToastrModule.forRoot({ preventDuplicates: true })],
      declarations: [NewBuildingComponent],
      providers: [AuthService, BuildingService, TokenUtilsService, { provide: Router, useValue: routerMock }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewBuildingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
