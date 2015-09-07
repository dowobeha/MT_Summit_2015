#!/usr/bin/ruby

times=Hash.new{|k,v| k[v]=Array.new}

last_seen_time=0
last_seen_sentence_number=0

STDIN.each_line {|line|
	
	unless (line.start_with?("#"))

		parts=line.split("\t")
		#puts parts[4]
		
		timestamp=parts[0].strip.to_i
		last_seen_time=timestamp
		event_type=parts[1].strip
		sentence_number=parts[4].strip.to_i
	
		if (event_type=="FOCUS_GAINED")
			times[sentence_number].push(timestamp)
			last_seen_sentence_number=sentence_number
		elsif (event_type=="FOCUS_LOST")
			start=times[sentence_number].pop
			times[sentence_number].push(timestamp-start)
		end	
	
	end
}

			start=times[last_seen_sentence_number].pop
			times[last_seen_sentence_number].push(last_seen_time-start)


def alignments_were_present(doc,who)

	if (who=="mt")
		return ""
	end

	if (doc=="es-en.1")
		if (who=="s1" || who=="s3")
			return false
		elsif (who=="s2" || who=="s4")
			return true
		else
			raise "ERROR:\t#{doc}\t\#{who}"
		end
	elsif (doc=="es-en.2")
		if (who=="s1" || who=="s3")
			return true
		elsif (who=="s2" || who=="s4")
			return false
		else
			raise "ERROR:\t#{doc}\t\#{who}"
		end	
	else
		raise "ERROR:\t#{doc}\t\#{who}"
	end

end

times.keys.sort.each{|sentence_number|
	sum=times[sentence_number].inject(0, :+)
	a=alignments_were_present(ARGV[0],ARGV[1])
	puts "#{ARGV[0]}\t#{sentence_number}\t#{sum}\t#{a}"
	#	puts "#{sentence_number}\t#{sum} milliseconds\t#{sum/1000} seconds\t#{sum/1000/60} minutes"
}
