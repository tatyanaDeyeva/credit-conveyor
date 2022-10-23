--liquibase formatted sql
--changeset tdeeva:create_entity_tables

create table if not exists client
(
    id               bigserial primary key,
    last_name        varchar,
    first_name       varchar,
    middle_name      varchar,
    birth_date       date,
    email            varchar,
    gender           varchar,
    marital_status   varchar,
    dependent_amount integer,
    passport         jsonb,
    employment       jsonb,
    account          varchar
);

create table if not exists credit
(
    id                   bigserial primary key,
    amount               decimal,
    term                 int,
    monthly_payment      decimal,
    rate                 decimal,
    psk                  decimal,
    payment_schedule     jsonb,
    is_insurance_enabled boolean,
    is_salary_client     boolean,
    credit_status        varchar
);

create table if not exists application
(
    id             bigserial primary key,
    client_id      bigint,
    credit_id      bigint,
    status         varchar,
    creation_date  timestamp,
    applied_offer  jsonb,
    sign_date      date,
    ses_code       varchar,
    status_history jsonb,
    constraint fk_client
        foreign key (client_id)
            references client(id),
    constraint fk_credit
        foreign key (credit_id)
            references credit(id)
);

