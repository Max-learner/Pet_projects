create extension if not exists pgcrypto;

update usr set password = crypt(password, gen_salt('bf', 8));	-- gen_salt - additional value to increase pass strength via encoding