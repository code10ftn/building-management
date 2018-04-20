import { HttpClientModule } from '@angular/common/http';
import { DebugElement } from '@angular/core/src/debug/debug_node';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ToastrModule } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';

import { AuthService } from '../../core/http/auth.service';
import { TokenUtilsService } from '../../core/util/token-utils.service';
import { SigninComponent } from './signin.component';

describe('SigninComponent', () => {
  let component: SigninComponent;
  let fixture: ComponentFixture<SigninComponent>;
  let debugElement: DebugElement;

  beforeEach(async(() => {
    const routerMock = {
      navigateByUrl: jasmine.createSpy('navigateByUrl')
    };

    TestBed.configureTestingModule({
      declarations: [SigninComponent],
      imports: [BrowserAnimationsModule, FormsModule, HttpClientModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [AuthService, TokenUtilsService, { provide: Router, useValue: routerMock }]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SigninComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement;
    fixture.detectChanges();

    this.authService = TestBed.get(AuthService);
    this.router = TestBed.get(Router);

    this.usernameEl = debugElement.query(By.css('input[name="username"]')).nativeElement;
    this.passwordEl = debugElement.query(By.css('input[name="password"]')).nativeElement;
    this.submitButtonEl = debugElement.query(By.css('button')).nativeElement;
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should correctly bind from object to form', fakeAsync(() => {
    component.user.username = 'test';
    component.user.password = 'test';

    fixture.detectChanges();
    tick();

    expect(this.usernameEl.value).toBe('test');
    expect(this.passwordEl.value).toBe('test');
  }));

  it('should correctly bind from form to object', fakeAsync(() => {
    this.usernameEl.value = 'test';
    this.passwordEl.value = 'test';

    expect(component.user.username).not.toBe('test');
    expect(component.user.password).not.toBe('test');

    this.usernameEl.dispatchEvent(newEvent('input'));
    this.passwordEl.dispatchEvent(newEvent('input'));

    expect(component.user.username).toBe('test');
    expect(component.user.password).toBe('test');
  }));

  it('should disable submit button when form is incomplete', () => {
    this.usernameEl.value = 'test';

    this.usernameEl.dispatchEvent(newEvent('input'));
    fixture.detectChanges();

    expect(this.usernameEl.value).toBe('test');
    expect(this.passwordEl.value).toBe('');
    expect(this.submitButtonEl.disabled).toBeTruthy();

    this.passwordEl.value = 'test';

    this.passwordEl.dispatchEvent(newEvent('input'));
    fixture.detectChanges();

    expect(this.passwordEl.value).toBe('test');
    expect(this.submitButtonEl.disabled).toBeFalsy();
  });

  it('should sign in user when all fields are valid', () => {
    spyOn(this.authService, 'authenticate').and.returnValue(of({ token: 'random.token.test' }));

    component.user.username = 'test';
    component.user.password = 'test';

    component.signin();
    expect(this.authService.authenticate).toHaveBeenCalled();
  });

  it('should throw error during signin when data is invalid', fakeAsync(() => {
    spyOn(this.authService, 'authenticate').and.returnValue(Observable.throw('Email and password do not match!'));

    component.user.username = 'test';
    component.user.password = 'password';

    component.signin();
    tick();
    expect(this.authService.authenticate).toHaveBeenCalled();

    // TODO: check if error was thrown
  }));

  function newEvent(eventName: string, bubbles = false, cancelable = false) {
    const event = document.createEvent('CustomEvent');  // MUST be 'CustomEvent'
    event.initCustomEvent(eventName, bubbles, cancelable, null);
    return event;
  }
});
