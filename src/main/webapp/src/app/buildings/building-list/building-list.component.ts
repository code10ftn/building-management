import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/bs-modal-ref.service';

import { AuthService } from '../../core/http/auth.service';
import { BuildingService } from '../../core/http/building.service';
import { ConfirmModalComponent } from '../../shared/confirm-modal/confirm-modal.component';
import { Building } from '../../shared/model/building.model';
import { Page } from '../../shared/model/page.model';
import { initialPageNumber, pageSize } from '../../shared/util/constants';

@Component({
  selector: 'bm-building-list',
  templateUrl: './building-list.component.html',
  styleUrls: ['./building-list.component.css']
})
export class BuildingListComponent implements OnInit {

  modalRef: BsModalRef;

  page = new Page();

  rows: Building[];

  constructor(private authService: AuthService,
    private buildingService: BuildingService,
    private modalService: BsModalService,
    private router: Router) {
    this.page.pageNumber = initialPageNumber;
    this.page.size = pageSize;
  }

  ngOnInit() {
    this.setPage({ offset: 0 });
  }

  setPage(pageInfo) {
    this.page.pageNumber = pageInfo.offset;
    this.buildingService.findAllPagable(this.page).subscribe(
      pagedData => {
        this.page.setPagedData(pagedData);
        this.rows = pagedData.content;
      },
      err => { });
  }

  getName(supervisor) {
    if (supervisor.firstName && supervisor.lastName) {
      return `${supervisor.firstName} ${supervisor.lastName}`;
    }
    return supervisor.name;
  }

  goToNewBuilding() {
    this.router.navigateByUrl('buildings/new');
  }

  openConfirmDialog(row) {
    this.modalRef = this.modalService.show(ConfirmModalComponent);
    this.modalRef.content.onClose.subscribe(result => {
      if (result) {
        this.deleteBuilding(row);
      }
    });
  }

  deleteBuilding(row) {
    this.buildingService.delete(row.id).subscribe(
      res => {
        this.rows = this.rows.filter(item => item !== row);
        this.page.totalElements--;
      }, err => { });
  }

  showBuildingDetails(row) {
    this.router.navigate(['buildings', row.id]);
  }

  isAdmin() {
    return this.authService.isAdmin();
  }

  isSupervisor(): boolean {
    return this.authService.isSupervisor();
  }

  isSupervising(building: Building): string {
    return building.supervisor.id === this.authService.getAuthenticatedUserId() ? 'Yes' : 'No';
  }
}
