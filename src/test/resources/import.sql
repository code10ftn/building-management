-- Supervisor (as company)
insert into registered_user(id, username, password, email, phone_number, last_password_reset) values(9, 'haus', '$2a$10$knXwRutBBg1USUZLqHkbQeIBjUwu52xBAzXjrK72j.iX0GBUJA44q', 'haus@haus', '021 123 456', null)
insert into company(id, registered_name, address, description, work_area) values(9, 'Haus Majstor i Higijena', 'Bulevar oslobodjenja 76a, Novi Sad, Srbija 21000', 'Kompanija specijalizovana za ciscenje i odrzavanje poslovnih prostora i stambenih zgrada.', 'HOUSEKEEPING')
insert into user_role(user_id, authority) values(9, 'COMPANY')
insert into user_role(user_id, authority) values(9, 'SUPERVISOR')

-- Building
insert into building(id, address, supervisor_id) values(1, 'Adresa 174c', 9)
insert into building(id, address, supervisor_id) values(2, 'I can be deleted', 9)

--Apartment
insert into apartment(id, number, building_id) values(1, 4, 1)
insert into apartment(id, number, building_id) values(2, 2, 1)

-- Admin
insert into registered_user(id, username, password, email, phone_number, last_password_reset) values(1, 'admin', '$2a$10$XeciV2uxy/QllL1XidywReLyqEkN0mly2qZ88uffoQzG0YkKWUyo.', 'admin@admin', '021 123 456', null)
insert into user_role(user_id, authority) values(1, 'ADMIN')

-- Tenant
insert into registered_user(id, username, password, email, phone_number, last_password_reset) values(2, 'luka', '$2a$10$Sd4vX3ZWVngeuJt5R3cTeuvXQ/seHEDepnLZnv1cBZ61kmkWiRJ8K', 'luka@luka', '021 123 456', null)
insert into tenant(id, first_name, last_name, apartment_id) values(2, 'Luka', 'Maletin', 1)
insert into user_role(user_id, authority) values(2, 'TENANT')

insert into registered_user(id, username, password, email, phone_number, last_password_reset) values(3, 'helena', '$2a$10$TwiM9AzyH3EOzyAlpyfXz.it1Im8U9PB38FNNju1ENATJ45dgsdXC', 'helena@helena', '021 123 456', null)
insert into tenant(id, first_name, last_name) values(3, 'Helena', 'Zecevic')
insert into user_role(user_id, authority) values(3, 'TENANT')

insert into registered_user(id, username, password, email, phone_number, last_password_reset) values(4, 'aleksandar', '$2a$10$l8kvFJPaX.JZaAj5C3NwY.4jWf5Nny4jWoNVI8myMmWhPZj41mH6e', 'aleksandar@aleksandar', '021 123 456', null)
insert into tenant(id, first_name, last_name) values(4, 'Aleksandar', 'Nikolic')
insert into user_role(user_id, authority) values(4, 'TENANT')

insert into registered_user(id, username, password, email, phone_number, last_password_reset) values(5, 'nemanja', '$2a$10$sHHzmMpyBfcZSORrhilYOeqrsFRean5GD1YGVAF44nxUyQ1GAg1sW', 'nemanja@nemanja', '021 123 456', null)
insert into tenant(id, first_name, last_name, apartment_id) values(5, 'Nemanja', 'Brzak', 2)
insert into user_role(user_id, authority) values(5, 'TENANT')

-- Company
insert into registered_user(id, username, password, email, phone_number, last_password_reset) values(6, 'gas', '$2a$10$PhCq/GYhnhibNp8o2JSbP.S7S7TufKzLODF8WtXFzWNNM./vpmv8', 'gas@gas', '021 123 456', null)
insert into company(id, registered_name, address, description, work_area) values(6, 'SRBIJAGAS', 'Narodnog fronta 12, Novi Sad, Srbija 21000', 'Preduzece za transport, distribuciju, skladistenje i trgovinu prirodnim gasom.', 'HEATING')
insert into user_role(user_id, authority) values(6, 'COMPANY')

insert into registered_user(id, username, password, email, phone_number, last_password_reset) values(7, 'vodovod', '$2a$10$KXay5X0Jp8LOqm.mldS5T.83OQr/5GbSORMGiI9DHAhMgScnAepVW', 'vodovod@vodovod', '021 123 456', null)
insert into company(id, registered_name, address, description, work_area) values(7, 'JKP VODOVOD I KANALIZACIJA', 'Masarikova 17, Novi Sad, Srbija 21000', 'Vodovod i kanalizacija.', 'WATER')
insert into user_role(user_id, authority) values(7, 'COMPANY')

-- Supervisor (as tenant)
insert into registered_user(id, username, password, email, phone_number, last_password_reset) values(8, 'upravnik', '$2a$10$/PFnQhc5TDTHmkmJ87IfOO3GBLxvmf9fNwaDpmwE7ndkyHXhtGH7a', 'upravnik@upravnik', '021 123 456', null)
insert into tenant(id, first_name, last_name) values(8, 'Petar', 'Petrovic')
insert into user_role(user_id, authority) values(8, 'TENANT')
insert into user_role(user_id, authority) values(8, 'SUPERVISOR')

-- Building's company
insert into building_company(building_id, company_id) values(1, 6)

-- Building's malfunction
insert into malfunction(id, description, report_date, building_id, creator_id) values(1, 'desc', '2017-09-09', 1, 2)

-- Building's meeting
insert into meeting(id, meeting_date, building_id) values (1, '2017-01-25', 1);
insert into topic(id, content, accepted, meeting_id) values (1, 'Some topic', true, 1);
insert into topic(id, content, accepted, meeting_id) values (2, 'Another topic', true, 1);

-- Survey non expired
insert into survey(id, expiration_date, building_id) values (1, to_date('12-12-2222', 'DD-MM-YYYY'), 1)
insert into question(id, text, survey_id) values (1, 'Test question?', 1)
insert into answer(id, text, question_id) values (1, 'Answer to test question.', 1)
insert into answer(id, text, question_id) values (2, 'Second answer to test question.', 1)

-- Survey expired
insert into survey(id, expiration_date, building_id) values (2, to_date('4-12-2017', 'DD-MM-YYYY'), 1)
insert into question(id, text, survey_id) values (2, 'Test question?', 2)
insert into answer(id, text, question_id) values (3, 'Answer to test question.', 2)
insert into answer(id, text, question_id) values (4, 'Second answer to test question.', 2)