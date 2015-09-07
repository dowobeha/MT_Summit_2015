
import TranslatedDocuments
import TranslatedDocument
import java.util.Scanner
import scala.collection.mutable.ArrayBuffer
import java.io.File
import edu.illinois.linguistics.postedit.ParallelSentence


class AFRL_WMT_2014_ParallelDocuments(documents : IndexedSeq[TranslatedDocument], val postEditedPath : String) extends TranslatedDocuments(documents) {

  override def save() : Boolean = {
    
    import java.io.BufferedWriter
    import java.io.OutputStreamWriter

    import java.io.FileOutputStream
    import java.io.IOException
    
    var success = true;

    var out : BufferedWriter = null;
    try {
      out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(postEditedPath), "UTF-8"));
      out.write("=================================");
      out.newLine();
      out.write("=================================");
      out.newLine();
      documents.foreach { document => 
        document.foreach { parallelSentence => 
          out.write(parallelSentence.editedTranslation);
          out.newLine();
        }
        out.write("=================================");
        out.newLine();
        out.write("=================================");
        out.newLine();
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

object AFRL_WMT_2014_ParallelDocumentsReader {
  
  def collateTranslatedDocuments(postEditedPath:String, sourcePath:String, targetPath:String, alignmentsPath:String) : TranslatedDocuments = {
    
    val list = new ArrayBuffer[TranslatedDocument] 
    val parallelSentences = new ArrayBuffer[ParallelSentence]

    val postEditScanner = new Scanner(new File(postEditedPath), "UTF-8")
    val sourceScanner = new Scanner(new File(sourcePath), "UTF-8")
    val targetScanner = new Scanner(new File(targetPath), "UTF-8")
    val alignmentsScanner = new Scanner(new File(alignmentsPath), "UTF-8")

    try {
      
      while (postEditScanner.hasNextLine() && sourceScanner.hasNextLine() && targetScanner.hasNextLine() && alignmentsScanner.hasNextLine() ) {
        
        val postEdit = postEditScanner.nextLine()
        val sourceLine = sourceScanner.nextLine()
        val targetLine = targetScanner.nextLine()
        val alignments = alignmentsScanner.nextLine()
        
        val sourceParts = sourceLine.split("\\s+")
        val targetParts = targetLine.split("\\s+")
        val alignmentParts = validateAlignments(alignments.split("\\s+"))
        
        if (valid(sourceParts) && valid(targetParts)) {
          
          parallelSentences.append(new ParallelSentence(postEdit, sourceParts, targetParts, alignmentParts, parallelSentences.length, list.length))
          
        } else {
          if (! parallelSentences.isEmpty) {
            
            list.append(new TranslatedDocument(parallelSentences.toIndexedSeq));
            parallelSentences.clear()
          }
        }
        
      }

    } finally {
      if (postEditScanner != null) {
        postEditScanner.close();
      }
      if (sourceScanner != null) {
        sourceScanner.close();
      }
      if (targetScanner != null) {
        targetScanner.close();
      }
      if (alignmentsScanner != null) {
        alignmentsScanner.close();
      }
    }
      
    return new AFRL_WMT_2014_ParallelDocuments(list.toIndexedSeq,postEditedPath)
    
  }
  
  
  private def valid(line:Array[String]) : Boolean = {
    if (line==null) {
      return false;
    } else if (line.length==0) {
      return false;
    } else if ( line.length==1 && (line(0).equals("=================================") || line(0).equals("")) ) {
      return false;
    } else {
      return true;
    } 
  }

  private def validateAlignments(alignmentParts:Array[String]) : Array[String] = {
    if (valid(alignmentParts)) {
      return alignmentParts
    } else {
      return new Array[String](0)
    }
  }  

}
