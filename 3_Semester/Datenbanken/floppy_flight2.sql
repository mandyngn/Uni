set search_path to floppyflight;

--Aufgabe a
select event_type, count(event_type) from events 
group by event_type
order by count(event_type) asc;

--Aufgabe b
select distinct session_id, player, max(final_score) from sessions
group by session_id, player
order by max(final_score) desc;

--Aufgabe c
--timestamp in csv hat nicht das richtige Format
select distinct player, round(avg(
  extract(epoch from to_timestamp(left(timestamp_iso, 19), 'YYYY-MM-DD"T"HH24:MI:SS'))
),2) as durchschnitt from sessions
group by distinct player
order by durchschnitt desc;

--Aufgabe d
select distinct player, sum(final_score) as Gesamtpunktzahl from sessions
group by player
order by sum(final_score) desc; 

--Aufgabe e funktioniert nicht :(
select s.timestamp_iso, s.session_id, p.matrikelnummer, p.player_name, s.os
from sessions as s
inner join players as p
on s.player = p.matrikelnummer
where s.timestamp_iso like '2025-11-10%' and s.os like 'Windows 10%';

--Aufgabe f
select matrikelnummer, player_name from players
order by matrikelnummer asc;

--Aufgabe g
select distinct player, round(avg(final_score), 2)  from sessions
group by player
order by round(avg(final_score), 2) desc;

--Aufgabe h funktioniert nicht :(
select player, session_id, avg(
  extract(epoch from to_timestamp(left(timestamp_iso, 19), 'YYYY-MM-DD"T"HH24:MI:SS'))
) as durchschnitt from sessions
group by player, session_id
having round(avg(
  extract(epoch from to_timestamp(left(timestamp_iso, 19), 'YYYY-MM-DD"T"HH24:MI:SS'))
  ), 2) = (
  select round(min(durchschnitt), 2) from (select avg(
    extract(epoch from to_timestamp(left(timestamp_iso, 19), 'YYYY-MM-DD"T"HH24:MI:SS'))
  ) as durchschnitt from sessions
  group by player, session_id) tem
);
