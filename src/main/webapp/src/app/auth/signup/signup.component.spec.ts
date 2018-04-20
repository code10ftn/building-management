import { HttpClientModule } from '@angular/common/http';
import { DebugElement } from '@angular/core/src/debug/debug_node';
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { ToastrModule } from 'ngx-toastr';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';

import { AuthService } from '../../core/http/auth.service';
import { CompanyService } from '../../core/http/company.service';
import { TenantService } from '../../core/http/tenant.service';
import { WorkArea } from '../../shared/model/work-area.model';
import { SharedModule } from '../../shared/shared.module';
import { SignupComponent } from './signup.component';

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;
  let debugElement: DebugElement;

  beforeEach(async(() => {
    const authServiceMock = {
      usernameTaken: jasmine.createSpy('usernameTaken').and.returnValue(of(false))
    };

    const routerMock = {
      navigateByUrl: jasmine.createSpy('navigateByUrl')
    };

    TestBed.configureTestingModule({
      declarations: [SignupComponent],
      imports: [FormsModule, HttpClientModule, SharedModule, ToastrModule.forRoot({ preventDuplicates: true })],
      providers: [CompanyService, TenantService, { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement;
    fixture.detectChanges();

    this.authService = TestBed.get(AuthService);
    this.companyService = TestBed.get(CompanyService);
    this.tenantService = TestBed.get(TenantService);
    this.router = TestBed.get(Router);

    this.roleSelectEl = debugElement.query(By.css('select[name="role"]')).nativeElement;
    this.phoneNumberEl = debugElement.query(By.css('input[name="phoneNumber"]')).nativeElement;
    this.usernameEl = debugElement.query(By.css('input[name="username"]')).nativeElement;
    this.emailEl = debugElement.query(By.css('input[name="email"]')).nativeElement;
    this.passwordEl = debugElement.query(By.css('input[name="password"]')).nativeElement;
    this.submitButtonEl = debugElement.query(By.css('button')).nativeElement;
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set tenant as role on init', () => {
    component.ngOnInit();
    expect(component.role).toBe('TENANT');
  });

  it('should call authService.usernameTaken when checkUsernameTaken is called', fakeAsync(() => {
    component.checkUsernameTaken('luka');
    tick(300);
    expect(this.authService.usernameTaken).toHaveBeenCalledTimes(1);
  }));

  it('should correctly bind from object to form when role is tenant', fakeAsync(() => {
    component.role = 'TENANT';
    fixture.detectChanges();
    tick();
    expect(this.roleSelectEl.value).toBe('TENANT');

    component.user.username = 'test';
    component.user.password = 'test';
    component.user.phoneNumber = '123456';
    component.user.email = 'test@test';
    component.tenant.firstName = 'Test';
    component.tenant.lastName = 'Test';

    fixture.detectChanges();
    tick();
    this.firstNameEl = debugElement.query(By.css('input[name="firstName"]')).nativeElement;
    this.lastNameEl = debugElement.query(By.css('input[name="lastName"]')).nativeElement;

    expect(this.usernameEl.value).toBe('test');
    expect(this.passwordEl.value).toBe('test');
    expect(this.phoneNumberEl.value).toBe('123456');
    expect(this.emailEl.value).toBe('test@test');
    expect(this.firstNameEl.value).toBe('Test');
    expect(this.lastNameEl.value).toBe('Test');
  }));

  it('should correctly bind from object to form when role is company', fakeAsync(() => {
    component.role = 'COMPANY';
    fixture.detectChanges();
    tick();
    expect(this.roleSelectEl.value).toBe('COMPANY');

    component.user.username = 'test';
    component.user.password = 'test';
    component.user.phoneNumber = '123456';
    component.user.email = 'test@test';
    component.company.name = 'Test company';
    component.company.address = 'Test address';
    component.company.description = 'Test description';
    component.company.workArea = WorkArea.HEATING;

    this.companyNameEl = debugElement.query(By.css('input[name="companyName"]')).nativeElement;
    this.companyAddressEl = debugElement.query(By.css('input[name="address"]')).nativeElement;
    this.companyDescriptionEl = debugElement.query(By.css('input[name="description"]')).nativeElement;
    this.workAreaSelectEl = debugElement.query(By.css('select[name="workArea"]')).nativeElement;

    fixture.detectChanges();
    tick();

    expect(this.usernameEl.value).toBe('test');
    expect(this.passwordEl.value).toBe('test');
    expect(this.phoneNumberEl.value).toBe('123456');
    expect(this.emailEl.value).toBe('test@test');
    expect(this.companyNameEl.value).toBe('Test company');
    expect(this.companyAddressEl.value).toBe('Test address');
    expect(this.companyDescriptionEl.value).toBe('Test description');
    expect(this.workAreaSelectEl.value).toBe(`${WorkArea.HEATING}`);
  }));

  it('should correctly bind from form to object when role is tenant', fakeAsync(() => {
    this.roleSelectEl.value = 'TENANT';
    this.roleSelectEl.dispatchEvent(newEvent('change'));
    expect(component.role).toBe('TENANT');

    this.firstNameEl = debugElement.query(By.css('input[name="firstName"]')).nativeElement;
    this.lastNameEl = debugElement.query(By.css('input[name="lastName"]')).nativeElement;

    this.firstNameEl.value = 'Test';
    this.lastNameEl.value = 'Test';
    this.usernameEl.value = 'test';
    this.passwordEl.value = 'test';
    this.emailEl.value = 'test@test';
    this.phoneNumberEl.value = '123456';

    expect(component.tenant.firstName).not.toBe('Test');
    expect(component.tenant.lastName).not.toBe('Test');
    expect(component.user.username).not.toBe('test');
    expect(component.user.password).not.toBe('test');
    expect(component.user.email).not.toBe('test@test');
    expect(component.user.phoneNumber).not.toBe('123456');

    this.firstNameEl.dispatchEvent(newEvent('input'));
    this.lastNameEl.dispatchEvent(newEvent('input'));
    this.usernameEl.dispatchEvent(newEvent('input'));
    this.passwordEl.dispatchEvent(newEvent('input'));
    this.emailEl.dispatchEvent(newEvent('input'));
    this.phoneNumberEl.dispatchEvent(newEvent('input'));

    expect(component.tenant.firstName).toBe('Test');
    expect(component.tenant.lastName).toBe('Test');
    expect(component.user.username).toBe('test');
    expect(component.user.password).toBe('test');
    expect(component.user.email).toBe('test@test');
    expect(component.user.phoneNumber).toBe('123456');
  }));

  it('should correctly bind from form to object when role is company', fakeAsync(() => {
    this.roleSelectEl.value = 'COMPANY';
    this.roleSelectEl.dispatchEvent(newEvent('change'));
    expect(component.role).toBe('COMPANY');

    fixture.detectChanges();
    tick();

    this.companyNameEl = debugElement.query(By.css('input[name="companyName"]')).nativeElement;
    this.companyAddressEl = debugElement.query(By.css('input[name="address"]')).nativeElement;
    this.companyDescriptionEl = debugElement.query(By.css('input[name="description"]')).nativeElement;
    this.workAreaSelectEl = debugElement.query(By.css('select[name="workArea"]')).nativeElement;

    this.companyNameEl.value = 'Test company';
    this.companyAddressEl.value = 'Test address';
    this.companyDescriptionEl.value = 'Test description';
    this.workAreaSelectEl.value = WorkArea.HEATING;
    this.usernameEl.value = 'test';
    this.passwordEl.value = 'test';
    this.emailEl.value = 'test@test';
    this.phoneNumberEl.value = '123456';

    expect(component.company.name).not.toBe('Test company');
    expect(component.company.address).not.toBe('Test address');
    expect(component.company.address).not.toBe('Test description');
    expect(component.company.workArea).not.toBe(WorkArea.HEATING);
    expect(component.user.username).not.toBe('test');
    expect(component.user.password).not.toBe('test');
    expect(component.user.email).not.toBe('test@test');
    expect(component.user.phoneNumber).not.toBe('123456');

    this.companyNameEl.dispatchEvent(newEvent('input'));
    this.companyAddressEl.dispatchEvent(newEvent('input'));
    this.companyDescriptionEl.dispatchEvent(newEvent('input'));
    this.workAreaSelectEl.dispatchEvent(newEvent('change'));
    this.usernameEl.dispatchEvent(newEvent('input'));
    this.passwordEl.dispatchEvent(newEvent('input'));
    this.emailEl.dispatchEvent(newEvent('input'));
    this.phoneNumberEl.dispatchEvent(newEvent('input'));

    expect(component.company.name).toBe('Test company');
    expect(component.company.address).toBe('Test address');
    expect(component.company.description).toBe('Test description');
    expect(component.company.workArea.toString()).toBe(`${WorkArea.HEATING}`);
    expect(component.user.username).toBe('test');
    expect(component.user.password).toBe('test');
    expect(component.user.email).toBe('test@test');
    expect(component.user.phoneNumber).toBe('123456');
  }));

  it('should disable sign up button when email is invalid or form is incomplete', fakeAsync(() => {
    this.roleSelectEl.value = 'TENANT';
    this.roleSelectEl.dispatchEvent(newEvent('change'));
    expect(component.role).toBe('TENANT');

    this.firstNameEl = debugElement.query(By.css('input[name="firstName"]')).nativeElement;
    this.lastNameEl = debugElement.query(By.css('input[name="lastName"]')).nativeElement;

    this.firstNameEl.value = 'Test';
    this.lastNameEl.value = 'Test';
    this.usernameEl.value = 'test';
    this.passwordEl.value = 'test';
    this.emailEl.value = 'test';

    this.firstNameEl.dispatchEvent(newEvent('input'));
    this.lastNameEl.dispatchEvent(newEvent('input'));
    this.usernameEl.dispatchEvent(newEvent('input'));
    this.passwordEl.dispatchEvent(newEvent('input'));
    this.emailEl.dispatchEvent(newEvent('input'));
    fixture.detectChanges();
    expect(this.phoneNumberEl.value).toBe('');
    expect(this.submitButtonEl.disabled).toBeTruthy();

    this.phoneNumberEl.value = '123456';
    this.phoneNumberEl.dispatchEvent(newEvent('input'));
    fixture.detectChanges();
    expect(this.phoneNumberEl.value).toBe('123456');
    expect(this.submitButtonEl.disabled).toBeTruthy();

    this.emailEl.value = 'test@test';
    this.emailEl.dispatchEvent(newEvent('input'));
    fixture.detectChanges();
    expect(this.submitButtonEl.disabled).toBeFalsy();
  }));

  it('should signup tenant when all fields are valid', fakeAsync(() => {
    spyOn(this.tenantService, 'create').and.returnValue(of(110));

    component.role = 'TENANT';
    component.tenant.firstName = 'Test tenant';
    component.tenant.lastName = 'Test tenant';
    component.user.phoneNumber = '123456';
    component.user.username = 'tenant username';
    component.user.email = 'tenant@email';
    component.user.password = 'test';

    component.signup();
    tick();
    expect(this.tenantService.create).toHaveBeenCalled();
    expect(this.router.navigateByUrl).toHaveBeenCalledWith('signin');
  }));

  it('should throw error during tenant signup when email is invalid', fakeAsync(() => {
    spyOn(this.tenantService, 'create').and.returnValue(Observable.throw('Email invalid!'));

    component.role = 'TENANT';
    component.tenant.firstName = 'Test tenant';
    component.tenant.lastName = 'Test tenant';
    component.user.phoneNumber = '123456';
    component.user.username = 'tenant username';
    component.user.email = 'invalidemail';
    component.user.password = 'test';

    component.signup();
    tick();
    expect(this.tenantService.create).toHaveBeenCalled();

    // TODO: check if error was thrown
  }));

  it('should signup company when all fields are valid', fakeAsync(() => {
    spyOn(this.companyService, 'create').and.returnValue(of(111));

    component.role = 'COMPANY';
    component.company.name = 'Test company';
    component.company.address = 'Test address';
    component.company.description = 'Test description';
    component.company.workArea = WorkArea.HEATING;
    component.user.phoneNumber = '123456';
    component.user.username = 'company username';
    component.user.email = 'company@email';
    component.user.password = 'test';

    component.signup();
    tick();
    expect(this.companyService.create).toHaveBeenCalled();
    expect(this.router.navigateByUrl).toHaveBeenCalledWith('signin');
  }));

  it('should throw error during company signup when email is invalid', fakeAsync(() => {
    spyOn(this.companyService, 'create').and.returnValue(Observable.throw('Email invalid!'));

    component.role = 'COMPANY';
    component.company.name = 'Test company';
    component.company.address = 'Test address';
    component.company.description = 'Test description';
    component.company.workArea = WorkArea.HEATING;
    component.user.phoneNumber = '123456';
    component.user.username = 'tenant username';
    component.user.email = 'invalidemail';
    component.user.password = 'test';

    component.signup();
    tick();
    expect(this.companyService.create).toHaveBeenCalled();
    // TODO: check if error was thrown
  }));

  // a helper function to tell Angular that an event on the HTML page has happened
  function newEvent(eventName: string, bubbles = false, cancelable = false) {
    const event = document.createEvent('CustomEvent');  // MUST be 'CustomEvent'
    event.initCustomEvent(eventName, bubbles, cancelable, null);
    return event;
  }
});
