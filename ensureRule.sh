# make sure all test cases and suites have a test watcher rule.
find -name "*.java" -exec grep -l 'class .*Test[CS]' "{}" ";" > testcases.txt
wc -l testcases.txt
xargs grep -HA1 'class .*Test[CS][au]' < testcases.txt > twos.txt #| tr '\n' '|' > removed.txt
wc -l twos.txt
rm xyz*
csplit  --suppress-matched -f xyz twos.txt /--/ {*}
grep -l /sgf/ xyz* -exec rm "{}" ";"
rm `grep -l /sgf/ xyz*`
wc xyz*

