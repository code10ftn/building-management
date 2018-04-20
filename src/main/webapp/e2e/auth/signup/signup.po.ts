import { browser, by, element } from 'protractor';

export class SignupPage {
    navigateTo() {
        return browser.get('signup');
    }

    getRoleSelect() {
        return element(by.css('select[name="role"]'));
    }

    getFirstNameInput() {
        return element(by.css('input[name="firstName"]'));
    }

    getLastNameInput() {
        return element(by.css('input[name="lastName"]'));
    }

    getCompanyNameInput() {
        return element(by.css('input[name="companyName"]'));
    }

    getCompanyAddressInput() {
        return element(by.css('input[name="address"]'));
    }

    getCompanyDescriptionInput() {
        return element(by.css('input[name="description"]'));
    }

    getCompanyWorkAreaSelect() {
        return element(by.css('select[name="workArea"]'));
    }

    getPhoneNumberInput() {
        return element(by.css('input[name="phoneNumber"]'));
    }

    getUsernameInput() {
        return element(by.css('input[name="username"]'));
    }

    getEmailInput() {
        return element(by.css('input[name="email"]'));
    }

    getPasswordInput() {
        return element(by.css('input[name="password"]'));
    }

    getSignupButton() {
        return element(by.id('signupButton'));
    }
}
