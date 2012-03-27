create function dna_quarterdiff(st timestamp, ed timestamp)
returns int
specific dna_quarterdiff
language sql
deterministic
begin atomic
	return (year(ed)-year(st))*4 + quarter(ed) - quarter(st);
end