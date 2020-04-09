--[init_all]--
drop table department;
create table department(
	pk varchar(100),
	name varchar(100),
	ts timestamp not null,
	hash_foremost_db int not null
);
alter table department add primary key(pk);

drop table person;
create table person(
	pk varchar(100) primary key,
	username varchar(100) unique,
	password varchar(100) not null,
	department_pk varchar(100),
	ts timestamp not null,
	hash_foremost_db int not null
);

create table config(
	pk VARCHAR(100) primary key,
	ts TIMESTAMP NOT NULL,
	hash_foremost_db INT NOT NULL,
	config_key varchar(100),
	config_value varchar(500)
);
create index idx_config_key on config(config_key);

insert into department(pk,name) values('department-1','部门1');
insert into department(pk,name) values('department-2','部门2');
insert into department(pk,name) values('department-3','部门3');
insert into department(pk,name) values('department-4','部门4');
insert into department(pk,name) values('department-5','部门5');

insert into person(pk,username,password,department_pk) values('person-1','user1','123456','department-1');
insert into person(pk,username,password,department_pk) values('person-2','user2','123456','department-1');
insert into person(pk,username,password,department_pk) values('person-3','user3','123456','department-2');
insert into person(pk,username,password,department_pk) values('person-4','user4','123456','department-3');
insert into person(pk,username,password,department_pk) values('person-5','user5','123456','department-4');
insert into person(pk,username,password,department_pk) values('person-6','user6','123456','department-5');
