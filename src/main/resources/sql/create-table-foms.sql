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

-- drop table payment_types.form_fields
create table payment_types.form_fields( 
	id_form_field 	serial PRIMARY KEY,
	id_form   	int2 NOT NULL REFERENCES payment_types.forms(id_form),
     code   		varchar(40) NOT NULL CHECK (code != ''),
     type 		varchar(40) NOT NULL CHECK (type in ('STRING', 'NUMBER', 'DATE') ),
     description   	varchar(256)
     ); 

insert into payment_types.form_fields( id_form, code, type, description) values (1, 'pc-serviceId', 'STRING', '');
insert into payment_types.form_fields( id_form, code, type, description) values (1, 'id1', 'STRING', '');
insert into payment_types.form_fields( id_form, code, type, description) values (1, 'receiverProductId', 'STRING', '');
insert into payment_types.form_fields( id_form, code, type, description) values (1, 'sourceProductId', 'STRING', '');
insert into payment_types.form_fields( id_form, code, type, description) values (1, 'sourceProductType', 'STRING', '');

select * from payment_types.form_fields;
