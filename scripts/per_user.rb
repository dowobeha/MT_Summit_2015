#!/usr/bin/ruby

def print(doc,sentence_number,who,score)
	align=alignments_were_present(doc,who)
	puts "#{doc}\t#{sentence_number}\t#{who}\t#{score}\t#{align}"
end


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


bins=Hash.new{|k,v| k[v]=Array.new}


STDIN.each_line{|line|

	doc, sentence_number, unused, sentence, score, who = line.gsub('\n','').split("\t")
	who=who.strip
	if (who.strip != "")
		#puts "who='#{who}'\t#{line}"
		score=score.strip.to_i
		
#		if (score==12)
#			new_score=10
#		elsif (score==10)
#			new_score=9
#		else
#			new_score=score
#		end
		
		print(doc.to_s.strip,sentence_number.to_s.strip,who.to_s.strip,score.to_s.strip)
	end


}
