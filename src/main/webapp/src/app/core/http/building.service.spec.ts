import { HttpClientModule } from '@angular/common/http';
import { inject, TestBed } from '@angular/core/testing';
import { ToastrModule } from 'ngx-toastr';

import { BuildingService } from './building.service';

describe('BuildingService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [BuildingService]
    });
  });

  it('should be created', inject([BuildingService], (service: BuildingService) => {
    expect(service).toBeTruthy();
  }));
});
