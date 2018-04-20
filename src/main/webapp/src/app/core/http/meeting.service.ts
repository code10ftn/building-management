import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

import { Meeting } from '../../shared/model/meeting.model';
import { RestService } from './rest.service';

@Injectable()
export class MeetingService extends RestService<Meeting> {

  constructor(http: HttpClient, toastr: ToastrService) {
    super(http, ['/api/buildings', 'meetings'], toastr);
  }
}
