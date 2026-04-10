--3
set search_path to classicmodels

create or replace procedure inflation (p_productcode text)
language plpgsql
as $$
begin
update products
set buyprice = buyprice*1.05
where productcode = p_productcode;
end;
$$;

call inflation ('S18_2581')

select * from orders

--4
create or replace function get_order_count_by_status(IN p_status text, out p_total_order integer)
language plpgsql
as $$
--declare 
--v_total_order integer;
begin
select count(*)
from orders into p_total_order
where status = p_status;
raise Info 'Die Anzahl der Bestellungen mit dem Status % beträgt: %', p_status, p_total_order;
end;
$$;

select get_order_count_by_status('Delayed')

--5
create or replace procedure preiserhoehung_absolut (p_productcode text, increase_amount decimal)
language plpgsql
as $$
begin
update products
set buyprice = buyprice + increase_amount
where productcode = p_productcode;
end;
$$;

select buyprice from products
where productcode = 'S18_2581'

call preiserhoehung_absolut ('S18_2581', 10.99)

--6
create or replace function get_profit(p_productcode text, out p_msrp decimal, out p_buyprice decimal, out p_profit decimal)
language plpgsql
as $$
begin 
select msrp, buyprice, msrp-buyprice as profit 
into p_msrp, p_buyprice, p_profit
from products
where productcode = p_productcode;
if p_profit >= 0 then
raise info 'Ihre Eingaben: msrp = %€, buyprice =
% €. Der Gewinn beträgt % €.', p_msrp, p_buyprice, p_profit;
elsif p_profit < 0  then
raise info 'Ihre Eingaben: msrp = %€, buyprice =
% €. Der Verlust beträgt % €.', p_msrp, p_buyprice, p_profit;
else
raise exception 'Überprüfen Sie bitte Ihre Eingaben.';
end if;
end;
$$;

select get_profit('S18_2581')

--7
set search_path to floppy

create or replace function get_average_score_by_group (p_group int, out p_avg_score decimal)
language plpgsql
as $$
begin
select avg(s.final_score)
into p_avg_score
from sessions as s
inner join players as p
on s.player = p.player_name
where p.group = p_group;
end;
$$;

select get_average_score_by_group (1)

--8
create or replace procedure rename_player(p_player_name text, p_new_name text)
language plpgsql
as $$
begin
update players
set player_name = p_new_name
where player_name = p_player_name;
raise info 'Der Spieler % heißt nun %.', p_player_name, p_new_name;
end;
$$;

call rename_player ('test', 'Testing_Rename_Player')

select * from players

--9
create or replace procedure reset_session_score (p_session_id text)
language plpgsql
as $$
begin
update sessions
set final_score = 0
where session_id = p_session_id;
if found then
raise info 'Score zurückgesetzt';
else raise info 'Nicht gefunden';
end if;
end;
$$;

call reset_session_score('0693da1d-d8f2-4c8b-9daa-861b5b5911a2')

--10
create or replace procedure double_final_score()
language plpgsql
as $$
declare v_count integer;
begin
select count(*)
from sessions into v_count;
update sessions
set final_score = 2*final_score;
raise info 'Die Anzahl der verdoppelten Einträge beträgt: %', v_count;
end;
$$;

call double_final_score()







