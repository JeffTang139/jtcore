create function dna_isleapday(ts timestamp)
returns int
specific dna_isleapday
language sql
deterministic
begin atomic
	return case when month(ts)=2 and day(ts)=29 then 1 else 0 end;
end