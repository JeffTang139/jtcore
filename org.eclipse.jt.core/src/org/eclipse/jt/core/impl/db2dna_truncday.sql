create function dna_truncday(ts timestamp)
returns timestamp
specific dna_truncday
language sql
deterministic
begin atomic
	return timestamp('1900-01-01 00:00:00') + ((days(ts) - days('1900-1-1'))) days;
end