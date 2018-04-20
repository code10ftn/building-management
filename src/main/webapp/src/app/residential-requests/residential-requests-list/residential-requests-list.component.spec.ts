import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';

import { ResidentialRequestService } from '../../core/http/residential-request.service';
import { ResidentialRequestsListComponent } from './residential-requests-list.component';

describe('ResidentialRequestsListComponent', () => {
  let component: ResidentialRequestsListComponent;
  let fixture: ComponentFixture<ResidentialRequestsListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ResidentialRequestsListComponent],
      imports: [BrowserAnimationsModule, HttpClientModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [ResidentialRequestService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResidentialRequestsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
