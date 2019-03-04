create table transactions
(
    id integer not null
        constraint transactions_pkey
        primary key,
    state varchar(255),
    city varchar(255)
);



