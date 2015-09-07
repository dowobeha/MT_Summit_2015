#!/usr/bin/ruby


class DataPoint

	attr_reader :doc, :sentence_number, :who, :score, :alignment

	def initialize(line)
	
		parts = line.gsub('\n','').split("\t")
	
		@doc=parts[0].strip
		@sentence_number=parts[1].strip.to_i
		@who=parts[2].strip
		@score=parts[3].strip.to_i
		@alignment=parts[4].strip
		if @alignment=="true"
			@alignment=true
		elsif @alignment=="false"
			@alignment=false
		end
	
	end

	def is_mt()
		return @who=="mt"
	end

	def sentence_id()
		return "#{@doc}_#{@sentence_number}"
	end

	def to_s()
		return "#{@doc}\t#{@sentence_number}\t#{@who}\t#{@score}\t#{@alignment}"
	end

	def <=> other
    	return self.sentence_id() <=> other.sentence_id()
 	end
 	
 	def to_f
 		return self.score
 	end

end

class SentenceData

	attr_reader :mt, :data

	def initialize()
		@data=Array.new
		@mt=nil
	end

	def add(dataPoint)
		if (dataPoint.is_mt())
			@mt=dataPoint
		else
			@data.push(dataPoint)
		end
	end

	def mt_score()
		return @mt.score
	end

 	def <=> other
    	self.mt <=> other.mt
  	end

	def to_s()
		return "#{self.mt.to_s} with #{self.data.size} points"
	end

end

def mean(array)
	
	sum=0.0
	
	array.each{|dataPoint|
		#sum += dataPoint.score
		sum += dataPoint.to_f
	}
	
	return sum/array.size
	
end

data_per_sentence=Hash.new{|k,v| k[v]=SentenceData.new}

File.open("analysis/scored_breakdown.updated.tsv", "r") { |file|
  file.each_line{ |line|

	d=DataPoint.new(line)
	
	data_per_sentence[d.sentence_id()].add(d)

  }
}

just_mt=Array.new
data_per_mt_score=Hash.new{|k,v| k[v]=Array.new}

data_per_sentence.values.sort.each{|s|
	data_per_mt_score[s.mt_score].push(s)
	just_mt.push(s.mt)
}



with_alignments = Hash.new{|k,v| k[v]=Array.new}
no_alignments   = Hash.new{|k,v| k[v]=Array.new}

data_per_mt_score.keys.sort.each{|mt_score|
	
	#puts "score==#{mt_score}"
	
	data_per_mt_score[mt_score].each{|sentence|
		sentence.data.each{|dataPoint|
			if (dataPoint.alignment)
				with_alignments[mt_score].push(dataPoint)
				#puts "#{dataPoint.score}"
			else
				no_alignments[mt_score].push(dataPoint)
			end
		}
	}
}

print '\addplot coordinates {'
data_per_mt_score.keys.sort.each{|mt_score|
	print "(#{mt_score},#{mean(with_alignments[mt_score])}) "
}
puts '};'

print '\addplot coordinates {'
data_per_mt_score.keys.sort.each{|mt_score|
	print "(#{mt_score},#{mean(no_alignments[mt_score])}) "
}
puts '};'

print '\addplot[red,sharp plot,update limits=false] coordinates { '
print "(-1,#{mean(just_mt)}) (12,#{mean(just_mt)}) "
puts '};'

puts
puts
puts


difference_with_alignments = Hash.new{|k,v| k[v]=Array.new}
difference_no_alignments   = Hash.new{|k,v| k[v]=Array.new}

data_per_mt_score.keys.sort.each{|mt_score|

	data_per_mt_score[mt_score].each{|sentence|
		sentence.data.each{|dataPoint|
			if (dataPoint.alignment)
				difference_with_alignments[mt_score].push(dataPoint.score-mt_score)
			else
				difference_no_alignments[mt_score].push(dataPoint.score-mt_score)
			end
		}
	}
}

print '\addplot coordinates {'
data_per_mt_score.keys.sort.each{|mt_score|
	print "(#{mt_score},#{mean(difference_with_alignments[mt_score])}) "
}
puts '};'

print '\addplot coordinates {'
data_per_mt_score.keys.sort.each{|mt_score|
	print "(#{mt_score},#{mean(difference_no_alignments[mt_score])}) "
}
puts '};'


puts
puts
puts


per_participant_with_alignments = Hash.new{|k,v| k[v]=Array.new}
per_participant_no_alignments   = Hash.new{|k,v| k[v]=Array.new}

data_per_mt_score.keys.sort.each{|mt_score|

	data_per_mt_score[mt_score].each{|sentence|
		sentence.data.each{|dataPoint|
			if (dataPoint.alignment)
				per_participant_with_alignments[dataPoint.who].push(dataPoint.score)
			else
				per_participant_no_alignments[dataPoint.who].push(dataPoint.score)
			end
		}
	}
}


print '\addplot coordinates {'
per_participant_with_alignments.keys.sort.each{|who|
	print "(#{who.sub('s','')},#{mean(per_participant_with_alignments[who])}) "
}
puts '};'

print '\addplot coordinates {'
per_participant_no_alignments.keys.sort.each{|who|
	print "(#{who.sub('s','')},#{mean(per_participant_no_alignments[who])}) "
}
puts '};'

print '\addplot[red,sharp plot,update limits=false] coordinates { '
print "(-1,#{mean(just_mt)}) (12,#{mean(just_mt)}) "
puts '};'

puts
puts
puts


with_alignments_category_counts = Hash.new{|k,v| k[v]=0}
no_alignments_category_counts   = Hash.new{|k,v| k[v]=0}
mt_category_counts   = Hash.new{|k,v| k[v]=0}

data_per_mt_score.keys.sort.each{|mt_score|

	data_per_mt_score[mt_score].each{|sentence|
	
		mt_category_counts[sentence.mt.score] += 1
	
		sentence.data.each{|dataPoint|
			if (dataPoint.alignment)
				with_alignments_category_counts[dataPoint.score] += 1
			else
				no_alignments_category_counts[dataPoint.score] += 1
			end
		}
	}
}

print '\addplot coordinates {'
with_alignments_category_counts.keys.sort.each{|score|
	print "(#{score},#{(with_alignments_category_counts[score].to_f/with_alignments_category_counts.values.inject(0,:+))*100}) "
}
puts '};'

print '\addplot coordinates {'
no_alignments_category_counts.keys.sort.each{|score|
	print "(#{score},#{(no_alignments_category_counts[score].to_f/no_alignments_category_counts.values.inject(0,:+))*100}) "
}
puts '};'

print '\addplot coordinates {'
mt_category_counts.keys.sort.each{|score|
	print "(#{score},#{(mt_category_counts[score].to_f/mt_category_counts.values.inject(0,:+))*100}) "
}
puts '};'

puts
puts
puts
