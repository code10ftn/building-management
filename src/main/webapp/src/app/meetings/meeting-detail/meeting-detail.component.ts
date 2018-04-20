import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as moment from 'moment';
import { ToastrService } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';
import { MeetingService } from '../../core/http/meeting.service';
import { TopicService } from '../../core/http/topic.service';
import { DatetimePickerComponent } from '../../shared/datetime-picker/datetime-picker.component';
import { Malfunction } from '../../shared/model/malfunction.model';
import { Meeting } from '../../shared/model/meeting.model';
import { Survey } from '../../shared/model/survey.model';
import { Topic } from '../../shared/model/topic.model';
import { ReportCreateDto } from '../dto/report-create.model';
import { TopicCommentDto } from '../dto/topic-comment.model';
import { TopicCreateDto } from '../dto/topic-create.model';

@Component({
  selector: 'bm-meeting-detail',
  templateUrl: './meeting-detail.component.html',
  styleUrls: ['./meeting-detail.component.css']
})
export class MeetingDetailComponent implements OnInit {

  @ViewChild(DatetimePickerComponent)
  datetimePicker: DatetimePickerComponent;

  meeting = new Meeting();

  topic: string;

  newReport = new ReportCreateDto();

  reportMode = false;

  buildingId: number;

  meetingId: number;

  constructor(
    private authService: AuthService,
    private meetingService: MeetingService,
    private route: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService,
    private topicService: TopicService) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.buildingId = params['buildingId'];
      this.meetingId = params['meetingId'];
      this.getMeeting();
    });
  }

  getMeeting(): void {
    this.meetingService.findById(this.buildingId, this.meetingId).subscribe(res => {
      this.meeting = res;
      this.initDatetimePicker();
      this.initTopicComments();
    }, err => {
      this.router.navigate(['buildings', this.buildingId, 'meetings']);
    });
  }

  initDatetimePicker(): void {
    if (this.datetimePicker != null) {
      this.datetimePicker.selectedDate = new Date(this.meeting.date);
      this.datetimePicker.selectedTime = new Date(this.meeting.date);
    }
  }

  initTopicComments(): void {
    for (let i = 0; i < this.meeting.topics.length; i++) {
      const topicComment = new TopicCommentDto();
      topicComment.topicId = this.meeting.topics[i].id;
      topicComment.comment = this.meeting.topics[i].comment;
      this.newReport.comments.push(topicComment);
    }
    if (this.meeting.report && this.meeting.report.comment) {
      this.newReport.comment = this.meeting.report.comment;
    }
  }

  addTopic(): void {
    const topicCreateDto = new TopicCreateDto(this.topic);
    this.topicService.create(topicCreateDto, this.buildingId, this.meetingId).subscribe(res => {
      const topic = new Topic();
      topic.id = res;
      topic.content = topicCreateDto.content;
      topic.accepted = this.isSupervisor();
      this.meeting.topics.push(topic);
    });
    this.topic = '';
  }

  removeTopic(topic: Topic): void {
    this.topicService.delete(this.buildingId, this.meetingId, topic.id).subscribe(res => {
      const index = this.meeting.topics.indexOf(topic);
      if (index > -1) {
        this.meeting.topics.splice(index, 1);
      }
    }, err => { });
  }

  setTopicStatus(topic: Topic): void {
    topic.accepted = !topic.accepted;
    this.topicService.update(topic, this.buildingId, this.meetingId, topic.id).subscribe(res => {
    }, err => { });
  }

  updateMeeting(): void {
    const meetingUpdate = new Meeting();
    meetingUpdate.date = this.datetimePicker.selectedDate;
    meetingUpdate.date.setHours(this.datetimePicker.selectedTime.getHours());
    meetingUpdate.date.setMinutes(this.datetimePicker.selectedTime.getMinutes());
    meetingUpdate.date.setSeconds(0);

    this.meetingService.update(meetingUpdate, this.buildingId, this.meeting.id).subscribe(res => {
      this.toastr.success('Meeting rescheduled!');
      this.meeting.date = new Date(meetingUpdate.date);
    }, err => { });
  }

  enableReportMode(): void {
    this.reportMode = true;
  }

  saveReport(): void {
    this.meetingService.update<ReportCreateDto>(this.newReport, this.buildingId, this.meetingId, 'report').subscribe(res => {
      this.reportMode = false;
      this.meeting = res;
    }, err => { });
  }

  cancelReport(): void {
    this.reportMode = false;
  }

  meetingPassed(): boolean {
    return moment(this.meeting.date).diff(moment()) < 0;
  }

  isSupervisor(): boolean {
    return this.authService.isSupervisor();
  }

  canReschedule(): boolean {
    return this.isSupervisor() && !this.meetingPassed();
  }

  canRemoveTopic(): boolean {
    return this.isSupervisor() && !this.meetingPassed();
  }

  canCreateReport(): boolean {
    return this.isSupervisor() && this.meetingPassed() && !this.meeting.report && !this.reportMode;
  }

  canEditReport(): boolean {
    return this.isSupervisor() && this.meetingPassed() && this.meeting.report && !this.reportMode;
  }

  shouldShowReport(): boolean {
    return this.meetingPassed() && !!this.meeting.report && !this.reportMode;
  }

  datePassed(): boolean {
    if (!this.datetimePicker || !this.datetimePicker.selectedDate) {
      return true;
    }

    const date = this.datetimePicker.selectedDate;
    date.setHours(this.datetimePicker.selectedTime.getHours());
    date.setMinutes(this.datetimePicker.selectedTime.getMinutes());
    date.setSeconds(0);
    return moment(date).diff(moment()) < 0;
  }

  openMalfunction(malfunction: Malfunction): void {
    this.router.navigate(['buildings', this.buildingId, 'malfunctions', malfunction.id]);
  }

  openSurvey(survey: Survey): void {
    this.router.navigate(['buildings', this.buildingId, 'surveys', survey.id]);
  }
}
