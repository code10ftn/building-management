import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

import { Comment } from '../../shared/model/comment.model';
import { RestService } from './rest.service';

@Injectable()
export class CommentService extends RestService<Comment> {

  constructor(http: HttpClient, toastr: ToastrService) {
    super(http, ['/api/buildings', 'malfunctions', 'comments'], toastr);
  }
}
