create table public.likes
(
	user_id integer not null
		constraint likes_user_id_fkey
			references public.users,
	art_id integer not null
		constraint likes_art_id_fkey
			references public.gallery
);

