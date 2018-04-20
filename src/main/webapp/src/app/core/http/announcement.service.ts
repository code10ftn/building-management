import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

import { Announcement } from '../../shared/model/announcement.model';
import { RestService } from './rest.service';

@Injectable()
export class AnnouncementService extends RestService<Announcement> {

  constructor(http: HttpClient, toastr: ToastrService) {
    super(http, ['/api/buildings', 'announcements'], toastr);
  }
}
