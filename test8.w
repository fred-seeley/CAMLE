{COMS22201 test48: program to test extra language features.}

if 1 != 1
  then skip
  else write(0);
if 1 != 2
  then write(1)
  else skip;
if 2 < 3
  then write(2)
  else skip;
if 4 < 4
  then ( write(3); writeln )
  else ( write(4); writeln );
if 5 >= 5
  then ( write(5); writeln; writeln )
  else ( write(6); writeln; writeln );
if 6 >= 7
  then skip
  else write(7);
if 9 >= 8
  then skip
  else write(8);
if 1 > 2
  then skip
  else write(9);
if 3 > 2
  then write(0)
  else skip;
if (1 = 2) | (1 != 2)
  then write('foo')
  else skip;
if (1 = 1) | (2 < 3)
  then write('bar')
  else skip;
if (1 = 2) | (2 > 3)
  then skip
  else write('!!!')
