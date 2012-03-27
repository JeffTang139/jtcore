create function dna_isleapyear(ts timestamp)
returns int
specific dna_isleapyear2
language sql
deterministic
begin atomic
	declare y int;
	set y = year(ts);
	return case when mod(y,400)=0 then 1 when mod(y,100)=0 then 0 when mod(y,4)=0 then 1 else 0 end;
end