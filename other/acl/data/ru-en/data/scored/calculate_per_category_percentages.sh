#!/bin/bash

codes="en s0-en s1-en s2-en s3-en s4-en s5-en s6-en"
docs="doc110 doc130"

for code in ${codes}; do

	if [[ "${code}" == "en" ]]; then
		user="moses"	
	else
		user="${code}"
	fi
	
	for doc in ${docs}; do
		./calculate_per_category_percentages.rb < ${doc}.${user}.scores > ${doc}.${user}.percentages
	done

done


for doc in ${docs}; do
	paste ${doc}.moses.percentages ${doc}.s*-en.percentages > ${doc}.percentages.csv
done
