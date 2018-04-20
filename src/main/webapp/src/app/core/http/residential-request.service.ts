import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';

import { ResidentialRequest } from '../../shared/model/residential-request.model';
import { RestService } from './rest.service';

@Injectable()
export class ResidentialRequestService extends RestService<ResidentialRequest> {

  constructor(http: HttpClient, toastr: ToastrService) {
    super(http, ['/api/buildings', 'residentialRequests'], toastr);
  }

  findMyRequest(...queryParams: any[]): Observable<ResidentialRequest> {
    return this.http.get<ResidentialRequest>(this.url(queryParams));
  }
}
