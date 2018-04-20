import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

import { Tenant } from '../../shared/model/user/tenant.model';
import { RestService } from './rest.service';

@Injectable()
export class TenantService extends RestService<Tenant> {

  constructor(http: HttpClient, toastr: ToastrService) {
    super(http, ['/api/tenants'], toastr);
  }
}
