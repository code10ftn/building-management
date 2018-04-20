import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { catchError } from 'rxjs/operators';

import { SupervisorDto } from '../../buildings/dto/supervisor.dto';
import { Building } from '../../shared/model/building.model';
import { Company } from '../../shared/model/user/company.model';
import { RestService } from './rest.service';

@Injectable()
export class BuildingService extends RestService<Building> {

  constructor(http: HttpClient, toastr: ToastrService) {
    super(http, ['/api/buildings'], toastr);
  }

  addressTaken(address: string): Observable<boolean> {
    return this.http.get<boolean>(this.url(['addressTaken']), { params: { 'address': address } }).pipe(
      catchError(this.handleError<boolean>())
    );
  }

  potentialSupervisors(id: number): Observable<SupervisorDto[]> {
    return this.http.get<SupervisorDto[]>(this.url([id + '/potentialSupervisors'])).pipe(catchError(this.handleError<SupervisorDto[]>()));
  }

  updateCompanies(company: Company, ...queryParams: any[]): Observable<void> {
    return this.http.put<void>(this.url(queryParams), null, { params: { 'companyId': company.id.toString() } })
      .pipe(catchError(this.handleError<void>()));
  }
}
