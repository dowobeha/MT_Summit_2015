#!/usr/bin/ruby

data=Array.new
keys=nil

STDIN.each_line {|line|

	if line=~/[[:alpha:]]/
		keys=line.split
	else
		data.push(line.split)
	end

}

sums=Array.new(keys.size) { 0.0 }
means=Array.new(keys.size) { 0.0 }

sums_of_differences=Array.new(keys.size) { 0.0 }
means_of_differences=Array.new(keys.size) { 0.0 }

data.each{ |row|

	row.each_with_index{|value,index|
		sums[index] += value.to_i
		sums_of_differences[index] += (value.to_i - row[0].to_i)
	}

}


sums.each_with_index{ |sum,index|
	means[index] = sum/data.size
}

sums_of_differences.each_with_index{ |sum,index|
	means_of_differences[index] = sum/data.size
}


puts keys.join(" & ")
puts means.join(" & ")
puts
puts
puts keys.join(" & ")
puts means_of_differences.join(" & ")
