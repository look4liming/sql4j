--[insert_new_config]--
insert into config(config_key,config_value) 
values(?configKey, ?configValue);

--[select_sys_name]--
select pk,config_key configKey, config_value configValue
from config t
where t.config_key='sys_name'
order by t.pk desc
page(capacity 2 index 1);