import { Component, Input, OnInit } from '@angular/core';

import { AuthService } from '../../core/http/auth.service';
import { BuildingService } from '../../core/http/building.service';
import { CompanyService } from '../../core/http/company.service';
import { Building } from '../../shared/model/building.model';
import { Company } from '../../shared/model/user/company.model';

@Component({
  selector: 'bm-building-companies-list',
  templateUrl: './building-companies-list.component.html',
  styleUrls: ['./building-companies-list.component.css']
})
export class BuildingCompaniesListComponent implements OnInit {

  building: Building;

  companies = new Array<Company>();

  selectedCompany: string;

  newCompany: Company;

  @Input()
  set setBuilding(building: Building) {
    this.building = building;
    this.getUnassignedCompanies();
  }

  constructor(private authService: AuthService,
    private buildingService: BuildingService,
    private companyService: CompanyService) { }

  ngOnInit() {
  }

  companySelected(event) {
    this.newCompany = event.item;
  }

  getUnassignedCompanies() {
    if (!this.isSupervisorOfBuilding()) {
      return;
    }

    this.companyService.findUnassigned(this.building.id).subscribe(
      res => {
        this.companies = res;
      },
      err => { });
  }

  assignCompany() {
    this.buildingService.updateCompanies(this.newCompany, this.building.id + '/assignCompany').subscribe(
      res => {
        this.building.companies.push(this.newCompany);
        this.selectedCompany = '';
        this.companies.splice(this.companies.indexOf(this.newCompany), 1);
      },
      err => { });
  }

  revokeCompany(company: Company) {
    this.buildingService.updateCompanies(company, this.building.id + '/revokeCompany').subscribe(
      res => {
        this.companies.push(company);
        this.building.companies.splice(this.building.companies.indexOf(company), 1);
      },
      err => { });
  }

  isSelectedCompanyValid() {
    for (const company of this.companies) {
      if (company.name === this.selectedCompany) {
        return false;
      }
    }

    return true;
  }

  isSupervisorOfBuilding() {
    return this.authService.isSupervisorOfBuilding(this.building.id);
  }
}
