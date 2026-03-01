#find -name "*.java" -exec grep -H -R -nE '[a-zA-Z0-9] (save|restore).*\(*\).*{'  "{}" ";"
# find -name "*.java" -exec grep -H -nE '[a-zA-Z0-9] .*\.+(save(?Model!Mnode)|restore(?Model!Mnode)).*\('  "{}" ";"
#echo ----------------
f#ind -name "*.java" -exec grep -H -R -nE '[a-zA-Z0-9] save(Model|MNode).*\(*\).*{'  "{}" ";"
e#cho ----------------
#f#nd -name "*.java" -exec grep -H -R -nE '[a-zA-Z0-9] restore(Model|MNode).*\(*\).*{'  "{}" ";"

grep -R -nE --include='*.java' '[a-zA-Z0-9] (save|restore).*\(.*\).*\{' src tst

