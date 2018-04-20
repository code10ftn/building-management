import { Component, Input, OnInit } from '@angular/core';
import * as moment from 'moment';
import { BsDatepickerConfig } from 'ngx-bootstrap/datepicker';

@Component({
  selector: 'bm-datetime-picker',
  templateUrl: './datetime-picker.component.html',
  styleUrls: ['./datetime-picker.component.css']
})
export class DatetimePickerComponent implements OnInit {

  bsConfig: Partial<BsDatepickerConfig> = Object.assign({}, { containerClass: 'theme-default', dateInputFormat: 'MMM D, Y' });

  minDate: Date = new Date();

  maxDate: Date = moment().add(1, 'years').toDate();

  @Input() selectedDate: Date = new Date();

  @Input() selectedTime: Date = new Date();

  constructor() { }

  ngOnInit() {
  }
}
