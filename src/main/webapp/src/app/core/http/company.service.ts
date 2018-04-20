import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { catchError } from 'rxjs/operators';

import { Malfunction } from '../../shared/model/malfunction.model';
import { Page } from '../../shared/model/page.model';
import { PagedData } from '../../shared/model/paged-data.model';
import { Company } from '../../shared/model/user/company.model';
import { RestService } from './rest.service';

@Injectable()
export class CompanyService extends RestService<Company> {

  constructor(http: HttpClient, toastr: ToastrService) {
    super(http, ['/api/companies'], toastr);
  }

  findUnassigned(buildingId: number): Observable<Company[]> {
    return this.http.get<Company[]>(this.url(), { params: { 'buildingId': buildingId.toString() } }).pipe(
      catchError(this.handleError<Company[]>())
    );
  }

  findMalfunctions(page: Page, companyId: number): Observable<PagedData<Malfunction>> {
    return this.http.get<PagedData<Malfunction>>(
      this.url() + '/' + companyId + '/malfunctions', { params: { 'page': String(page.pageNumber), 'size': String(page.size) } }
    ).pipe(
      catchError(this.handleError<PagedData<Malfunction>>())
      );
  }
}
