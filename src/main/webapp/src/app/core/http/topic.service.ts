import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

import { Topic } from '../../shared/model/topic.model';
import { RestService } from './rest.service';

@Injectable()
export class TopicService extends RestService<Topic> {

  constructor(http: HttpClient, toastr: ToastrService) {
    super(http, ['/api/buildings', 'meetings', 'topics'], toastr);
  }
}
