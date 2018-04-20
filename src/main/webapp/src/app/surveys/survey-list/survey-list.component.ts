import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { AuthService } from '../../core/http/auth.service';
import { SurveyService } from '../../core/http/survey.service';
import { Page } from '../../shared/model/page.model';
import { initialPageNumber, pageSize } from '../../shared/util/constants';
import { Survey } from '../../shared/model/survey.model';

@Component({
  selector: 'bm-survey-list',
  templateUrl: './survey-list.component.html',
  styleUrls: ['./survey-list.component.css']
})
export class SurveyListComponent implements OnInit {

  page = new Page();

  rows: Survey[];

  buildingId: number;

  constructor(private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private surveyService: SurveyService) {
    this.page.pageNumber = initialPageNumber;
    this.page.size = pageSize;
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.buildingId = params['buildingId'];
      this.setPage({ offset: 0 });
    });
  }

  setPage(pageInfo) {
    this.page.pageNumber = pageInfo.offset;
    this.surveyService.findAllPagable(this.page, true, this.buildingId)
      .subscribe(pagedData => {
        this.page.setPagedData(pagedData);
        this.rows = pagedData.content;
      }, err => { });
  }

  getName(survey: Survey): string {
    return survey.name;
  }

  newSurvey(): void {
    this.router.navigate(['buildings', this.buildingId, 'surveys', 'new']);
  }

  answerSurvey(survey: Survey): void {
    this.router.navigate(['buildings', this.buildingId, 'surveys', survey.id, 'answer']);
  }

  surveyDetails(survey: Survey): void {
    this.router.navigate(['buildings', this.buildingId, 'surveys', survey.id]);
  }

  canAnswer(survey: Survey): boolean {
    let disabled = false;
    survey.answeredBy.forEach(tenant => {
      if (tenant.id === this.authService.getAuthenticatedUserId()) {
        disabled = true;
      }
    });

    return disabled;
  }

  isSupervisor(): boolean {
    return this.authService.isSupervisor();
  }

  isTenant(): boolean {
    return this.authService.isTenant();
  }
}
