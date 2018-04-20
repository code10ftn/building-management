import { browser, by, element } from 'protractor';

import { SignupPage } from './signup.po';

describe('signup', () => {
    let signupPage: SignupPage;

    beforeEach(() => {
        signupPage = new SignupPage();
        signupPage.navigateTo();
    });

    it('should signup tenant when form is valid', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/signup');
        expect(signupPage.getRoleSelect().isDisplayed()).toBeTruthy();

        signupPage.getRoleSelect().sendKeys('TENANT');
        expect(signupPage.getFirstNameInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getLastNameInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getPhoneNumberInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getUsernameInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getEmailInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getPasswordInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getSignupButton().isDisplayed()).toBeTruthy();
        expect(signupPage.getSignupButton().isEnabled()).toBeFalsy();

        signupPage.getFirstNameInput().sendKeys('Invalid');
        signupPage.getLastNameInput().sendKeys('Tenant');
        signupPage.getPhoneNumberInput().sendKeys('12345678');
        signupPage.getUsernameInput().sendKeys(`tenant-${Date.now()}`);
        signupPage.getEmailInput().sendKeys(`tenant-${Date.now()}@mail`);
        signupPage.getPasswordInput().sendKeys('tenant');

        expect(signupPage.getSignupButton().isEnabled()).toBeTruthy();
        signupPage.getSignupButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/signin');
    });

    it('should disable tenant signup when invalid mail or username already taken', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/signup');
        expect(signupPage.getRoleSelect().isDisplayed()).toBeTruthy();

        signupPage.getRoleSelect().sendKeys('TENANT');
        expect(signupPage.getFirstNameInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getLastNameInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getPhoneNumberInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getUsernameInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getEmailInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getPasswordInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getSignupButton().isDisplayed()).toBeTruthy();
        expect(signupPage.getSignupButton().isEnabled()).toBeFalsy();

        signupPage.getFirstNameInput().sendKeys('Invalid');
        signupPage.getLastNameInput().sendKeys('Tenant');
        signupPage.getPhoneNumberInput().sendKeys('12345678');
        signupPage.getUsernameInput().sendKeys(`tenant-${Date.now()}`);
        signupPage.getEmailInput().sendKeys('tenant');
        signupPage.getPasswordInput().sendKeys('tenant');
        expect(signupPage.getSignupButton().isEnabled()).toBeFalsy();

        signupPage.getEmailInput().sendKeys(`-${Date.now()}@mail`);
        signupPage.getUsernameInput().clear();
        signupPage.getUsernameInput().sendKeys('luka');
        expect(signupPage.getSignupButton().isEnabled()).toBeFalsy();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/signup');
    });

    it('should signup company when form is valid', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/signup');
        expect(signupPage.getRoleSelect().isDisplayed()).toBeTruthy();

        signupPage.getRoleSelect().sendKeys('COMPANY');
        expect(signupPage.getCompanyNameInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getCompanyAddressInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getCompanyDescriptionInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getCompanyWorkAreaSelect().isDisplayed()).toBeTruthy();
        expect(signupPage.getPhoneNumberInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getUsernameInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getEmailInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getPasswordInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getSignupButton().isDisplayed()).toBeTruthy();
        expect(signupPage.getSignupButton().isEnabled()).toBeFalsy();

        signupPage.getCompanyNameInput().sendKeys('Valid company');
        signupPage.getCompanyAddressInput().sendKeys('Company address');
        signupPage.getCompanyDescriptionInput().sendKeys('Company description');
        const allOptions = element.all(by.id('workarea'));
        const firstOption = allOptions.first();
        firstOption.click();
        signupPage.getPhoneNumberInput().sendKeys('12345678');
        signupPage.getUsernameInput().sendKeys(`company-${Date.now()}`);
        signupPage.getEmailInput().sendKeys(`company-${Date.now()}@mail`);
        signupPage.getPasswordInput().sendKeys('company');

        expect(signupPage.getSignupButton().isEnabled()).toBeTruthy();
        signupPage.getSignupButton().click();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/signin');
    });

    it('should disable company signup when invalid mail or work area not set', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/signup');
        expect(signupPage.getRoleSelect().isDisplayed()).toBeTruthy();

        signupPage.getRoleSelect().sendKeys('COMPANY');
        expect(signupPage.getCompanyNameInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getCompanyAddressInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getCompanyDescriptionInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getCompanyWorkAreaSelect().isDisplayed()).toBeTruthy();
        expect(signupPage.getPhoneNumberInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getUsernameInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getEmailInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getPasswordInput().isDisplayed()).toBeTruthy();
        expect(signupPage.getSignupButton().isDisplayed()).toBeTruthy();
        expect(signupPage.getSignupButton().isEnabled()).toBeFalsy();

        signupPage.getCompanyNameInput().sendKeys('Invalid company');
        signupPage.getCompanyAddressInput().sendKeys('Company address');
        signupPage.getCompanyDescriptionInput().sendKeys('Company description');
        signupPage.getPhoneNumberInput().sendKeys('12345678');
        signupPage.getUsernameInput().sendKeys(`company-${Date.now()}`);
        signupPage.getEmailInput().sendKeys('company');
        signupPage.getPasswordInput().sendKeys('company');
        expect(signupPage.getSignupButton().isEnabled()).toBeFalsy();

        const allOptions = element.all(by.id('workarea'));
        const firstOption = allOptions.first();
        firstOption.click();
        expect(signupPage.getSignupButton().isEnabled()).toBeFalsy();

        signupPage.getEmailInput().sendKeys(`-${Date.now()}@company`);
        expect(signupPage.getSignupButton().isEnabled()).toBeTruthy();

        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/signup');
    });
});
