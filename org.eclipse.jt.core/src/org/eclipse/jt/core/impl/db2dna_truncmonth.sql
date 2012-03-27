create function dna_truncmonth(ts timestamp)
returns timestamp
deterministic
language sql
begin atomic
	return timestamp('1900-01-01 00:00:00') + ((year(ts)-year('1900-1-1'))*12 + month(ts) - month('1900-1-1')) months;
end