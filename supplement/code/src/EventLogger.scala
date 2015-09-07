// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

import java.awt.Component
import java.awt.Container
import java.awt.event.ComponentEvent
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.UnsupportedEncodingException
import java.io.Writer
import java.util.zip.GZIPOutputStream
import java.util.zip.ZipOutputStream


class EventLogFormatter() {
  
  private var startTime = -1L;
  
  protected def elapsedMilliseconds() : Long = {
    
    if (startTime < 0) {
      startTime = System.currentTimeMillis();
    }

    val now = System.currentTimeMillis();
    val elapsed = now - startTime;
    return elapsed
    
  }
  
  def shouldLog(e:ComponentEvent) : Boolean = {
    e.getComponent() match {
      case _:EditField                => return true
      case _:ParallelSentencePanel    => return true
      case _:SourceWordLabel          => return true
      case _:TargetWordLabel          => return true
      case _                          => return false
    }
  }
  
  def eventType(e:ComponentEvent) : String = {
    e.getID match {
      case MouseEvent.MOUSE_PRESSED  => return "MOUSE_PRESSED"
      case MouseEvent.MOUSE_RELEASED => return "MOUSE_RELEASED" 
      case MouseEvent.MOUSE_CLICKED  => return "MOUSE_CLICKED"
      case MouseEvent.MOUSE_ENTERED  => return "MOUSE_ENTERED"
      case MouseEvent.MOUSE_EXITED   => return "MOUSE_EXITED"
      case MouseEvent.MOUSE_MOVED    => return "MOUSE_MOVED"
      case MouseEvent.MOUSE_DRAGGED  => return "MOUSE_DRAGGED"
      case MouseEvent.MOUSE_WHEEL    => return "MOUSE_WHEEL"
      case KeyEvent.KEY_PRESSED      => return "KEY_PRESSED"
      case KeyEvent.KEY_RELEASED     => return "KEY_RELEASED"
      case KeyEvent.KEY_TYPED        => return "KEY_TYPED"    
      case FocusEvent.FOCUS_GAINED   => return "FOCUS_GAINED"
      case FocusEvent.FOCUS_LOST     => return "FOCUS_LOST"
      case _                         => return "UNKNOWN"
    }
  }
  
  def eventDetails(e:ComponentEvent) : String = {
    e match {
      case keyEvent:KeyEvent => {
        e.getID match {
          case KeyEvent.KEY_PRESSED   => return KeyEvent.getKeyText(keyEvent.getKeyCode())
          case KeyEvent.KEY_RELEASED  => return KeyEvent.getKeyText(keyEvent.getKeyCode())
          case KeyEvent.KEY_TYPED     => return keyEvent.getKeyChar().toString()
          case _                      => return ""
        }
      }
      case _                          => return ""
    }
  }

  def documentNumber(e:ComponentEvent) : String = {
    e.getComponent() match {
      case field:EditField            => return field.parallelSentence.documentNumber.toString()
      case panel:ParallelSentencePanel=> return panel.parallelSentence.documentNumber.toString()
      case label:SourceWordLabel      => return label.parallelSentence.documentNumber.toString()
      case label:TargetWordLabel      => return label.parallelSentence.documentNumber.toString()
      case _                          => return ""
    }
  }
  
  
  def sentenceNumber(e:ComponentEvent) : String = {
    e.getComponent() match {
      case field:EditField            => return field.parallelSentence.sentenceNumber.toString()
      case panel:ParallelSentencePanel=> return panel.parallelSentence.sentenceNumber.toString()
      case label:SourceWordLabel      => return label.parallelSentence.sentenceNumber.toString()
      case label:TargetWordLabel      => return label.parallelSentence.sentenceNumber.toString()
      case _                          => return ""
    }
  }
  
  def userInterfaceItemType(e:ComponentEvent) : String = {
    e.getComponent() match {
      case _:EditField                => return "Field"
      case _:ParallelSentencePanel    => return "Panel"
      case _:SourceWordLabel          => return "Source"
      case _:TargetWordLabel          => return "Target"
      case _                          => return ""
    }
  }
  
  def wordNumber(e:ComponentEvent) : String = {
    e.getComponent() match {
      case label:SourceWordLabel      => return label.sourceIndex.toString()
      case label:TargetWordLabel      => return label.targetIndex.toString()
      case _                          => return ""
    }
  }
  
