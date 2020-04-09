--[update_department_5]--
update department 
set name='部门555'
where pk='department-5';

insert into department(pk,name) 
values('department-666','部门666');

delete from department 
where pk='department-666';

--[select_department_5]--
select pk,trim(name||'(===@@@===)       ')||'XXX' as name
from department t
where t.pk='department-5';
