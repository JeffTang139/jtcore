create function dna_monthdiff(st timestamp, ed timestamp)
returns int
specific dna_monthdiff
language sql
deterministic
begin atomic
	return (year(ed)-year(st))*12 + month(ed) - month(st);
end