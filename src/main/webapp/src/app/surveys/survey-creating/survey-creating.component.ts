import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';
import { SurveyService } from '../../core/http/survey.service';
import { DatetimePickerComponent } from '../../shared/datetime-picker/datetime-picker.component';
import { QuestionCreateDto } from '../dto/question-create.model';
import { SurveyCreateDto } from '../dto/survey-create.model';

@Component({
  selector: 'bm-survey-creating',
  templateUrl: './survey-creating.component.html',
  styleUrls: ['./survey-creating.component.css']
})
export class SurveyCreatingComponent implements OnInit {

  @ViewChild(DatetimePickerComponent)
  datetimePicker: DatetimePickerComponent;

  survey: SurveyCreateDto;

  minAnswersNum = 2;

  minQuestionCharacters = 10;

  constructor(private authService: AuthService,
    private surveyService: SurveyService,
    private router: Router,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.survey = new SurveyCreateDto();
  }

  addQuestion(): void {
    this.survey.questions.push(new QuestionCreateDto());
  }

  addAnswer(question: QuestionCreateDto): void {
    question.answers.push('');
  }

  createSurvey(): void {
    this.survey.expirationDate = this.datetimePicker.selectedDate;
    this.survey.expirationDate.setHours(this.datetimePicker.selectedTime.getHours());
    this.survey.expirationDate.setMinutes(this.datetimePicker.selectedTime.getMinutes());
    this.survey.expirationDate.setSeconds(0);

    this.surveyService.create(this.survey, this.authService.getBuildingId()).subscribe(id => {
      this.toastr.success('Survey created'),
        this.router.navigate(['buildings', this.authService.getBuildingId(), 'surveys']);
    });
  }

  deleteQuestion(question: QuestionCreateDto): void {
    const index = this.survey.questions.indexOf(question);
    if (index > -1) {
      this.survey.questions.splice(index, 1);
    }
  }

  deleteAnswer(question: QuestionCreateDto, answer: string): void {
    const index = question.answers.indexOf(answer);
    if (index > -1) {
      question.answers.splice(index, 1);
    }
  }

  invalidName(): boolean {
    return this.survey.name.length === 0;
  }

  invalidQuestionAnswerNum(question: QuestionCreateDto): boolean {
    return question.answers.length < this.minAnswersNum;
  }

  invalidQuestionCharactersNum(question: QuestionCreateDto): boolean {
    return question.question && question.question.length < this.minQuestionCharacters;
  }

  createDisabled(): boolean {
    let disabled = false;
    this.survey.questions.forEach(s => {
      if (this.invalidQuestionAnswerNum(s)) {
        disabled = true;
      }
      if (this.invalidQuestionCharactersNum(s)) {
        disabled = true;
      }
      if (this.invalidName()) {
        disabled = true;
      }
    });
    return disabled;
  }

  trackByFn(index: any, item: any) {
    return index;
  }
}
