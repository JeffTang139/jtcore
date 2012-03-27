create function dna_yeardiff(st timestamp, ed timestamp)
returns int
specific dna_yeardiff
language sql
deterministic
begin atomic
	return (year(ed)-year(st));
end