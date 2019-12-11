create table public.gallery
(
	art_id serial not null
		constraint gallery_pk
			primary key,
	user_id integer not null
		constraint gallery_user_id_fkey
			references public.users,
	data text not null,
	is_private boolean not null
);

