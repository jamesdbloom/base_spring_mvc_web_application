create table user (
  id                        varchar(255) not null,
  name                      varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  one_time_token            varchar(255),
  version                   integer not null,
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;

create sequence user_seq;



