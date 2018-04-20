import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';

import { BuildingService } from '../../core/http/building.service';
import { Tenant } from '../../shared/model/user/tenant.model';

@Component({
  selector: 'bm-building-tenant-list',
  templateUrl: './building-tenant-list.component.html',
  styleUrls: ['./building-tenant-list.component.css']
})
export class BuildingTenantListComponent implements OnInit {

  rows: Tenant[];

  constructor(private route: ActivatedRoute,
    private buildingService: BuildingService,
    private router: Router) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.getBuilding(params['buildingId']);
    });
  }

  getBuilding(buildingId: number) {
    this.buildingService.findById(buildingId).subscribe(
      res => {
        this.rows = res.tenants;
      },
      err => {
        this.router.navigateByUrl('buildings');
      });
  }
}
