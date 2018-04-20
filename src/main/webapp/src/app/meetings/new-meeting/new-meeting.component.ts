import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as moment from 'moment';
import { ToastrService } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';
import { MeetingService } from '../../core/http/meeting.service';
import { DatetimePickerComponent } from '../../shared/datetime-picker/datetime-picker.component';
import { MeetingCreateDto } from '../dto/meeting-create.model';
import { TopicCreateDto } from '../dto/topic-create.model';

@Component({
  selector: 'bm-new-meeting',
  templateUrl: './new-meeting.component.html',
  styleUrls: ['./new-meeting.component.css']
})
export class NewMeetingComponent implements OnInit {

  @ViewChild(DatetimePickerComponent)
  datetimePicker: DatetimePickerComponent;

  meeting = new MeetingCreateDto();
  topic: string;

  initDate: Date = new Date();

  buildingId: number;

  constructor(private authService: AuthService,
    private meetingService: MeetingService,
    private toastr: ToastrService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.buildingId = params['buildingId'];
    });
  }

  schedule(): void {
    this.meeting.date = this.datetimePicker.selectedDate;
    this.meeting.date.setHours(this.datetimePicker.selectedTime.getHours());
    this.meeting.date.setMinutes(this.datetimePicker.selectedTime.getMinutes());
    this.meeting.date.setSeconds(0);

    this.meetingService.create(this.meeting, this.buildingId).subscribe(id => {
      this.toastr.success(`Meeting schedule for ${moment(this.meeting.date).format('MMM D, Y, HH:mm')}`);
      this.router.navigate(['buildings', this.buildingId, 'meetings', id]);
    },
      err => { });
  }

  addTopic(): void {
    this.meeting.topics.push(new TopicCreateDto(this.topic));
    this.topic = '';
  }

  removeTopic(topic: TopicCreateDto): void {
    const index = this.meeting.topics.indexOf(topic);
    if (index > -1) {
      this.meeting.topics.splice(index, 1);
    }
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
}
