import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../http/auth.service';

@Component({
  selector: 'bm-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css']
})
export class NavComponent {

  isCollapsed = true;

  constructor(private authService: AuthService,
    private router: Router) { }

  signout() {
    this.authService.signout();
    this.router.navigateByUrl('signin');
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  hasBuilding(): boolean {
    return this.authService.hasBuilding();
  }

  getBuildingId(): number {
    return this.authService.getBuildingId();
  }

  hasResidentialRequests(): boolean {
    return this.authService.isAdmin() || this.authService.isSupervisor();
  }

  isCompany(): boolean {
    return this.authService.isCompany();
  }

  openSurveys(): void {
    this.router.navigate(['buildings', this.authService.getBuildingId(), 'surveys']);
  }
}
