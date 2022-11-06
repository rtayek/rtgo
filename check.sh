for i in xyz*
	do
	lines=`wc -l $i |  awk '{ print $1 }'`
	#echo $lines
	
	if [ $lines -ne 2 ]
		then echo $i has $lines lines.
		#cat $i
	fi
	done
for i in xyz*
	do
	grep -q @Rule $i
	if [ $? -ne 0 ]
		then echo $i does not have a rule.
	fi
	done	