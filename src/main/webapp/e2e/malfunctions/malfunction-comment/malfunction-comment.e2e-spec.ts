import { browser } from 'protractor';

import { NavPage } from '../../nav/nav.po';
import SecurityUtil from '../../util/security-util';
import { MalfunctionDetailPage } from '../malfunction-detail/malfunction-details.po';
import { MalfunctionListPage } from '../malfunction-list/malfunction-list.po';
import { MalfunctionCommentPage } from './malfunction-comment.po';

describe('malfunction-comment', () => {

    let navPage: NavPage;
    let malfunctionListPage: MalfunctionListPage;
    let malfunctionDetailPage: MalfunctionDetailPage;
    let malfunctionCommentPage: MalfunctionCommentPage;

    beforeAll(() => {
        SecurityUtil.authenticate('upravnik', 'upravnik');
    });

    afterAll(() => {
        SecurityUtil.signout();
    });

    beforeEach(() => {
        navPage = new NavPage();
        malfunctionListPage = new MalfunctionListPage();
        malfunctionDetailPage = new MalfunctionDetailPage();
        malfunctionCommentPage = new MalfunctionCommentPage();

        navPage.getMalfunctionsButton().click();
        malfunctionListPage.getDetailsButton('1').click();

    });

    it('should add comment when form is valid', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');


        expect(malfunctionCommentPage.getCommentButton().isEnabled()).toBeFalsy();

        malfunctionCommentPage.getCommentInput().sendKeys('test');

        expect(malfunctionCommentPage.getCommentButton().isEnabled()).toBeTruthy();

        malfunctionCommentPage.getCommentButton().click();
        expect(malfunctionCommentPage.getCommentButton().isEnabled()).toBeFalsy();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');
    });

    it('should hide delete button when other user is signed in', () => {
        SecurityUtil.signout();
        SecurityUtil.authenticate('nemanja', 'nemanja');
        navPage.getMalfunctionsButton().click();
        malfunctionListPage.getDetailsButton('1').click();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');
        malfunctionCommentPage.getComments().first().getId().then(id => {
            expect(malfunctionCommentPage.getDeleteButton(id).isPresent()).toBeFalsy();
        });
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');
        SecurityUtil.signout();
        SecurityUtil.authenticate('upravnik', 'upravnik');
    });

    it('should delete comment when comment creator wants to delete it', () => {
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');


        malfunctionCommentPage.getDeleteButtons().last().isPresent();
        malfunctionCommentPage.getDeleteButtons().last().click();
        malfunctionCommentPage.confirmModal.getConfirmButton().click();
        expect(browser.getCurrentUrl()).toMatch('http:\/\/localhost:[1-9]+\/buildings\/[1-9]+\/malfunctions\/[1-9]+');
    });


});
