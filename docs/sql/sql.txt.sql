delete from tab_1;
alter table tab_1 add column pk varchar(100);
alter table tab_1 drop primary key;
alter table tab_1 add primary key(pk);
drop index idx_tab_1_name_abc on tab_1;
create unique index idx_tab_1_name_abc on tab_1(name,abc);