import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { ToastrModule } from 'ngx-toastr';

import { SharedModule } from '../../shared/shared.module';
import { AuthService } from '../http/auth.service';
import { TokenUtilsService } from '../util/token-utils.service';
import { NavComponent } from './nav.component';

describe('NavComponent', () => {
  let component: NavComponent;
  let fixture: ComponentFixture<NavComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NavComponent],
      imports: [BrowserAnimationsModule, HttpClientModule, RouterTestingModule, SharedModule,
        ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [AuthService, TokenUtilsService]
    })
      .compileComponents();

    this.authService = TestBed.get(AuthService);
    this.router = TestBed.get(Router);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should signout and navigate to signup', fakeAsync(() => {
    spyOn(this.authService, 'signout');
    spyOn(this.router, 'navigateByUrl');

    component.signout();
    tick();

    expect(this.authService.signout).toHaveBeenCalled();
    expect(this.router.navigateByUrl).toHaveBeenCalledWith('signin');
  }));
});
