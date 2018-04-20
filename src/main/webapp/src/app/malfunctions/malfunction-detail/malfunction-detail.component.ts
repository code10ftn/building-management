import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';
import { BuildingService } from '../../core/http/building.service';
import { MalfunctionService } from '../../core/http/malfunction.service';
import { Malfunction } from '../../shared/model/malfunction.model';
import { User } from '../../shared/model/user/user.model';
import { WorkArea } from '../../shared/model/work-area.model';
import { AssigneeForwardDto } from '../dto/assignee-forward.model';
import { CommentCreateDto } from '../dto/comment-create.model';

@Component({
  selector: 'bm-malfunction-detail',
  templateUrl: './malfunction-detail.component.html',
  styleUrls: ['./malfunction-detail.component.css']
})
export class MalfunctionDetailComponent implements OnInit {

  malfunction = new Malfunction();

  workAreas = WorkArea;

  buildingId: number;

  malfunctionId: number;

  canEdit = false;

  isAssignee = false;

  comment = new CommentCreateDto();

  assignees = new Array<User>();

  newAssignee = new AssigneeForwardDto();

  constructor(private authService: AuthService,
    private buildingService: BuildingService,
    private malfunctionService: MalfunctionService,
    private route: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService, ) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.buildingId = params['buildingId'];
      this.malfunctionId = params['malfunctionId'];
      this.getMalfunction();
      this.initAssignees();
    });
  }

  initAssignees() {
    this.buildingService.findById(this.buildingId).subscribe(
      res => {
        this.assignees = res.companies;
        this.assignees.push(res.supervisor);
      },
      err => { }
    );
  }

  forward() {
    this.malfunctionService.forward(this.newAssignee, this.buildingId, this.malfunctionId).subscribe(
      res => {
        this.malfunction = res;
        this.toastr.success('Malfunction forwarded succesfully');
      },
      err => { }
    );
  }

  getMalfunction(): void {
    this.malfunctionService.findById(this.buildingId, this.malfunctionId).subscribe(res => {
      this.malfunction = res;
      this.malfunction.workArea = WorkArea[res.workArea.toString()];

      this.canEdit = (this.authService.isSupervisor() ||
        this.malfunction.creator.username === this.authService.getAuthenticatedUser().user.username) &&
        !this.malfunction.repairment;

      this.isAssignee = this.malfunction.assignee.id === this.authService.getAuthenticatedUserId();
    }, err => {
      this.router.navigate(['buildings', this.buildingId, 'malfunctions']);
    });
  }

  createRepairment() {
    this.router.navigate(['buildings', this.buildingId, 'malfunctions', this.malfunctionId, 'repair']);
  }

  update() {
    this.malfunctionService.update(this.malfunction, this.buildingId, this.malfunctionId).subscribe(
      res => {
        this.malfunction = res;
        this.malfunction.workArea = WorkArea[res.workArea.toString()];
        this.toastr.success('Malfunction updated succesfully');
      },
      err => { }
    );
  }

  canForward() {
    return this.malfunction.assignee && this.malfunction.assignee.id === this.authService.getAuthenticatedUserId();
  }
}
