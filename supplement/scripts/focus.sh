#!/bin/bash

for s in 1 2 3 4; do

	for doc in es-en.1 es-en.2; do

		#echo "./scripts/focus.rb ${doc} s${s} < results/subject${s}/mt.${doc}*.log.txt"
		./scripts/focus.rb ${doc} s${s} < results/subject${s}/mt.${doc}*.log.txt

	done

done
