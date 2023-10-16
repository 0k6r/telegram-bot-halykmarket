create sequence hibernate_sequence start with 1 increment by 1;

create table properties (
                            id          integer not null,
                            name        varchar(4096),
                            value       varchar(4096),
                            latitude    varchar(500),
                            longitude   varchar(500),primary key (id)
);

insert into properties (id, name, value)        values (1, 'botUsername',   'time_controller_bot');
insert into properties (id, name, value)        values (2, 'botToken',      '6556541432:AAGAB2dxLtEdaxlgSwjk0RIJNYU8sxQBlNs');

