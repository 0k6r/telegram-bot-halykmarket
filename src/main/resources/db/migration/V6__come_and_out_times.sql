create table come_times
(
    id           serial                   not null,
    user_id      serial                   not null
        references users,
    created_date date NOT NULL DEFAULT now(),
    created_time time NOT NULL
);

create table out_times
(
    id           serial                   not null,
    user_id      serial                   not null
        references users,
    created_date date NOT NULL DEFAULT now(),
    created_time time NOT NULL
);