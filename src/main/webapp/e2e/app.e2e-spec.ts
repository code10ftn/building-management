import { AppPage } from './app.po';

describe('webapp App', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  it('should 1 equal 1', () => {
    page.navigateTo();
    expect(1).toEqual(1);
  });
});
