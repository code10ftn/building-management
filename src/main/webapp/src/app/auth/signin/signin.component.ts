import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { AuthService } from '../../core/http/auth.service';
import { User } from '../../shared/model/user/user.model';

@Component({
  selector: 'bm-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SigninComponent implements OnInit {

  user: User = new User();

  constructor(private authService: AuthService,
    private router: Router,
    private toastr: ToastrService) { }

  ngOnInit() {
  }

  signin(): void {
    this.authService.authenticate(this.user).subscribe(
      response => {
        this.toastr.success(`Welcome ${this.user.username}`);
        const buildingId = this.authService.getBuildingId();
        if (buildingId) { // Tenant signed in.
          this.router.navigate(['buildings', buildingId, 'announcements']);
        } else { // Company signed in.
          this.router.navigateByUrl('buildings');
        }
      },
      err => { });
  }
}
