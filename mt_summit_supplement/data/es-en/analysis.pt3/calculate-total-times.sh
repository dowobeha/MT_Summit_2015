#!/bin/bash

subjects=$(cd results; ls -1)

echo -e "Subject\tDocument\tTime (milliseconds)\tAlignments shown"

for subject in ${subjects}; do

	for doc in 1 2; do
	
		time=$(tail -n 1 results/${subject}/mt.es-en.${doc}.*.log.txt | cut -f 1)
		alignments=$(grep "Showing alignments" results/${subject}/mt.es-en.${doc}.*.log.txt | sed 's,.*:\s,,')
		
		
		echo -e "${subject}\t${doc}\t${time}\t${alignments}"
	
	done

done
