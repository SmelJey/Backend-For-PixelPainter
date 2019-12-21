create table public.users
(
	user_id serial not null
		constraint users_pk
			primary key,
	login varchar(32) not null,
	password varchar(32) not null,
	token varchar(32),
	email varchar(64) not null,
	first_name varchar(64),
	second_name varchar(64),
	age integer,
	vk_profile varchar(64),
	country varchar(64),
	token_time timestamp
);

create unique index users_login_uindex
	on public.users (login);

create unique index users_token_uindex
	on public.users (token);

create unique index users_email_uindex
	on public.users (email);

