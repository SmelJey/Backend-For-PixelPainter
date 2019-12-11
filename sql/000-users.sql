create table public.users
(
	user_id serial not null
		constraint users_pk
			primary key,
	login varchar(32) not null,
	password varchar(32) not null,
	token varchar(32)
);

create unique index users_login_uindex
	on public.users (login);

create unique index users_token_uindex
	on public.users (token);

