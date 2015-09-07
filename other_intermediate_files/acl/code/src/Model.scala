
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SortedSet
import scala.collection.mutable.TreeSet

abstract class TranslatedDocuments(val documents : IndexedSeq[TranslatedDocument]) extends IndexedSeq[TranslatedDocument] {

  def length() : Int = {
    return documents.length
  }
  
  def apply(index:Int) : TranslatedDocument = {
    return documents(index)
  }

  override def iterator() : Iterator[TranslatedDocument] = {
    return documents.iterator
  }

  def save() : Boolean
  
}

class TabDelineatedTranslatedDocuments(documents : IndexedSeq[TranslatedDocument], val postEditedPath : String) extends TranslatedDocuments(documents) {
  override def save() : Boolean = {
    
    import java.io.BufferedWriter
    import java.io.OutputStreamWriter

    import java.io.FileOutputStream
    import java.io.IOException
    
    var success = true;

    var out : BufferedWriter = null;
    try {
      out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(postEditedPath), "UTF-8"));
//      out.write("=================================");
//      out.newLine();
//      out.write("=================================");
//      out.newLine();
      documents.foreach { document => 
        document.foreach { parallelSentence => 
          out.write(parallelSentence.editedTranslation);
          out.newLine();
        }
//        out.write("=================================");
        out.newLine();
//        out.write("=================================");
//        out.newLine();
      }
    } catch {
      case e: IOException => {
        success = false;
        e.printStackTrace();
      }
    } finally {
      if (out!=null) {
        try {
          out.close();
        } catch {
          case e: IOException => {
            success = false;
            e.printStackTrace();
          }
        }
      }
    }
    
    return success;
  }  
}

object TabDelineatedTranslatedDocuments {
  
  
  def get(dataPath:String, resultsPath:String, showAlignments:Boolean, showSource:Boolean, showMT:Boolean) : TabDelineatedTranslatedDocuments = {
    
    import java.util.Scanner
    import java.io.File
    
    val list = new ArrayBuffer[TranslatedDocument] 
    val parallelSentences = new ArrayBuffer[ParallelSentence]
    
    val scanner = new Scanner(new File(dataPath), "UTF-8")
    
    while (scanner.hasNextLine()) {
      
      val line:String = scanner.nextLine();
//      println(line)
      if (line.contains("\t")) {
        try {
        val parts = line.split("\t")
        val source : String = parts(0)
        val characterAlignments : String = parts(1)
        val wordAlignments : String = parts(2)
        val target : String = parts(3)
        
        val characterAlignmentPoints : Seq[String] = characterAlignments.split("\\s+")
        val splitData = new Split(source, target, characterAlignments)
        val sourceTokens = if (showSource) splitData.getSourceTokens() else Seq()
        val targetTokens = if (showMT) splitData.getTargetTokens() else Seq()
        val wordAlignmentPoints : Seq[String] = if (showAlignments) splitData.getWordAlignments() else Seq()
//        val sourceIndexMap = splitData.getSourceIndexMap()
//        val targetIndexMap = splitData.getTargetIndexMap()
        
        parallelSentences.append(new ParallelSentence(target, sourceTokens, targetTokens, wordAlignmentPoints, parallelSentences.length, list.length))
        
//        val targetTokens : Seq[String] = split(target, characterAlignmentPoints)
        
//        val sourceWords : Seq[String] = source.split("\\s+")
//        val wordAlignmentPoints : Seq[String] = if (showAlignments) wordAlignments.split("\\s+") else Seq()
//        val targetWords : Seq[String] = target.split("\\s+")
        
//        parallelSentences.append(new ParallelSentence(target, sourceWords, targetWords, wordAlignmentPoints, parallelSentences.length, list.length))
        } catch {
          case e:RuntimeException => {
            // This space intentionally left blank
          }
        }
      } else {
          if (! parallelSentences.isEmpty) {
            
            list.append(new TranslatedDocument(parallelSentences.toIndexedSeq));
            parallelSentences.clear()
          }
        }
    }
    
    if (! parallelSentences.isEmpty) {
    	list.append(new TranslatedDocument(parallelSentences.toIndexedSeq));
    	parallelSentences.clear()
    }

    
    return new TabDelineatedTranslatedDocuments(list, resultsPath)
  }
}

class TranslatedDocument(val parallelSentences : IndexedSeq[ParallelSentence]) extends IndexedSeq[ParallelSentence] {

  def length() : Int = {
    return parallelSentences.length
  }
  
  def apply(index:Int) : ParallelSentence = {
    return parallelSentences(index)
  }

  override def iterator() : Iterator[ParallelSentence] = {
    return parallelSentences.iterator
  }

}
 

/*
trait ParallelDocument extends IndexedSeq[ParallelSentence] {
  // This space intentionally left blank
}
*/

class ParallelSentence(var editedTranslation:String, val sourceWords:Seq[String], val targetWords:Seq[String], alignments:Seq[String], val sentenceNumber:Int, val documentNumber:Int) {
  
  val alignmentPoints:Seq[WordAlignment] = WordAlignment.convert(alignments)

  def sourceWordIndices : Seq[Int] = {
    return (0 until sourceWords.length)
  }

  def targetWordIndices : Seq[Int] = {
    return (0 until targetWords.length)
  }
    
}


/**
 * @author Lane Schwartz
 */
class WordAlignment(val sourceIndex : Int, val targetIndex : Int) {
	// This space intentionally left blank
}




object WordAlignment {
  
  class FormatException(message:String) extends RuntimeException(message) {
    // This space intentionally left blank
  }
  
  def convert(pointsAsStrings:Seq[String]) : Seq[WordAlignment] = {
    val size = pointsAsStrings.length
    
    val alignmentPoints = new ArrayBuffer[WordAlignment](size)
    
    for (i <- 0 until size) {
      val parts = pointsAsStrings(i).split("-")
      if (parts.length == 2) {
        val sourceIndex = parts(0).toInt
        val targetIndex = parts(1).toInt
        alignmentPoints.append(new WordAlignment(sourceIndex, targetIndex))
      } else {
        System.err.println(s"Warning:\tIll-formed word alignment string:\t${pointsAsStrings(i)}")
        //throw new WordAlignment.FormatException(s"Ill-formed word alignment string:\t${pointsAsStrings(i)}")
      }
    }
    
    return alignmentPoints
  }
}

