import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { catchError } from 'rxjs/operators/catchError';

import { Page } from '../../shared/model/page.model';
import { PagedData } from '../../shared/model/paged-data.model';
import { Survey } from '../../shared/model/survey.model';
import { SurveyAnswerDto } from '../../surveys/dto/survey-answer.model';
import { RestService } from './rest.service';

@Injectable()
export class SurveyService extends RestService<Survey> {

  constructor(http: HttpClient, toastr: ToastrService) {
    super(http, ['/api/buildings', 'surveys'], toastr);
  }

  findAllPagable(page: Page, isActive: boolean, ...queryParams: any[]): Observable<PagedData<Survey>> {
    return this.http.get<PagedData<Survey>>(
      this.url(queryParams), {
        params: {
          'page': String(page.pageNumber), 'size': String(page.size),
          'isActive': String(isActive)
        }
      }
    ).pipe(
      catchError(this.handleError<PagedData<Survey>>())
      );
  }

  answerSurvey(body: SurveyAnswerDto, buildingId: number, surveyId: number): Observable<Survey> {
    return this.http.post<Survey>('/api/buildings/' + buildingId + '/surveys/' + surveyId + '/answers', body).pipe(
      catchError(this.handleError<Survey>())
    );
  }
}
