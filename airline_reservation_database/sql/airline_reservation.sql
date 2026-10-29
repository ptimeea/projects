--1
-- Create tables 
CREATE TABLE Pilot ( 
id_pilot  INTEGER NOT NULL PRIMARY KEY, 
nume  VARCHAR(50) NOT NULL, 
grad  VARCHAR(50), 
email  VARCHAR(50) 
); 

CREATE TABLE Zbor ( 
id_zbor  INTEGER NOT null PRIMARY KEY, 
cod_zbor VARCHAR(10),
origine  VARCHAR(50),
destinatie  VARCHAR(50),
data_zbor DATE,
durata_min INTEGER,
id_pilot INTEGER not null references Pilot(id_pilot),
pret_baza INTEGER
); 

CREATE TABLE Pasager ( 
id_pas  INTEGER NOT NULL PRIMARY KEY, 
nume  VARCHAR(50), 
prenume  VARCHAR(50), 
telefon  VARCHAR(20), 
email  VARCHAR(50)
);

create table Rezervare (
id_rez integer not null primary key,
id_pas integer not null references Pasager(id_pas),
id_zbor integer not null references Zbor(id_zbor),
data_rezervare date,
total integer,
status varchar(20),
metoda_plata varchar(20)
);

create table Detaliu_Rezervare(
id_rez integer not null references Rezervare(id_rez),
clasa varchar(20),
loc varchar(10),
pret_unitar integer,
reducere_pct integer
);

alter table Pilot add telefon varchar(20);
select* from Pilot;

--2
alter table pilot  
add check (char_length(nume)>=3);

alter table Zbor
add check (pret_baza>0);

alter table Rezervare
add check(
	metoda_plata is null or metoda_plata in ('cash','card')
);

alter table Detaliu_rezervare
add check(
	reducere_pct>=0 and reducere_pct<=50
);

alter table Detaliu_Rezervare
add check (pret_unitar>=0);

alter table Rezervare
add check(
	status in('inregistrata','confirmata','platita','anulata')
);

alter table Detaliu_Rezervare
add check(
	clasa in('Economy','Business','Premium')
);

--3

--Query: 3 a)
select* from zbor as z 
where(z.durata_min > 180);

--Query: 3 b)
select* from pasager p 
where(p.email not like '%@%');

--Query: 3 c)
select* from zbor z join pilot p on z.id_pilot = p.id_pilot 
where(p.nume = 'Popescu Alexandru');

insert into pilot values
(1, 'Popescu Alexandru', 'Capitan', 'ap@test.com'),
(2, 'Dumitrescu Ion', 'Copilot', 'ion@test.com'),
(3, 'Ionescu Maria', 'Pilot', 'maria@test.com');

insert into zbor values
(1, 'RO009', 'Bucuresti', 'Paris','2026-01-15', 200, 1, 500),
(2, 'RO235', 'Istanbul', 'Paris','2026-01-14', 170, 2, 300),
(3, 'RO765', 'Bucuresti', 'Madrid','2026-02-15', 190, 3, 700),
(4, 'RO654', 'Atena', 'Oradea','2026-01-20', 150, 1, 200);

insert into pasager values
(1, 'Popescu','Ana',null,'ana@test.com'),
(2,'Ionescu','Gheorghe',null,'gheorghe.test');

--4

--Query: 4 a)
select p.nume, z.cod_zbor, z.destinatie,
r.data_rezervare, r.total, r.status
from pasager p join Rezervare r on p.id_pas = r.id_pas 
join zbor z on z.id_zbor = r.id_zbor;

--Query: 4 b)
select distinct p.* from pasager p join rezervare r on p.id_pas=r.id_pas
join zbor z on z.id_zbor = r.id_zbor
join pilot pi on pi.id_pilot = z.id_pilot 
where(pi.grad = 'Capitan');


insert into Rezervare values
(1,1,1,'2026-01-05',500,'inregistrata','cash'),
(2,2,2,'2026-01-06',300,'confirmata','cash'),
(3,1,3,'2026-01-10',650,'platita','card'),
(4,2,4,'2026-01-12',200,'anulata',null);

insert into detaliu_rezervare values
(1,'Economy','4A', 500, 0),
(2,'Business','10C', 300, 10),
(2,'Premium','12B', 700, 50),
(4,'Economy','20D', 200, 5);

--5

--Query: 5 a)
select* from pasager p
where p.id_pas not in(
	select id_pas from rezervare
);

insert into pasager values
(3, 'Marinescu','Valeria','1234','valeria@test.com'),
(4,'Anghelescu','Marian','5678','amarian@test.com');

--Query: 5 b)
select* from zbor
where id_zbor in(
	select id_zbor from rezervare
	where status = 'confirmata'
);

--6

--Query: 6 a)
select 
	destinatie, 
	count(*) as nr_zboruri, 
	avg(durata_min) as durata_medie
from zbor 
group by destinatie;

--Query: 6 b)
select p.nume as pilot, count(z.id_zbor) as nr_zboruri
from pilot p join zbor z on z.id_pilot = p.id_pilot
group by p.nume;

--7
insert into pilot values
(12, 'Pop Andreea', 'Capitan', 'andreea@test.com'),
(13, 'Velicea Matei', 'Pilot', 'matei@test.com');

insert into zbor values
(5, 'RO200', 'Cluj', 'Paris','2026-01-15', 140, 12, 750);

insert into zbor values
(6, 'RO780', 'Cluj', 'Cairo','2023-11-15', 210, 13, 460);

