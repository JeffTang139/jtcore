drop function if exists dna_isleapyear;
drop function if exists dna_isleapmonth;
drop function if exists dna_isleapday;
drop function if exists dna_truncyear;
drop function if exists dna_truncmonth;
drop function if exists dna_truncday;
drop function if exists dna_newrecid;
drop function if exists dna_bigint2hex;
drop function if exists dna_byte2hex;

create function dna_isleapyear(ts timestamp)
returns bit
deterministic
begin
	declare y int;
	set y = year(ts);
	return case when y%400=0 then 1 when y%100=0 then 0 when y%4=0 then 1 else 0 end;
end;

create function dna_isleapmonth(ts timestamp)
returns bit
deterministic
begin
	declare y int;
	set y = year(ts);
	return case when month(ts)<>2 then 0 when y%400=0 then 1 when y%100=0 then 0 when y%4=0 then 1 else 0 end;
end;

create function dna_isleapday(ts timestamp)
returns bit
deterministic
begin
	return case when month(ts)=2 and dayofmonth(ts)=29 then 1 else 0 end;
end;

create function dna_truncyear(ts timestamp)
returns timestamp
deterministic
return timestampadd(year, timestampdiff(year, '1900-1-1', ts), '1900-1-1');

create function dna_truncmonth(ts timestamp)
returns timestamp
deterministic
return timestampadd(month, timestampdiff(month, '1900-1-1', ts), '1900-1-1');

create function dna_truncday(ts timestamp)
returns timestamp
deterministic
return timestampadd(day, timestampdiff(day, '1900-1-1', ts), '1900-1-1');

create function dna_byte2hex(val tinyint unsigned)
returns varchar(2)
deterministic
begin
	if (val is null) then
		return '';
	elseif (val <16) then
		return concat('0', hex(val));
	else
		return hex(val);
	end if;
end;

create function dna_bigint2hex(val bigint unsigned)
returns varchar(16)
deterministic
begin
	declare s varchar(16);
	declare i int;
	if (val is null) then
		return '0000000000000000';
	end if;
	set s = dna_byte2hex(val & 255);
	set i = 1;
	repeat
		set s = concat(dna_byte2hex((val >> (i * 8)) & 255), s);
		set i = i + 1;
	until i = 8 end repeat;
	return s;
end;

create function dna_newrecid()
returns binary(16)
not deterministic
begin
	declare ts bigint;
	declare seq bigint;
	declare m bigint;
	declare l bigint;
	set ts = timestampdiff(second, date'1970-1-1', now());
	set seq = (uuid_short() & 1048575) << 4;
	set m = ((ts << 24) & 9223372036854775807) + seq;
	set l = floor(rand()*9223372036854775807);
	return unhex(concat(dna_bigint2hex(m), dna_bigint2hex(l)));
end;