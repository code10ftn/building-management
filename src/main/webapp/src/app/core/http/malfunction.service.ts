import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { catchError } from 'rxjs/operators';

import { AssigneeForwardDto } from '../../malfunctions/dto/assignee-forward.model';
import { MalfunctionRepairDto } from '../../malfunctions/dto/malfunction-repair.model';
import { Malfunction } from '../../shared/model/malfunction.model';
import { RestService } from './rest.service';

@Injectable()
export class MalfunctionService extends RestService<Malfunction> {

  constructor(http: HttpClient, toastr: ToastrService) {
    super(http, ['/api/buildings', 'malfunctions'], toastr);
  }

  repair(body: MalfunctionRepairDto, buildingId: number, malfunctionId: number): Observable<Malfunction> {
    return this.http.post<Malfunction>('api/buildings/' + buildingId + '/malfunctions/' + malfunctionId + '/repairment', body).pipe(
      catchError(this.handleError<Malfunction>())
    );
  }

  forward(body: AssigneeForwardDto, buildingId: number, malfunctionId: number): Observable<Malfunction> {
    return this.http.put<Malfunction>('api/buildings/' + buildingId + '/malfunctions/' + malfunctionId + '/forward', body).pipe(
      catchError(this.handleError<Malfunction>())
    );
  }

  upoadImage(body: any, buildingId: number, malfunctionId: number): Observable<any> {
    return this.http.post<any>('api/buildings/' + buildingId + '/malfunctions/' + malfunctionId + '/image', body).pipe(
      catchError(this.handleError<any>())
    );
  }

  getImage(buildingId: number, malfunctionId: number): Observable<Blob> {
    return this.http.get('api/buildings/' + buildingId + '/malfunctions/' + malfunctionId + '/image', {
      responseType: 'blob'
    });
  }
}
