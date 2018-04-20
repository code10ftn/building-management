import { browser } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import { SigninPage } from './signin.po';

describe('signin', () => {
    let navPage: NavPage;
    let signinPage: SigninPage;

    beforeEach(() => {
        navPage = new NavPage();
        signinPage = new SigninPage();
        signinPage.navigateTo();
    });

    it('should signin user when form is valid', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/signin');

        expect(signinPage.getUsernameInput().isDisplayed()).toBeTruthy();
        expect(signinPage.getPasswordInput().isDisplayed()).toBeTruthy();
        expect(signinPage.getSigninButton().isDisplayed()).toBeTruthy();

        signinPage.getUsernameInput().sendKeys('luka');
        signinPage.getPasswordInput().sendKeys('luka');

        signinPage.getSigninButton().click();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/announcements');
        navPage.getSignoutButton().click();
    });

    it('should disable signin when form is invalid', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/signin');

        expect(signinPage.getUsernameInput().isDisplayed()).toBeTruthy();
        expect(signinPage.getPasswordInput().isDisplayed()).toBeTruthy();
        expect(signinPage.getSigninButton().isDisplayed()).toBeTruthy();
        expect(signinPage.getSigninButton().isEnabled()).toBeFalsy();

        signinPage.getUsernameInput().sendKeys('luka');
        expect(signinPage.getSigninButton().isEnabled()).toBeFalsy();

        signinPage.getPasswordInput().sendKeys('asd');
        expect(signinPage.getSigninButton().isEnabled()).toBeTruthy();
    });

    it('should fail signin when username and password do not match', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/signin');

        expect(signinPage.getUsernameInput().isDisplayed()).toBeTruthy();
        expect(signinPage.getPasswordInput().isDisplayed()).toBeTruthy();
        expect(signinPage.getSigninButton().isDisplayed()).toBeTruthy();
        expect(signinPage.getSigninButton().isEnabled()).toBeFalsy();

        signinPage.getUsernameInput().sendKeys('luka');
        signinPage.getPasswordInput().sendKeys('asd');
        expect(signinPage.getSigninButton().isEnabled()).toBeTruthy();

        signinPage.getSigninButton().click();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/signin');
    });
});
