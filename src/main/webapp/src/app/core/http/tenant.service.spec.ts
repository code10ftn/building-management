import { inject, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ToastrModule, ToastrService } from 'ngx-toastr';

import { TenantService } from './tenant.service';

describe('TenantService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [TenantService]
    });

    this.tenantService = TestBed.get(TenantService);
    this.httpMock = TestBed.get(HttpTestingController);
  });

  it('should be created', inject([TenantService], (service: TenantService) => {
    expect(service).toBeTruthy();
  }));

  it('should create tenant when body is valid', () => {
    const tenant = {
      firstName: 'Test first name',
      lastName: 'Test last name',
      phoneNumber: '123456',
      username: 'test',
      email: 'test@test',
      password: 'test'
    };
    this.tenantService.create(tenant).subscribe((id) => {
      expect(id).toBe(100);
    });

    const request = this.httpMock.expectOne(`${this.tenantService.url()}`);
    expect(request.request.method).toBe('POST');
    request.flush(100);
  });
});
