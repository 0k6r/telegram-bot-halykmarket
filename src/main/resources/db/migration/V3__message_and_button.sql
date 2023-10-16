create table message (
                         id          integer,
                         name        varchar(8096) not null,
                         photo       varchar,
                         keyboard_id integer,
                         file        varchar,
                         file_type   varchar,
                         language_id integer
);

create table button (
                        id              integer      not null,
                        name            varchar(300) not null,
                        command_id      integer default 0,
                        url             varchar(4096),
                        request_contact Boolean default false,
                        message_id      integer,
                        lang_id         integer
);

INSERT INTO message (id, name, photo, keyboard_id, file, file_type, language_id)VALUES (1, 'Команда не найдена', null, 1, null, null, 1);
INSERT INTO message (id, name, photo, keyboard_id, file, file_type, language_id) VALUES (1, 'Команда не найдена', null, 1, null, null, 2);
INSERT INTO message (id, name, photo, keyboard_id, file, file_type, language_id) VALUES (2, 'Добро пожаловать', null, 1, null, null, 1);
INSERT INTO message (id, name, photo, keyboard_id, file, file_type, language_id) VALUES (2, 'Добро пожаловать', null, 1, null, null, 2);
INSERT INTO message (id, name, photo, keyboard_id, file, file_type, language_id) VALUES (3, '🥰', null, null, null, null, 1);
INSERT INTO message (id, name, photo, keyboard_id, file, file_type, language_id) VALUES (3, '🥰', null, null, null, null, 2);
insert into message(id, name, photo, keyboard_id, file, file_type, language_id) values (4, concat('Интерфейс тілін таңдаңыз.',E'\n','-------------------------------',E'\n','Выберите язык интерфейса.'), null, 2, null, null, 1);
insert into message(id, name, photo, keyboard_id, file, file_type, language_id) values (4, concat('Интерфейс тілін таңдаңыз.',E'\n','-------------------------------',E'\n','Выберите язык интерфейса.'), null, 2, null, null, 2);
insert into message(id, name, photo, keyboard_id, file, file_type, language_id) values (5, 'Поделиться номером телефона', null, 2, null, null, 1);
INSERT INTO message (id, name, photo, keyboard_id, file, file_type, language_id) VALUES (5, 'Поделиться номером телефона', null, 3, null, null, 2);
INSERT INTO message (id, name, photo, keyboard_id, file, file_type, language_id) VALUES (163, 'О чат-боте', null, null, null, null, 1);
INSERT INTO message (id, name, photo, keyboard_id, file, file_type, language_id) VALUES (163, 'Чат-бот туралы', null, null, null, null, 2);

-----------------------------------------------------------------------------------


INSERT INTO button (ID, NAME, COMMAND_ID, URL, REQUEST_CONTACT, MESSAGE_ID, LANG_ID) VALUES (1, '/start', 2, null, false, 4, 2);
INSERT INTO button (ID, NAME, COMMAND_ID, URL, REQUEST_CONTACT, MESSAGE_ID, LANG_ID) VALUES (1, '/start', 2, null, false, 4, 1);

INSERT INTO button (ID, NAME, COMMAND_ID, URL, REQUEST_CONTACT, MESSAGE_ID, LANG_ID) VALUES (2, 'Поделиться номером телефона', 0, null, true, null, 1);
INSERT INTO button (ID, NAME, COMMAND_ID, URL, REQUEST_CONTACT, MESSAGE_ID, LANG_ID) VALUES (2, 'Поделиться номером телефона', 0, null, true, null, 2);

INSERT INTO button (ID, NAME, COMMAND_ID, URL, REQUEST_CONTACT, MESSAGE_ID, LANG_ID) VALUES (3, '🇰🇿 Қазақ тілінде', 3, null, false, null, 1);
INSERT INTO button (ID, NAME, COMMAND_ID, URL, REQUEST_CONTACT, MESSAGE_ID, LANG_ID) VALUES (3, '🇰🇿 Қазақ тілінде', 3, null, false, null, 2);

INSERT INTO button (ID, NAME, COMMAND_ID, URL, REQUEST_CONTACT, MESSAGE_ID, LANG_ID) VALUES (4, '🇷🇺 На русском языке', 3, null, false, null, 2);
INSERT INTO button (ID, NAME, COMMAND_ID, URL, REQUEST_CONTACT, MESSAGE_ID, LANG_ID) VALUES (4, '🇷🇺 На русском языке', 3, null, false, null, 1);

INSERT INTO button (ID, NAME, COMMAND_ID, URL, REQUEST_CONTACT, MESSAGE_ID, LANG_ID) VALUES (15, 'Сome', 1, null, false, null, 2);
INSERT INTO button (ID, NAME, COMMAND_ID, URL, REQUEST_CONTACT, MESSAGE_ID, LANG_ID) VALUES (15, 'Come', 1, null, false, null, 1);

INSERT INTO button (ID, NAME, COMMAND_ID, URL, REQUEST_CONTACT, MESSAGE_ID, LANG_ID) VALUES (16, 'Out', 1, null, false, null, 1);
INSERT INTO button (ID, NAME, COMMAND_ID, URL, REQUEST_CONTACT, MESSAGE_ID, LANG_ID) VALUES (16, 'Out', 1, null, false, null, 2);

INSERT INTO button (id, name, command_id, url, request_contact, message_id, lang_id) VALUES (17, 'Custom come', 1, null, false, null, 1);
INSERT INTO button (id, name, command_id, url, request_contact, message_id, lang_id) VALUES (17, 'Custom come', 1, null, false, null, 2);

INSERT INTO button (id, name, command_id, url, request_contact, message_id, lang_id) VALUES (18, 'Custom out', 1, null, false, null, 1);
INSERT INTO button (id, name, command_id, url, request_contact, message_id, lang_id) VALUES (18, 'Custom out', 1, null, false, null, 2);

INSERT INTO button (id, name, command_id, url, request_contact, message_id, lang_id) VALUES (19, 'Отчет', 1, null, false, null, 1);
INSERT INTO button (id, name, command_id, url, request_contact, message_id, lang_id) VALUES (19, 'Отчет', 1, null, false, null, 2);
