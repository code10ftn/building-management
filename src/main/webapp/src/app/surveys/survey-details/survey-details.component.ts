import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { SurveyService } from '../../core/http/survey.service';
import { Survey } from '../../shared/model/survey.model';

@Component({
  selector: 'bm-survey-details',
  templateUrl: './survey-details.component.html',
  styleUrls: ['./survey-details.component.css']
})
export class SurveyDetailsComponent implements OnInit {

  survey: Survey = new Survey();

  buildingId: number;

  surveyId: number;

  constructor(private surveyService: SurveyService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.buildingId = params['buildingId'];
      this.surveyId = params['surveyId'];
      this.getSurvey();
    });
  }

  getSurvey(): void {
    this.surveyService.findById(this.buildingId, this.surveyId).subscribe(res => {
      this.survey = res;
    }, err => {
      this.router.navigate(['buildings', this.buildingId, 'surveys']);
    });
  }
}
