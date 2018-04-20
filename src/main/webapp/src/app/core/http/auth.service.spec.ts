import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';
import { ToastrModule } from 'ngx-toastr';

import { TokenUtilsService } from '../util/token-utils.service';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  // tslint:disable-next-line:max-line-length
  this.token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1cHJhdm5payIsImF1ZGllbmNlIjoid2ViIiwiY3JlYXRlZCI6MTUxNTUwOTgwNzI1NiwiYXV0aG9yaXRpZXMiOiJURU5BTlQsU1VQRVJWSVNPUiIsImV4cCI6MTUxNjExNDYwN30.Dcp9gceujvp7Wkih_t590m-t6X0FDWynk79rmlm5PbA';

  this.authenticatedUserKey = 'authenticatedUser';

  this.authenticatedUser = {
    username: 'admin',
    roles: 'ADMIN',
    token: this.token
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [AuthService, TokenUtilsService]
    });

    this.authService = TestBed.get(AuthService);
    this.httpMock = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    this.httpMock.verify();
  });

  it('should be created', inject([AuthService], (service: AuthService) => {
    expect(service).toBeTruthy();
  }));

  it('should return true when username is taken', () => {
    this.authService.usernameTaken('luka').subscribe();
    const request = this.httpMock.expectOne(`${this.authService.url()}?username=luka`);
    expect(request.request.method).toBe('GET');
  });

  it('should remove authenticated user from local storage', () => {
    localStorage.setItem(this.authenticatedUserKey, JSON.stringify(this.authenticatedUser));
    this.authService.signout();
    expect(localStorage.getItem(this.authenticatedUserKey)).toEqual(null);
  });

  it('should return authenticated user from local storage', () => {
    localStorage.setItem(this.authenticatedUserKey, JSON.stringify(this.authenticatedUser));
    const authenticatedUser = this.authService.getAuthenticatedUser();
    expect(authenticatedUser).toEqual(this.authenticatedUser);
  });

  it('should return authenticated user token', () => {
    localStorage.setItem(this.authenticatedUserKey, JSON.stringify(this.authenticatedUser));
    const token = this.authService.getToken();
    expect(token).toEqual(this.authenticatedUser.token);
  });

  it('should return true when user is authenticated', () => {
    localStorage.setItem(this.authenticatedUserKey, JSON.stringify(this.authenticatedUser));
    const isAuthenticated = this.authService.isAuthenticated();
    expect(isAuthenticated).toBeTruthy();
  });

  it('should return false when user is not authenticated', () => {
    localStorage.removeItem(this.authenticatedUserKey);
    const isAuthenticated = this.authService.isAuthenticated();
    expect(isAuthenticated).toBeFalsy();
  });

  it('should return token when username and password are valid', () => {
    const user = {
      username: 'luka',
      password: 'luka'
    };
    this.authService.authenticate(user).subscribe(response => {
      expect(response.token).toEqual(this.token);
    });

    const request = this.httpMock.expectOne(`${this.authService.url()}`);
    expect(request.request.method).toBe('POST');
    request.flush({
      token: this.token
    });
  });
});
