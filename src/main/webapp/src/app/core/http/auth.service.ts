import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { catchError, tap } from 'rxjs/operators';

import { Authentication } from '../../shared/model/authentication.model';
import { User } from '../../shared/model/user/user.model';
import { TokenUtilsService } from '../util/token-utils.service';
import { RestService } from './rest.service';

const authenticatedUserKey = 'authenticatedUser';

@Injectable()
export class AuthService extends RestService<User> {

  constructor(http: HttpClient, toastr: ToastrService, private tokenUtils: TokenUtilsService) {
    super(http, ['/api/auth'], toastr);
  }

  authenticate(body: User): Observable<Authentication> {
    return this.http.post<Authentication>(this.url(), body).pipe(
      tap(res => {
        localStorage.setItem(authenticatedUserKey, JSON.stringify({
          user: res.user,
          roles: this.tokenUtils.getRoles(res.token),
          token: res.token
        }));
      }),
      catchError(this.handleError<Authentication>())
    );
  }

  signout(): void {
    localStorage.removeItem(authenticatedUserKey);
  }

  usernameTaken(username: string): Observable<boolean> {
    return this.http.get<boolean>(this.url(), { params: { 'username': username } }).pipe(
      catchError(this.handleError<boolean>())
    );
  }

  getCurrentUser() {
    return this.http.get<User>(this.url(['me'])).pipe(
      catchError(this.handleError<User>())
    );
  }

  getAuthenticatedUser() {
    return JSON.parse(localStorage.getItem(authenticatedUserKey));
  }

  getAuthenticatedUserId() {
    return this.getAuthenticatedUser().user.id;
  }

  getToken(): String {
    const authenticatedUser = this.getAuthenticatedUser();
    const token = authenticatedUser && authenticatedUser.token;
    return token ? token : '';
  }

  isAuthenticated(): boolean {
    return this.getToken() !== '';
  }

  isAdmin(): boolean {
    const authenticatedUser = this.getAuthenticatedUser();
    return authenticatedUser
      && authenticatedUser.roles
      && authenticatedUser.roles.indexOf('ADMIN') > -1;
  }

  isSupervisor(): boolean {
    const authenticatedUser = this.getAuthenticatedUser();
    return authenticatedUser
      && authenticatedUser.roles
      && authenticatedUser.roles.indexOf('SUPERVISOR') > -1;
  }

  isCompany(): boolean {
    const authenticatedUser = this.getAuthenticatedUser();
    return authenticatedUser
      && authenticatedUser.roles
      && authenticatedUser.roles.indexOf('COMPANY') > -1;
  }

  isTenant(): boolean {
    const authenticatedUser = this.getAuthenticatedUser();
    return authenticatedUser
      && authenticatedUser.roles
      && authenticatedUser.roles.indexOf('TENANT') > -1;
  }

  hasBuilding(): boolean {
    const authenticatedUser = this.getAuthenticatedUser();
    return authenticatedUser
      && authenticatedUser.user
      && authenticatedUser.user.apartment;
  }

  getBuildingId(): number {
    const authenticatedUser = this.getAuthenticatedUser();
    return authenticatedUser
      && authenticatedUser.user
      && authenticatedUser.user.apartment
      && authenticatedUser.user.apartment.building
      && authenticatedUser.user.apartment.building.id;
  }

  isSupervisorOfBuilding(buildingId: number): boolean {
    const authenticatedUser = this.getAuthenticatedUser();
    if (authenticatedUser
      && authenticatedUser.user
      && authenticatedUser.user.supervisingBuildings) {
      for (const building of authenticatedUser.user.supervisingBuildings) {
        if (building.id === +buildingId) {
          return true;
        }
      }
    }
    return false;
  }

  isTenantOfBuilding(buildingId: number): boolean {
    const authenticatedUser = this.getAuthenticatedUser();
    return authenticatedUser
      && authenticatedUser.user
      && authenticatedUser.user.apartment
      && authenticatedUser.user.apartment.building
      && authenticatedUser.user.apartment.building.id
      && authenticatedUser.user.apartment.building.id === +buildingId;
  }

  hasAccessToBuilding(buildingId: number): boolean {
    return this.isSupervisorOfBuilding(buildingId) || this.isTenantOfBuilding(buildingId);
  }

  isAssignedToBuilding(buildingId: number): boolean {
    const authenticatedUser = this.getAuthenticatedUser();
    return authenticatedUser && authenticatedUser.user &&
      authenticatedUser.user.buildings && authenticatedUser.user.buildings.find(building => building.id === +buildingId) != null;
  }

  hasAccessToBuildingMalfunctions(buildingId: number): boolean {
    return this.hasAccessToBuilding(buildingId) || this.isAssignedToBuilding(buildingId);
  }
}