insert into rezervare values
(5,2,6,'2023-11-06',480,'confirmata','cash');

insert into detaliu_rezervare values
(5,'Business','1F', 300, 10);

insert into detaliu_rezervare values
(1,'Premium','24C', 700, 20);


update rezervare r
set status = 'anulata'
where r.id_zbor in (
	select z.id_zbor
	from zbor z
	where z.data_zbor<'2024-01-01'
);

update detaliu_rezervare 
set pret_unitar = pret_unitar * (0.95), reducere_pct = 5
where id_rez = 1;

select* from detaliu_rezervare;

--8
create or replace function actualizeaza_total()
returns trigger as $$
begin
	update rezervare
	set total = (
		select coalesce(sum(pret_unitar), 0)
		from detaliu_rezervare
		where id_rez = coalesce(new.id_rez,old.id_rez)
	)
	where id_rez = coalesce(new.id_rez,old.id_rez);
	return null;
end;
$$ language plpgsql;

create trigger trg_actualizare_total
after insert or update or delete
on detaliu_rezervare
for each row
execute function actualizeaza_total();

insert into detaliu_rezervare values
(1,'Business','14E', 300, 10);

create or replace function verifica_pret()
returns trigger as $$
declare
v_pret_baza integer;
begin
	select z.pret_baza
	into v_pret_baza
	from rezervare r
	join zbor z on z.id_zbor = r.id_zbor
	where r.id_zbor = NEW.id_rez;
	if new.pret_unitar < v_pret_baza*0.5 then
		raise exception 'Pretul unitar (%) e mai mic decat 50%% din pretul de baza(%)',
		new.pret_unitar, v_pret_baza;
	end if;
	return new;
end;
$$ language plpgsql;

create trigger trg_verificare_pret_minim
before insert or update
on detaliu_rezervare 
for each row 
execute function verifica_pret();

--insert into detaliu_rezervare values
--(3,'Business','23F', 200, 50);

CREATE VIEW rezervare_detaliataB AS 
SELECT  
r.id_rez, r.data_rezervare, r.status, r.total, 
p.id_pas, p.nume AS nume_pasager, p.prenume, 
z.id_zbor, z.cod_zbor, z.origine, z.destinatie, z.data_zbor, 
dr.clasa, dr.loc, dr.pret_unitar, dr.reducere_pct 
FROM rezervare r 
JOIN pasager p ON p.id_pas = r.id_pas 
JOIN zbor z ON z.id_zbor = r.id_zbor 
JOIN detaliu_rezervare dr ON dr.id_rez = r.id_rez; 

create or replace function trg_ins_detaliu_rez()
returns trigger as $$
declare
v_status rezervare.status%type;
begin
	select status
	into v_status
	from rezervare
	where id_rez = new.id_rez;
	if not found then
		raise exception 'Rezervarea cu id %-ul nu exista', new.id_rez;
	end if;
	if v_status = 'anulata' then
		raise exception 'Nu se pot adauga detalii pt o rezervare anulata (id: %)',
		new.id_rez;
	end if;
	return null;
end;
$$ language plpgsql;

create trigger trg_ins_detaliu_rez 
before insert on detaliu_rezervare
for each row 
execute function trg_ins_detaliu_rez();

insert into detaliu_rezervare values
(2,'Economy','15B',320,5);

--insert into detaliu_rezervare values
--(4,'Business','2A',250,0);

--insert into detaliu_rezervare values
--(27,'Business','2A',250,0);


insert into Pilot values
(4, 'Ionescu Alina', 'Capitan', 'alina@test.com', '0723456789'),
(5, 'Marian Robert', 'Copilot', 'robert@test.com', '0767856987');

insert into zbor values
(7, 'RO900', 'Bucuresti', 'Londra','2026-03-10', 180, 4, 600),
(8, 'RO785', 'Paris', 'Roma','2026-03-12', 120, 5, 350),
(9, 'RO955', 'Madrid', 'Berlin','2023-08-01', 160, 4, 400);

insert into pasager values
(5,'Georgescu','Ioana','075678980', 'ioana@test.com'),
(6,'Popa','Mihai','0755678999', 'mihai@test.com');

insert into Rezervare values
(6,5,7,'2026-01-05',600,'inregistrata','cash'),
(7,6,8,'2026-01-06',350,'confirmata','cash'),
(8,5,9,'2023-06-10',400,'platita','card');

insert into detaliu_rezervare values
(6,'Economy','1A', 600, 0),
(6,'Business','1B', 700, 5),
(7,'Economy','5C', 350, 0),
(7,'Premium','2D', 500, 10),
(8,'Business','10A', 400, 0),
(8,'premium','10B', 450, 5);
select* from pilot;
select* from zbor;
select* from pasager;
select* from rezervare;
select* from detaliu_rezervare;

--proceduri stocate
alter table zbor 
add disponibil boolean default true;

create or replace function marcheaza_zbor(p_id_zbor integer)
returns void as $$
begin
	update zbor
	set disponibil = false
	where id_zbor = p_id_zbor;
	if not found then
		raise exception 'Zborul nu a fost gasit';
	end if;
end;
$$ language plpgsql;

create or replace function calc_reduceri(p_id_rez integer)
returns numeric as $$
declare v_total_reduceri numeric := 0;
begin
	select coalesce(sum(pret_unitar*reducere_pct/100.0), 0)
	into v_total_reduceri
	from detaliu_rezervare
	where id_rez = p_id_rez;
	if not found then
		v_total_reduceri := 0;
	end if;
	return v_total_reduceri;
end;
$$ language plpgsql;



