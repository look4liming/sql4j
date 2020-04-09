
--[select_person_by_username]--
select pk,username,password,department_pk departmentPk
from person
where username=?username;

--[select_joined_person_department]--
select t1.pk,t1.username,t1.password,t1.department_pk departmentPk,t2.name departmentName
from person t1
left join department t2
on t1.department_pk=t2.pk
where t1.username=?username and t1.ts is not null;

--[select_person_by_department_pk]--
select pk,username,password,department_pk departmentPk
from person
where department_pk=?departmentPk
