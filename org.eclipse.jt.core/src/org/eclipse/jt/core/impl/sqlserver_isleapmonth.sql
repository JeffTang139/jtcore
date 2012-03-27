if object_id('dbo.dna_isleapmonth','FN') is not null
	drop function dbo.dna_isleapmonth;

create function dna_isleapmonth(@ts datetime)
returns bit
begin
	declare @y int;
	set @y = datepart(yy, @ts);
	return case when datepart(mm, @ts)<>2 then 0 when @y%400=0 then 1 when @y%100=0 then 0 when @y%4=0 then 1 else 0 end;
end;