import { SigninPage } from '../auth/signin/signin.po';
import { NavPage } from '../nav/nav.po';

export default class SecurityUtil {

    static authenticate(username: string, password: string): void {
        const signinPage = new SigninPage();

        signinPage.navigateTo();
        signinPage.getUsernameInput().sendKeys(username);
        signinPage.getPasswordInput().sendKeys(password);
        signinPage.getSigninButton().click();
    }

    static signout(): void {
        const navPage = new NavPage();

        navPage.getSignoutButton().click();
    }
}
