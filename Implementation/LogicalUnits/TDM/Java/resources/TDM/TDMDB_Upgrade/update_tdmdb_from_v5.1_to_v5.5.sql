ALTER TABLE public.task_execution_list  
ADD COLUMN IF NOT EXISTS clean_redis boolean DEFAULT false;
