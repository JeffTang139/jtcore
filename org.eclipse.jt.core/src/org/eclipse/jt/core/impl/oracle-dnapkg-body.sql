create or replace package body dna is

function year(ts timestamp) return number is
  begin
    return (extract(year from ts));
  end;

function quarter(ts timestamp) return number is
  begin
    return (ceil(extract(month from ts)/3));
  end;

function month(ts timestamp) return number is
  begin
    return (extract(month from ts));
  end;

function weekofyear(ts timestamp) return number is
  begin
    return ((trunc(ts, 'd') - trunc(trunc(ts, 'y'), 'd'))/7 + 1);
  end;

function weekofyear_iso(ts timestamp) return number is
  begin
    return ((trunc(ts, 'iw') - trunc(trunc(ts, 'y'), 'iw'))/7 + 1);
  end;

function dayofyear(ts timestamp) return number is
  begin
    return (trunc(ts, 'dd') - trunc(ts, 'y') + 1);
  end;

function dayofmonth(ts timestamp) return number is
  begin
    return (extract(day from ts));
  end;

function dayofweek(ts timestamp) return number is
  begin
    return (trunc(ts, 'dd') - trunc(ts, 'd') + 1);
  end;

function dayofweek_iso(ts timestamp) return number is
  begin
    return (trunc(ts, 'dd') - trunc(ts, 'iw') + 1);
  end;

function hour(ts timestamp) return number is
  begin
    return (extract(hour from ts));
  end;

function minute(ts timestamp) return number is 
  begin
    return (extract(minute from ts));
  end;

function second(ts timestamp) return number is
  begin
    return (extract(second from ts));
  end;

function millisecond(ts timestamp) return number is
  begin
    return to_number(to_char(ts,'ff3'));
  end;
  
function yeardiff(st timestamp, ed timestamp) return number is
  begin
    return months_between(trunc(ed,'y'), trunc(st, 'y'))/12;
  end;

function quarterdiff(st timestamp, ed timestamp) return number is
  begin
    return months_between(trunc(ed,'q'), trunc(st, 'q'))/3;
  end;

function monthdiff(st timestamp, ed timestamp) return number is
  begin
    return months_between(trunc(ed,'mm'), trunc(st, 'mm'));
  end;

function weekdiff(st timestamp, ed timestamp) return number is
  begin
    return trunc((trunc(ed,'d') - trunc(st, 'd'))/7);
  end;

function weekdiff_iso(st timestamp, ed timestamp) return number is
  begin
    return trunc((trunc(ed,'iw') - trunc(st, 'iw'))/7);
  end;

function daydiff(st timestamp, ed timestamp) return number is
  begin
    return trunc(ed,'dd') - trunc(st, 'dd');
  end;

function isleapyear(ts timestamp) return number is
  y number;
  begin
    y:= year(ts);
    if mod(y, 400) = 0 then
      return 1;
    elsif mod(y, 100) = 0 then
      return 0;
    elsif mod(y, 4) = 0 then
      return 1;
    else
      return 0;
    end if;
  end;

function isleapmonth(ts timestamp) return number is
  begin
    if (extract(day from last_day(ts)) = 29) then
      return 1;
    else
      return 0;
    end if;
  end;

function isleapday(ts timestamp) return number is
  begin
    if (month(ts) = 2 and dayofmonth(ts) = 29) then
      return 1;
    else
      return 0;
    end if;
  end;

end dna;