import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';
import { ToastrModule } from 'ngx-toastr';

import { WorkArea } from '../../shared/model/work-area.model';
import { CompanyService } from './company.service';

describe('CompanyService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [CompanyService]
    });

    this.companyService = TestBed.get(CompanyService);
    this.httpMock = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    this.httpMock.verify();
  });

  it('should be created', inject([CompanyService], (service: CompanyService) => {
    expect(service).toBeTruthy();
  }));

  it('should create company when body is valid', () => {
    const company = {
      name: 'Test company',
      address: 'Test address',
      describe: 'Test description',
      workArea: WorkArea.DOORS,
      phoneNumber: '123456',
      username: 'test',
      email: 'test@test',
      password: 'test'
    };
    this.companyService.create(company).subscribe((id) => {
      expect(id).toBe(90);
    });

    const request = this.httpMock.expectOne(`${this.companyService.url()}`);
    expect(request.request.method).toBe('POST');
    request.flush(90);
  });
});
