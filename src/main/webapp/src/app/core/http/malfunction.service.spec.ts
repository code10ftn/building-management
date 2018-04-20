import { HttpClientModule } from '@angular/common/http';
import { inject, TestBed } from '@angular/core/testing';
import { ToastrModule } from 'ngx-toastr';

import { MalfunctionService } from './malfunction.service';

describe('MalfunctionService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [MalfunctionService]
    });
  });

  it('should be created', inject([MalfunctionService], (service: MalfunctionService) => {
    expect(service).toBeTruthy();
  }));
});
