import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { ResidentialRequestDto } from '../../buildings/dto/residential-request.dto';
import { AuthService } from '../../core/http/auth.service';
import { ResidentialRequestService } from '../../core/http/residential-request.service';
import { Building } from '../../shared/model/building.model';
import { ResidentialRequest } from '../../shared/model/residential-request.model';

@Component({
  selector: 'bm-building-residential-request',
  templateUrl: './building-residential-request.component.html',
  styleUrls: ['./building-residential-request.component.css']
})
export class BuildingResidentialRequestComponent implements OnInit {

  building: Building;

  residentialRequestDto = new ResidentialRequestDto();

  currentResidentialRequest = new ResidentialRequest();

  @Input()
  set setBuilding(building: Building) {
    this.building = building;
    this.getMyResidentialRequest();
  }

  constructor(private authService: AuthService,
    private residentialRequestService: ResidentialRequestService,
    private router: Router) { }

  ngOnInit() {
  }

  canRequestResidence() {
    return this.authService.isTenant() && this.authService.getBuildingId() !== this.building.id
      && this.currentResidentialRequest && this.currentResidentialRequest.apartment.building.id !== this.building.id;
  }

  hasResidentialRequestForBuilding() {
    return this.currentResidentialRequest && this.currentResidentialRequest.apartment.building.id === this.building.id;
  }

  getMyResidentialRequest() {
    if (!this.building || !this.building.id || !this.authService.isTenant()) {
      return;
    }

    this.residentialRequestService.findMyRequest(this.building.id, 'my').subscribe(
      request => {
        this.currentResidentialRequest = request;
      },
      err => {
        console.error('No residential request for current user');
      });
  }

  requestResidence() {
    this.residentialRequestService.create<ResidentialRequestDto>(this.residentialRequestDto,
      this.building.id).subscribe(
      id => {
        this.router.navigateByUrl('buildings');
      },
      err => { });
  }

  cancelResidentialRequest() {
    this.residentialRequestService.delete(this.building.id, this.currentResidentialRequest.id).subscribe(
      response => {
        this.router.navigateByUrl('buildings');
      },
      err => { });
  }
}
