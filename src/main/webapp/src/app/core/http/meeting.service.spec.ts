import { HttpClientModule } from '@angular/common/http';
import { inject, TestBed } from '@angular/core/testing';
import { ToastrModule } from 'ngx-toastr';

import { MeetingService } from './meeting.service';

describe('MeetingService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [MeetingService]
    });
  });

  it('should be created', inject([MeetingService], (service: MeetingService) => {
    expect(service).toBeTruthy();
  }));
});
