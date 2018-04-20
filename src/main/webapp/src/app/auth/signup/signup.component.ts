import 'rxjs/add/operator/share';

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs/Subject';

import { AuthService } from '../../core/http/auth.service';
import { CompanyService } from '../../core/http/company.service';
import { TenantService } from '../../core/http/tenant.service';
import { Company } from '../../shared/model/user/company.model';
import { Tenant } from '../../shared/model/user/tenant.model';
import { User } from '../../shared/model/user/user.model';
import { WorkArea } from '../../shared/model/work-area.model';

@Component({
  selector: 'bm-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  role: String;

  company: Company = new Company();

  tenant: Tenant = new Tenant();

  user: User = new User();

  workAreas = WorkArea;

  usernameTaken: Observable<boolean>;

  usernameTerms = new Subject<string>();

  constructor(
    private authService: AuthService,
    private companyService: CompanyService,
    private router: Router,
    private tenantService: TenantService) { }

  ngOnInit() {
    this.role = 'TENANT';

    this.usernameTaken = this.usernameTerms.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((term: string) => this.authService.usernameTaken(term))
    ).share();
  }

  checkUsernameTaken(term: string): void {
    this.usernameTerms.next(term);
  }

  signup(): void {
    if (this.role === 'TENANT') {
      this.tenant.username = this.user.username;
      this.tenant.password = this.user.password;
      this.tenant.email = this.user.email;
      this.tenant.phoneNumber = this.user.phoneNumber;

      this.tenantService.create(this.tenant).subscribe(
        id => {
          this.router.navigateByUrl('signin');
        },
        err => { });
    } else {
      this.company.username = this.user.username;
      this.company.password = this.user.password;
      this.company.email = this.user.email;
      this.company.phoneNumber = this.user.phoneNumber;

      this.companyService.create(this.company).subscribe(
        id => {
          this.router.navigateByUrl('signin');
        },
        err => { });
    }
  }
}