  def uiTextValue(e:ComponentEvent) : String = {
    e.getComponent() match {
      case field:EditField            => return field.getText()
      case label:SourceWordLabel      => return label.getText()
      case label:TargetWordLabel      => return label.getText()
      case _                          => return ""
    }
  }

  def cursorPositionWithinEditField(e:ComponentEvent) : String = {
    e.getComponent() match {
      case field:EditField            => {
        if (field.getSelectedText()==null) {
          return field.getCaretPosition().toString()
        } else {
          return s"${field.getSelectionStart()}-${field.getSelectionEnd()}"
        }
      }
      case _                          => return ""
    }
  } 
 
  def log(e:ComponentEvent) : String = { 

      val str = new StringBuilder();
    
      str.append(elapsedMilliseconds())
      str.append('\t')
      str.append(eventType(e))
      str.append('\t')
      str.append(eventDetails(e))
      str.append('\t')
      str.append(documentNumber(e))
      str.append('\t')
      str.append(sentenceNumber(e))
      str.append('\t')
      str.append(userInterfaceItemType(e))
      str.append('\t')
      str.append(wordNumber(e))
      str.append('\t')
      str.append(cursorPositionWithinEditField(e))
      str.append('\t')
      str.append(uiTextValue(e))
      str.append('\t')
      str.append(e.paramString().replaceFirst("\t.*", ""))
      str.append('\n')

      return str.toString()

  }
  
}



class EventLogger(fileName:String, val formatter:EventLogFormatter) extends MouseListener with MouseMotionListener with MouseWheelListener with KeyListener with FocusListener {

  def this(fileName:String) {
    this(fileName,new EventLogFormatter())
  }
  
  def applyListeners(component:Component) {
    component.addMouseListener(this)
    component.addMouseMotionListener(this)
    component.addMouseWheelListener(this)
    component.addKeyListener(this)
    component.addFocusListener(this)
    
  }
  
  def applyListener(container:Container) {
    applyListeners(container)
    container.getComponents.foreach { child => 
      child match {
        case childContainer:Container => applyListener(childContainer)
        case childComponent:Component => applyListeners(childComponent)
      }  
    }
  }
  
  private val writer:Writer = {
    
		  val output = new FileOutputStream(fileName);
      
		  if (fileName.endsWith(".gz")) {
        new OutputStreamWriter(new GZIPOutputStream(output), "UTF-8");
      } else if (fileName.endsWith(".zip")) {
        new OutputStreamWriter(new ZipOutputStream(output), "UTF-8");
      } else {
        new OutputStreamWriter(output, "UTF-8");
      }

  }
  
  Runtime.getRuntime().addShutdownHook(new Thread() {
        override def run() {
          try {
            writer.close()
          } catch {
            case e:IOException => e.printStackTrace()
          }
        }
      });
  
  def log(message:String) {
    try {
    	writer.append(message);
    	writer.flush()
//    	System.err.print(message)
    } catch {
    case exception:IOException => {
    	  exception.printStackTrace()
    	  System.err.println(message)
      }
    }
  }
  
  def log(e:ComponentEvent) {
	  if (formatter.shouldLog(e)) {
		  val message = formatter.log(e)
      log(message)
    }
  }

	override def mouseDragged(e:MouseEvent) {
		log(e);
	}

	override def mouseMoved(e:MouseEvent) {
		log(e);   
	}

	override def mouseClicked(e:MouseEvent) {
		log(e);   
	}

	override def mousePressed(e:MouseEvent) {
		log(e); 
	}

	override def mouseReleased(e:MouseEvent) {
		log(e); 
	}

	override def mouseEntered(e:MouseEvent) {
		log(e);   
	}

	override def mouseExited(e:MouseEvent) {
		log(e);   
	}

	override def mouseWheelMoved(e:MouseWheelEvent) {
		log(e);
	}

	override def keyTyped(e:KeyEvent) {
		log(e);
	}

	override def keyPressed(e:KeyEvent) {
		log(e);
	}

	override def keyReleased(e:KeyEvent) {
		log(e);
	}

	override def focusGained(e:FocusEvent) {
		log(e);   
	}

	override def focusLost(e:FocusEvent) {
		log(e);
	}

}
