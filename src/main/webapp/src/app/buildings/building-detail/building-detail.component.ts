import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs/Subject';

import { AuthService } from '../../core/http/auth.service';
import { BuildingService } from '../../core/http/building.service';
import { CompanyService } from '../../core/http/company.service';
import { Building } from '../../shared/model/building.model';
import { BuildingUpdateDto } from '../dto/building.update.dto';
import { SupervisorDto } from '../dto/supervisor.dto';

@Component({
  selector: 'bm-building-detail',
  templateUrl: './building-detail.component.html',
  styleUrls: ['./building-detail.component.css']
})
export class BuildingDetailComponent implements OnInit {

  building = new Building();

  selectedSupervisor: string;

  supervisors = new Array<SupervisorDto>();

  buildingUpdate = new BuildingUpdateDto();

  buildingAddress: string;

  addressTaken: Observable<boolean>;

  addressTerms = new Subject<string>();

  constructor(private authService: AuthService,
    private buildingService: BuildingService,
    private companyService: CompanyService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.getBuilding(params['id']);
      this.getPotentialSupervisors(params['id']);
    });

    this.addressTaken = this.addressTerms.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((term: string) => this.building.address !== this.buildingAddress ? this.buildingService.addressTaken(term) : of(false))
    ).share();
  }

  checkAddressTaken(term: string): void {
    this.addressTerms.next(term);
  }

  getName(supervisor) {
    if (!supervisor) {
      return '';
    }
    if (supervisor.firstName && supervisor.lastName) {
      return `${supervisor.firstName} ${supervisor.lastName}`;
    }
    return supervisor.name;
  }

  getBuilding(id: number): void {
    this.buildingService.findById(id).subscribe(
      res => {
        this.building = res;
        this.selectedSupervisor = this.getName(this.building.supervisor);
        this.buildingAddress = this.building.address;
      },
      err => {
        this.router.navigateByUrl('buildings');
      });
  }

  getPotentialSupervisors(id: number) {
    if (!this.isAdmin()) {
      return;
    }

    this.buildingService.potentialSupervisors(id).subscribe(
      res => {
        this.supervisors = res;
      }, err => { });
  }

  update() {
    this.buildingUpdate.address = this.building.address;

    this.buildingService.update(this.buildingUpdate, this.building.id).subscribe(
      res => {
        this.router.navigateByUrl('buildings');
      }, err => { });
  }

  supervisorSelected(event) {
    this.buildingUpdate.supervisorId = event.item.supervisorId;
  }

  isAdmin() {
    return this.authService.isAdmin();
  }

  isSupervisorOfBuilding() {
    return this.authService.isSupervisorOfBuilding(this.building.id);
  }

  isAssignedToBuilding() {
    return this.authService.isAssignedToBuilding(this.building.id);
  }

  isCompany() {
    return this.authService.isCompany();
  }
}
