create function dna_isleapmonth(ts timestamp)
returns int
specific dna_isleapmonth
language sql
deterministic
begin atomic
	declare y int;
	set y = year(ts);
	return case when month(ts)<>2 then 0 when mod(y,400)=0 then 1 when mod(y,100)=0 then 0 when mod(y,4)=0 then 1 else 0 end;
end