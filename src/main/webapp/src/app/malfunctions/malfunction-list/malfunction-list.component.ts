import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';

import { AuthService } from '../../core/http/auth.service';
import { CompanyService } from '../../core/http/company.service';
import { MalfunctionService } from '../../core/http/malfunction.service';
import { Malfunction } from '../../shared/model/malfunction.model';
import { Page } from '../../shared/model/page.model';
import { initialPageNumber, pageSize } from '../../shared/util/constants';

@Component({
  selector: 'bm-malfunction-list',
  templateUrl: './malfunction-list.component.html',
  styleUrls: ['./malfunction-list.component.css']
})
export class MalfunctionListComponent implements OnInit {

  page = new Page();

  rows: Malfunction[];

  buildingId: Number = null;

  constructor(private authService: AuthService,
    private companyService: CompanyService,
    private malfunctionService: MalfunctionService,
    private route: ActivatedRoute,
    private router: Router) {
    this.page.pageNumber = initialPageNumber;
    this.page.size = pageSize;
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.buildingId = params['buildingId'];
      this.setPage({ offset: 0 });
    });
  }

  setPage(pageInfo) {
    this.page.pageNumber = pageInfo.offset;
    if (this.authService.isCompany() && !this.buildingId) {
      const id = this.authService.getAuthenticatedUserId();
      this.companyService.findMalfunctions(this.page, id)
        .subscribe(pagedData => {
          this.page.setPagedData(pagedData);
          this.rows = pagedData.content;
        });
    } else {
      this.malfunctionService.findAllPagable(this.page, this.buildingId)
        .subscribe(pagedData => {
          this.page.setPagedData(pagedData);
          this.rows = pagedData.content;
        });
    }
  }

  reportMalfunction() {
    this.router.navigate(['buildings', this.buildingId, 'malfunctions', 'new']);
  }

  openDetails(malfunction: Malfunction): void {
    this.router.navigate(['buildings', malfunction.building.id, 'malfunctions', malfunction.id]);
  }

  hasAccess(): boolean {
    return this.buildingId && this.authService.hasAccessToBuilding(this.buildingId.valueOf());
  }
}
