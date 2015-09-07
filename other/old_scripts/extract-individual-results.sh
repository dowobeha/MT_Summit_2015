#!/bin/bash

codes="mt s1 s2 s3 s4"
docs="es-en.1 es-en.2"

for code in ${codes}; do
	analysis/scored.tsv
	grep "\t${code}$" analysis/scored.tsv > "analysis_per_user/scored.${user}.tsv"

	for doc in ${docs}; do
		echo "${user}" > ${doc}.${user}.scores
		grep "\t${code}$" final-results.csv | grep "^${doc}" | cut -f 3 >> ${doc}.${user}.scores
	done

done


for doc in ${docs}; do
	paste ${doc}.moses.scores ${doc}.s*-en.scores > ${doc}.scores.csv
done
