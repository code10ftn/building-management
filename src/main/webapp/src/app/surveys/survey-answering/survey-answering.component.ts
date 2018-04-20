import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';
import { SurveyService } from '../../core/http/survey.service';
import { Survey } from '../../shared/model/survey.model';
import { SurveyAnswerDto } from '../dto/survey-answer.model';

@Component({
  selector: 'bm-survey-answering',
  templateUrl: './survey-answering.component.html',
  styleUrls: ['./survey-answering.component.css']
})
export class SurveyAnsweringComponent implements OnInit {

  survey: Survey = new Survey();

  answerDto = new SurveyAnswerDto();

  buildingId: number;

  surveyId: number;

  constructor(private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private surveyService: SurveyService,
    private toastr: ToastrService) { }

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
      this.answerDto.answerIds = Array(this.survey.questions.length).fill(-1);

      if (this.alreadyAnswered()) {
        this.router.navigate(['buildings', this.authService.getBuildingId(), 'surveys']);
      }

    }, err => {
      this.router.navigate(['buildings', this.buildingId, 'surveys']);
    });
  }

  answerSurvey(): void {
    this.surveyService.answerSurvey(this.answerDto, this.buildingId, this.surveyId).subscribe(survey => {
      this.toastr.success('Survey answered'),
        this.router.navigate(['buildings', this.authService.getBuildingId(), 'surveys']);
    });
  }

  checkAnswer(answerId: number, questionIndex: number): void {
    this.answerDto.answerIds.splice(questionIndex, 1, answerId);
  }

  alreadyAnswered(): boolean {
    let answered = false;
    this.survey.answeredBy.forEach(tenant => {
      if (tenant.id === this.authService.getAuthenticatedUserId()) {
        answered = true;
      }
    });

    return answered;
  }

  canAnswer(): boolean {
    let disabled = false;
    this.answerDto.answerIds.forEach(id => {
      if (id === -1) {
        disabled = true;
      }
    });

    return disabled;
  }
}
