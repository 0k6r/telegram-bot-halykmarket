create table keyboard (
                          id          int not null constraint KEYBOARD_ID_UINDEX unique,
                          button_ids  varchar(4096),
                          inline      boolean default false,
                          comment     varchar(4096),
                          constraint KEYBOARD_PK primary key (ID)
);


INSERT INTO keyboard (ID, BUTTON_IDS, INLINE, COMMENT) VALUES (1, '15,16;17,18;19', false, 'Главное меню для пользователя');
INSERT INTO keyboard (ID, BUTTON_IDS, INLINE, COMMENT) VALUES (2, '3;4', false, 'Выбор языка при старте');
INSERT INTO keyboard (ID, BUTTON_IDS, INLINE, COMMENT) VALUES (3, '2', true, 'Поделиться номером телефона');

