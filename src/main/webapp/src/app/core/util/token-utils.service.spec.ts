import { inject, TestBed } from '@angular/core/testing';

import { TokenUtilsService } from './token-utils.service';

describe('TokenUtilsService', () => {
  // tslint:disable-next-line:max-line-length
  this.token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1cHJhdm5payIsImF1ZGllbmNlIjoid2ViIiwiY3JlYXRlZCI6MTUxNTUwOTgwNzI1NiwiYXV0aG9yaXRpZXMiOiJURU5BTlQsU1VQRVJWSVNPUiIsImV4cCI6MTUxNjExNDYwN30.Dcp9gceujvp7Wkih_t590m-t6X0FDWynk79rmlm5PbA';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TokenUtilsService]
    });

    this.tokenUtilsService = TestBed.get(TokenUtilsService);
  });

  it('should be created', inject([TokenUtilsService], (service: TokenUtilsService) => {
    expect(service).toBeTruthy();
  }));

  it('should get current user role from token', () => {
    const roles = this.tokenUtilsService.getRoles(this.token);
    expect(roles).toContain('TENANT');
    expect(roles).toContain('SUPERVISOR');
  });
});
