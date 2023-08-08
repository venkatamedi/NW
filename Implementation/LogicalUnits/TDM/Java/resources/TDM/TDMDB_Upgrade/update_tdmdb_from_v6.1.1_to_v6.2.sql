-- Update source_environment_id in task_execution_list based on the value in tasks

update task_execution_list l set source_environment_id = (select source_environment_id from tasks t where t.task_id = l.task_id) where source_environment_id is null;

-- Update user_name and user_id to lower case, as TDM is now case insensitive

update environment_role_users set username = lower(username), user_id = lower(user_id) where username <> 'ALL';

update activities set username = lower(username), user_id = lower(user_id);

update environment_owners set user_name = lower(user_name), user_id = lower(user_id);
