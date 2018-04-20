import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as moment from 'moment';
import { ToastrService } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';
import { MalfunctionService } from '../../core/http/malfunction.service';
import { DatetimePickerComponent } from '../../shared/datetime-picker/datetime-picker.component';
import { MalfunctionRepairDto } from '../dto/malfunction-repair.model';

@Component({
  selector: 'bm-new-repairment',
  templateUrl: './new-repairment.component.html',
  styleUrls: ['./new-repairment.component.css']
})
export class NewRepairmentComponent implements OnInit {

  @ViewChild(DatetimePickerComponent)
  datetimePicker: DatetimePickerComponent;

  initDate: Date = new Date();

  repairment = new MalfunctionRepairDto();

  buildingId: number;

  malfunctionId: number;

  constructor(
    private authService: AuthService,
    private malfunctionService: MalfunctionService,
    private router: Router,
    private route: ActivatedRoute,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.buildingId = params['buildingId'];
      this.malfunctionId = params['malfunctionId'];

      this.checkAssignee();
    });
  }

  checkAssignee() {
    this.malfunctionService.findById(this.buildingId, this.malfunctionId).subscribe(
      res => {
        if (res.assignee.id !== this.authService.getAuthenticatedUserId()) {
          this.router.navigate(['buildings', this.buildingId, 'malfunctions', this.malfunctionId]);
        }
      },
      err => { }
    );
  }

  create() {
    this.repairment.repairDate = this.datetimePicker.selectedDate;
    this.repairment.repairDate.setHours(this.datetimePicker.selectedTime.getHours());
    this.repairment.repairDate.setMinutes(this.datetimePicker.selectedTime.getMinutes());

    this.malfunctionService.repair(this.repairment, this.buildingId, this.malfunctionId).subscribe(
      updatedMalfunction => {
        this.toastr.success('Repairment submited succesfully');
        this.router.navigate(['buildings', this.buildingId, 'malfunctions', this.malfunctionId]);
      },
      err => { });
  }

  datePassed(): boolean {
    const date = this.datetimePicker.selectedDate;
    date.setHours(this.datetimePicker.selectedTime.getHours());
    date.setMinutes(this.datetimePicker.selectedTime.getMinutes());
    date.setSeconds(0);
    return moment(date).diff(moment()) < 0;
  }
}
