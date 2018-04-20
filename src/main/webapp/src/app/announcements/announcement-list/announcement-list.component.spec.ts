import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Params } from '@angular/router';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ToastrModule } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';

import { AnnouncementService } from '../../core/http/announcement.service';
import { AuthService } from '../../core/http/auth.service';
import { TokenUtilsService } from '../../core/util/token-utils.service';
import { AnnouncementListComponent } from './announcement-list.component';

describe('AnnouncementListComponent', () => {
  let component: AnnouncementListComponent;
  let fixture: ComponentFixture<AnnouncementListComponent>;

  beforeEach(async(() => {
    const activatedRouteMock = {
      params: {
        subscribe: jasmine.createSpy('subscribe').and.returnValue(Observable.of(<Params>{ id: 1 }))
      }
    };

    TestBed.configureTestingModule({
      declarations: [AnnouncementListComponent],
      imports: [BrowserAnimationsModule, BsDropdownModule.forRoot(), HttpClientModule, NgxDatatableModule,
        ToastrModule.forRoot({ preventDuplicates: true }), FormsModule],
      providers: [AnnouncementService, AuthService, BsModalService, TokenUtilsService,
        { provide: ActivatedRoute, useValue: activatedRouteMock }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AnnouncementListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
