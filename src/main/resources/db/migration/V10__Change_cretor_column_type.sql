-- alter table question alter column CREATOR bigint;
alter table question modify creator bigint not null;
-- alter table `comment` modify commentator bigint not null
