import { Component, OnInit } from '@angular/core';

import { ResidentialRequestService } from '../../core/http/residential-request.service';
import { ResidentialRequest } from '../../shared/model/residential-request.model';

@Component({
  selector: 'bm-residential-requests-list',
  templateUrl: './residential-requests-list.component.html',
  styleUrls: ['./residential-requests-list.component.css']
})
export class ResidentialRequestsListComponent implements OnInit {

  residentialRequests: ResidentialRequest[];

  constructor(private residentialRequestService: ResidentialRequestService) { }

  ngOnInit() {
    this.residentialRequestService.findAll(0).subscribe(
      res => {
        this.residentialRequests = res;
      },
      err => { });
  }

  acceptRequest(request: ResidentialRequest) {
    this.residentialRequestService.update(request, request.apartment.building.id, request.id).subscribe(
      res => { this.residentialRequests.splice(this.residentialRequests.indexOf(request), 1); },
      err => { });
  }

  declineRequest(request: ResidentialRequest) {
    this.residentialRequestService.delete(request.apartment.building.id, request.id).subscribe(
      res => { this.residentialRequests.splice(this.residentialRequests.indexOf(request), 1); },
      err => { });
  }
}
