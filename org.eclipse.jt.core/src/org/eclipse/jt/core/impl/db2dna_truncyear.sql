create function dna_truncyear(ts timestamp)
returns timestamp
deterministic
language sql
begin atomic
	return timestamp('1900-01-01 00:00:00') + (year(ts)-year('1900-1-1')) years;
end