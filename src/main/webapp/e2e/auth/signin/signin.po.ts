import { browser, by, element } from 'protractor';

export class SigninPage {
    navigateTo() {
        return browser.get('signin');
    }

    getUsernameInput() {
        return element(by.css('input[name="username"]'));
    }

    getPasswordInput() {
        return element(by.css('input[name="password"]'));
    }

    getSigninButton() {
        return element(by.id('signinButton'));
    }
}
