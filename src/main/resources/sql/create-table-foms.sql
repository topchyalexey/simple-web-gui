CREATE SCHEMA IF NOT EXISTS payment_types;
-- drop table payment_types.forms

create table payment_types.forms( 
	id_form 		serial PRIMARY KEY,
	id_service   	int2 NOT NULL REFERENCES services(id_service),
     func_code   	varchar(40) NOT NULL CHECK (func_code != ''),
     description   	varchar(256) 
     ); 

insert into payment_types.forms( id_service, func_code,  description) values (1, 'verifyWithSum', 'С карты на МТС');

select * from payment_types.forms;