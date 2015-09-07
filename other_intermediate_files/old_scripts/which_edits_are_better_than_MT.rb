#!/usr/bin/ruby

line_number=0
STDIN.each_line{ |line|

	if line=~/[[:alpha:]]/
		puts line.strip.split.join(" & ")+' \\\\ \hline'
	else
		parts=line.strip.split
		final_parts=Array.new(parts.size)
		
		parts.each_with_index { |part,index|
		
			if part.to_i > parts[0].to_i
				final_parts[index] = "\\textbf{#{part}}"
			elsif part.to_i < parts[0].to_i
				final_parts[index] = "\\textcolor{red}{\\textit{#{part}}}"
			else
				final_parts[index] = part
			end
		
		}
		
		puts "B#{line_number} & " + final_parts.join(" & ")+' \\\\ \hline'
		line_number += 1
	end

	
}