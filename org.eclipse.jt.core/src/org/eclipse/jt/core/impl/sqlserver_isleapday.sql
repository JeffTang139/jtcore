if object_id('dbo.dna_isleapday','FN') is not null
	drop function dbo.dna_isleapday;

create function dna_isleapday(@ts datetime)
returns bit
begin
	return case when datepart(mm,@ts)=2 and datepart(dd,@ts)=29 then 1 else 0 end;
end;