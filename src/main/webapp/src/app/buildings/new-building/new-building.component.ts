import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs/Subject';

import { AuthService } from '../../core/http/auth.service';
import { BuildingService } from '../../core/http/building.service';
import { BuildingCreateDto } from '../dto/building.create.dto';

@Component({
  selector: 'bm-new-building',
  templateUrl: './new-building.component.html',
  styleUrls: ['./new-building.component.css']
})
export class NewBuildingComponent implements OnInit {

  building: BuildingCreateDto;

  addressTaken: Observable<boolean>;

  addressTerms = new Subject<string>();

  constructor(private authService: AuthService,
    private buildingService: BuildingService,
    private router: Router,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.building = new BuildingCreateDto();

    this.addressTaken = this.addressTerms.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((term: string) => this.buildingService.addressTaken(term))
    ).share();
  }

  checkAddressTaken(term: string): void {
    this.addressTerms.next(term);
  }

  create() {
    this.buildingService.create<BuildingCreateDto>(this.building).subscribe(
      id => {
        this.toastr.success('Successfully created new building!');
        this.router.navigateByUrl('buildings');
      },
      err => { });
  }
}
