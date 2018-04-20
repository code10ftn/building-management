import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ToastrModule } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';

import { MalfunctionService } from '../../core/http/malfunction.service';
import { SharedModule } from '../../shared/shared.module';
import { NewMalfunctionComponent } from './new-malfunction.component';

describe('NewMalfunctionComponent', () => {
  let component: NewMalfunctionComponent;
  let fixture: ComponentFixture<NewMalfunctionComponent>;

  beforeEach(async(() => {
    const activatedRouteMock = {
      params: {
        subscribe: jasmine.createSpy('subscribe').and.returnValue(Observable.of(<Params>{ id: 1 }))
      }
    };

    const routerMock = {
      navigateByUrl: jasmine.createSpy('navigateByUrl')
    };

    TestBed.configureTestingModule({
      declarations: [NewMalfunctionComponent],
      imports: [FormsModule, HttpClientModule, SharedModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [MalfunctionService, { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: Router, useValue: routerMock }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewMalfunctionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
