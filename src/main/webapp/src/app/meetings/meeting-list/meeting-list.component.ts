import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/bs-modal-ref.service';

import { AuthService } from '../../core/http/auth.service';
import { MeetingService } from '../../core/http/meeting.service';
import { ConfirmModalComponent } from '../../shared/confirm-modal/confirm-modal.component';
import { Meeting } from '../../shared/model/meeting.model';
import { Page } from '../../shared/model/page.model';
import { initialPageNumber, pageSize } from '../../shared/util/constants';

@Component({
  selector: 'bm-meeting-list',
  templateUrl: './meeting-list.component.html',
  styleUrls: ['./meeting-list.component.css']
})
export class MeetingListComponent implements OnInit {

  page = new Page();

  rows: Meeting[];

  buildingId: number;

  modalRef: BsModalRef;

  constructor(private authService: AuthService,
    private meetingService: MeetingService,
    private modalService: BsModalService,
    private route: ActivatedRoute,
    private router: Router) {
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
    this.meetingService.findAllPagable(this.page, this.buildingId)
      .subscribe(pagedData => {
        this.page.setPagedData(pagedData);
        this.rows = pagedData.content;
      },
      err => { }
      );
  }

  openConfirmDialog(row) {
    this.modalRef = this.modalService.show(ConfirmModalComponent);
    this.modalRef.content.onClose.subscribe(result => {
      if (result) {
        this.deleteMeeting(row);
      }
    });
  }

  deleteMeeting(row) {
    this.meetingService.delete(this.buildingId, row.id).subscribe(
      res => {
        this.rows = this.rows.filter(item => item !== row);
        this.page.totalElements--;
      }, err => { });
  }

  getStatus(meeting: Meeting): string {
    return meeting.date < new Date() ? 'Finished' : 'Upcoming';
  }

  isSupervisor(): boolean {
    return this.authService.isSupervisor();
  }

  scheduleMeeting(): void {
    this.router.navigate(['buildings', this.buildingId, 'meetings', 'new']);
  }

  openDetails(meeting: Meeting): void {
    this.router.navigate(['buildings', this.buildingId, 'meetings', meeting.id]);
  }
}
