import { HttpClientModule } from '@angular/common/http';
import { inject, TestBed } from '@angular/core/testing';
import { ToastrModule } from 'ngx-toastr';

import { ResidentialRequestService } from './residential-request.service';

describe('ResidentialRequestService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [ResidentialRequestService]
    });
  });

  it('should be created', inject([ResidentialRequestService], (service: ResidentialRequestService) => {
    expect(service).toBeTruthy();
  }));
});
