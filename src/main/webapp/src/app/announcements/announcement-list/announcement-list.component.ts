import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/bs-modal-ref.service';

import { AnnouncementService } from '../../core/http/announcement.service';
import { AuthService } from '../../core/http/auth.service';
import { ConfirmModalComponent } from '../../shared/confirm-modal/confirm-modal.component';
import { Announcement } from '../../shared/model/announcement.model';
import { Page } from '../../shared/model/page.model';
import { initialPageNumber, pageSize } from '../../shared/util/constants';
import { AnnouncementCreateDto } from '../dto/announcement-create.model';

@Component({
  selector: 'bm-announcement-list',
  templateUrl: './announcement-list.component.html',
  styleUrls: ['./announcement-list.component.css']
})
export class AnnouncementListComponent implements OnInit {

  page = new Page();

  rows: Announcement[];

  newAnnouncement = new AnnouncementCreateDto();

  buildingId: number;

  modalRef: BsModalRef;

  constructor(private route: ActivatedRoute,
    private announcementService: AnnouncementService,
    private authService: AuthService,
    private modalService: BsModalService) {
    this.page.pageNumber = initialPageNumber;
    this.page.size = pageSize;
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.buildingId = params['buildingId'];
      this.setPage({ offset: 0 });
    });
  }

  setPage(pageInfo) {
    this.page.pageNumber = pageInfo.offset;
    this.announcementService.findAllPagable(this.page, this.buildingId)
      .subscribe(pagedData => {
        this.page.setPagedData(pagedData);
        this.rows = pagedData.content;
      },
      err => { }
      );
  }

  postAnnouncement() {
    this.announcementService.create(this.newAnnouncement, this.buildingId).subscribe(res => {
      const announcement = new Announcement();
      announcement.id = res;
      announcement.author = this.authService.getAuthenticatedUser().user;
      announcement.content = this.newAnnouncement.content;
      announcement.timestamp = new Date();
      this.newAnnouncement.content = '';

      this.rows.unshift(announcement);
      this.rows = [...this.rows];
      this.page.totalElements++;
    }, err => { });
  }

  openConfirmDialog(row) {
    this.modalRef = this.modalService.show(ConfirmModalComponent);
    this.modalRef.content.onClose.subscribe(result => {
      if (result) {
        this.deleteAnnouncement(row);
      }
    });
  }

  deleteAnnouncement(row) {
    this.announcementService.delete(this.buildingId, row.id).subscribe(
      res => {
        this.rows = this.rows.filter(item => item !== row);
        this.page.totalElements--;
      }, err => { });
  }

  getName(author) {
    if (author.firstName && author.lastName) {
      return `${author.firstName} ${author.lastName}`;
    }
    return author.name;
  }

  canDelete(announcement: Announcement): boolean {
    if (this.authService.isSupervisor()) {
      return true;
    }
    if (announcement
      && announcement.author
      && announcement.author.id === this.authService.getAuthenticatedUserId()) {
      return true;
    }
  }
}
