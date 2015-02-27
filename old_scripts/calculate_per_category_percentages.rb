#!/usr/bin/ruby

possible_values=[2,4,6,8,10,12]
counts=Hash.new{|k,v| k[v]=0.0}
data_points=0

STDIN.each_line{ |line|

	if line=~/[[:alpha:]]/
		puts line
	else
		value=line.strip.to_i
		counts[value] += 1
		data_points   += 1
		
	end
}

possible_values.each{ |value|

	decimal_value = counts[value]/data_points
	percentage = (decimal_value*100).round(0)
	puts "#{percentage}\\%"

}