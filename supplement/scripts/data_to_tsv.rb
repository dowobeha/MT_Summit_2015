#!/usr/bin/ruby

doc_names=[
	"es-en.1",
	"es-en.2"
]

comment="----"
mt_files={
	"es-en.1"=>"mt/mt.es-en.1",
	"es-en.2"=>"mt/mt.es-en.2",
}

def print(document, line, score, segment, subject)
	puts "#{document}\t#{line}\t#{score}\t#{segment}\t#{subject}"
end



results_files={

	"es-en.1"=>
	{
		"s1"=>"results/subject1/mt.es-en.1.2015-01-23_09-02-38-S1.output.txt",  
		"s2"=>"results/subject2/mt.es-en.1.Subject2.2015-01-23_12-21-01.output.txt",
		"s3"=>"results/subject3/mt.es-en.1.Subject3.2015-01-23_14-20-59.output.txt",
		"s4"=>"results/subject4/mt.es-en.1.subject_4.2015-01-24_09-52-43.output.txt"
	},
	
	"es-en.2"=>
	{
		"s1"=>"results/subject1/mt.es-en.2.2015-01-23_09-22-13-S1.output.txt",
		"s2"=>"results/subject2/mt.es-en.2.subject2.2015-01-23_12-00-39.output.txt",
		"s3"=>"results/subject3/mt.es-en.2.Subject3.2015-01-23_14-37-22.output.txt",
		"s4"=>"results/subject4/mt.es-en.2.Subject_4.2015-01-24_09-39-30.output.txt"
	}

}

doc_names=results_files.keys.sort

results_files.each_pair{ |doc_name,results_file|

	subjects=results_file.keys.sort

	results=Hash.new
	results_file.each_pair{|subject,filename|
		results[subject]=File.readlines(filename)
	}

	mt=Array.new
	source=Array.new

	File.open(mt_files[doc_name]).each_line {|line|
		source_line, character_alignment, word_alignment, mt_line = line.strip.split("\t")
		source.push(source_line.strip)
		mt.push(mt_line.strip)
	}

	0.upto(source.size-1) {|line_number|
		print("", "", comment, "", "")
		print("", "", comment, "", "")
		print("", "", comment, "", "")
		print(doc_name, line_number, comment, source[line_number], "")
		print(doc_name, line_number, comment, mt[line_number], "")
		print("", "", comment, "", "")
		subjects.shuffle.each{|s|
			print(doc_name, line_number, "", results[s][line_number].strip, s)
		}
	}

}

