#!/bin/bash

codes="en s0-en s1-en s2-en s3-en s4-en s5-en s6-en"
docs="doc110 doc130"

for code in ${codes}; do

	if [[ "${code}" == "en" ]]; then
		user="moses"	
	else
		user="${code}"
	fi
	
	
	grep "\t${code}$" final-results.csv > "final-results.${user}.csv"

	for doc in ${docs}; do
		echo "${user}" > ${doc}.${user}.scores
		grep "\t${code}$" final-results.csv | grep "^${doc}" | cut -f 3 >> ${doc}.${user}.scores
	done

done


for doc in ${docs}; do
	paste ${doc}.moses.scores ${doc}.s*-en.scores > ${doc}.scores.csv
done
