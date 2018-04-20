import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { MalfunctionService } from '../../core/http/malfunction.service';
import { WorkArea } from '../../shared/model/work-area.model';
import { MalfunctionCreateDto } from '../dto/malfunction-create.model';

@Component({
  selector: 'bm-new-malfunction',
  templateUrl: './new-malfunction.component.html',
  styleUrls: ['./new-malfunction.component.css']
})
export class NewMalfunctionComponent implements OnInit {

  malfunction = new MalfunctionCreateDto();

  workAreas = WorkArea;

  buildingId: number;

  constructor(private malfunctionService: MalfunctionService,
    private router: Router,
    private route: ActivatedRoute,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.buildingId = params['buildingId'];
    });
  }

  report() {
    this.malfunctionService.create(this.malfunction, this.buildingId).subscribe(
      id => {
        this.toastr.success('Malfunction reported succesfully');
        this.router.navigate(['buildings', this.buildingId, 'malfunctions', id]);
      },
      err => { }
    );
  }
}
