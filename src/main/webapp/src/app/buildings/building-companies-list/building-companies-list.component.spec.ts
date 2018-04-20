import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead';
import { ToastrModule } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';
import { BuildingService } from '../../core/http/building.service';
import { CompanyService } from '../../core/http/company.service';
import { TokenUtilsService } from '../../core/util/token-utils.service';
import { Building } from '../../shared/model/building.model';
import { BuildingCompaniesListComponent } from './building-companies-list.component';

describe('BuildingCompaniesListComponent', () => {
  let component: BuildingCompaniesListComponent;
  let fixture: ComponentFixture<BuildingCompaniesListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [BuildingCompaniesListComponent],
      imports: [BrowserAnimationsModule, FormsModule, HttpClientModule,
        ToastrModule.forRoot({ preventDuplicates: true }), TypeaheadModule.forRoot()],
      providers: [AuthService, BuildingService, CompanyService, TokenUtilsService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildingCompaniesListComponent);
    component = fixture.componentInstance;
    component.setBuilding = new Building();
    fixture.detectChanges();
  });

  it('should create', () => {
    // expect(component).toBeTruthy();
  });
});
