SET search_path TO media;

CREATE TABLE titel (
  titel_id serial primary key,
  titelname text not null,
  erscheinungsjahr smallint not null,
  genre text not null,
  altersfreigabe smallint not null,
  typ text not null,
  -- check if 'Film' or 'Serie', only those two allowed
  check (typ in('Film','Serie'))
)

CREATE TABLE episoden (
  episoden_id serial primary key,
  titel_id integer not null,
  episodentitel text not null,
  staffelnummer smallint not null,
  episodennummer smallint not null,
  erscheinungsdatum date not null,
  foreign key (titel_id) references titel(titel_id) on delete restrict,
  --titel_id, staffelnummer and episodennummer are unique together because it only exists one episode per season in a title
  unique(titel_id, staffelnummer, episodennummer)
)

CREATE TABLE personen (
  person_id serial primary key,
  vorname text not null,
  nachname text not null,
  geburtsdatum date not null,
  rolle text
)

CREATE TABLE bewertungen (
  bewertung_id serial primary key,
  titel_id integer not null,
  benutzer_id serial not null,
  benutzername text not null,
  bewertungsdatum date not null,
  sterne smallint not null,
  kommentar text not null,
  foreign key (titel_id) references titel(titel_id) on delete restrict,
  --sterne only from 1-10
  check (sterne between 1 and 10),
  --one rating belongs to one user
  unique(bewertung_id, benutzer_id, benutzername)
)

CREATE TABLE mitwirkungen (
  person_id integer not null,
  titel_id integer not null,
  funktion text not null,
  charaktername text,
  foreign key (titel_id) references titel(titel_id) on delete restrict,
  foreign key (person_id) references personen(person_id) on delete restrict
)