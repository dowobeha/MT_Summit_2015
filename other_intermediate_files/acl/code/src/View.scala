
import java.awt.Color
import java.awt.Container
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.Graphics2D
import java.awt.Graphics
import java.awt.RenderingHints
import java.awt.Rectangle
import java.awt.Point
import java.awt.Toolkit
import java.awt.event.KeyEvent
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.KeyStroke
import javax.swing.Scrollable
import javax.swing.ScrollPaneConstants
import javax.swing.ScrollPaneLayout
import java.awt.Font


class EditField(val parallelSentence:ParallelSentence) extends JTextField(parallelSentence.editedTranslation, 0) {
  
  this.setFont(new Font(this.getFont().getName(), Font.PLAIN, 18))
  
  EditFieldController.register(this)
  
}

class SourceWordLabel(val parallelSentence:ParallelSentence, val sourceIndex:Int) extends JLabel {
  
  this.setFont(new Font(this.getFont().getName(), Font.PLAIN, 18))
  
  override def getText() : String = {
    return parallelSentence.sourceWords(sourceIndex)
  }
}

class TargetWordLabel(val parallelSentence:ParallelSentence, val targetIndex:Int) extends JLabel {
  
  this.setFont(new Font(this.getFont().getName(), Font.PLAIN, 18))
//  this.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black))
  
  override def getText() : String = {
    return parallelSentence.targetWords(targetIndex)
  }
}

class SourceSentencePanel(val parallelSentence:ParallelSentence) extends JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5)) {
  
  val words : Seq[SourceWordLabel] = parallelSentence.sourceWordIndices.map({ sourceWordIndex:Int => new SourceWordLabel(parallelSentence,sourceWordIndex)})
  
  words.foreach { word => 
    this.add(word)
    this.add(new JLabel(" "))
  }

  this.setMaximumSize(this.getMinimumSize());
  
  this.setBackground(Color.WHITE)
  
}

class TargetSentencePanel(val parallelSentence:ParallelSentence) extends JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5)) {
  
  val words : Seq[TargetWordLabel] = parallelSentence.targetWordIndices.map({ targetWordIndex:Int => new TargetWordLabel(parallelSentence,targetWordIndex)})
  
  words.foreach { word => 
    this.add(word) 
    this.add(new JLabel(" "))
  }

  this.setMaximumSize(this.getMinimumSize());
  
  this.setBackground(Color.WHITE)
  
}

class ParallelSentencePanel(val parallelSentence:ParallelSentence) extends JPanel {

//  private val sourceWords : Seq[SourceWordLabel] = parallelSentence.sourceWordIndices.map({ sourceWordIndex:Int => new SourceWordLabel(parallelSentence,sourceWordIndex)})
//  private val targetWords : Seq[TargetWordLabel] = parallelSentence.targetWordIndices.map({ targetWordIndex:Int => new TargetWordLabel(parallelSentence,targetWordIndex)})
  private val editableField : EditField = new EditField(parallelSentence)

  private val sourcePanel : SourceSentencePanel = new SourceSentencePanel(parallelSentence)
  private val targetPanel : TargetSentencePanel = new TargetSentencePanel(parallelSentence)
  
  this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS))
  this.add(this.sourcePanel);
  this.add(Box.createRigidArea(new Dimension(0,20)));
  this.add(this.targetPanel);
  this.add(Box.createRigidArea(new Dimension(0,10)));
  this.add(this.editableField);
  this.add(Box.createRigidArea(new Dimension(0,15)));
  this.setMaximumSize(this.getMinimumSize());
  
  override def paint(g:Graphics) {
    super.paint(g);
    g.setColor(Color.BLACK);

    g.asInstanceOf[Graphics2D].setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    parallelSentence.alignmentPoints.foreach { a => 
    
      val source = sourcePanel.words(a.sourceIndex)
      val target = targetPanel.words(a.targetIndex)
      
      val sourceBounds : Rectangle = source.getBounds()
      val targetBounds : Rectangle = target.getBounds()
      
      val sourcePoint : Point = sourcePanel.getLocation()
      val targetPoint : Point = targetPanel.getLocation()
      
      g.drawLine(
    		  sourcePoint.x + sourceBounds.x + sourceBounds.width/2, 
    		  sourcePoint.y + sourceBounds.y + sourceBounds.height, 
    		  targetPoint.x + targetBounds.x + targetBounds.width/2, 
    		  targetPoint.y + targetBounds.y
    		  )
    }
  }
  
}

class ParallelSentencesPanel extends JPanel with Scrollable {

  private val singleSentenceDimension : Dimension = {
    val dummyParallelSentence = new ParallelSentence("foo",  Seq("foo"), Seq("bar"), Seq("0-0"), 0, 0);
    val dummyPanel = new ParallelSentencePanel(dummyParallelSentence)
    dummyPanel.getPreferredSize();
  }
  
  private val maxDimension : Dimension = {
    val dummyFrame = new JFrame();
    dummyFrame.setExtendedState(dummyFrame.getExtendedState() | Frame.MAXIMIZED_BOTH);
    dummyFrame.getMaximumSize()
  }
  
  this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS))

  override def getPreferredScrollableViewportSize() : Dimension = {
    return this.maxDimension;
  }

  override def getScrollableBlockIncrement(arg0:Rectangle, arg1:Int, arg2:Int) : Int = {
    return this.singleSentenceDimension.height * 5;
  }

  override def getScrollableTracksViewportHeight() : Boolean = {
    return false;
  }

  override def getScrollableTracksViewportWidth() : Boolean = {
    return false;
  }

  override def getScrollableUnitIncrement(arg0:Rectangle, arg1:Int, arg2:Int) : Int = {
    return this.singleSentenceDimension.height;
  }

}

class ParallelSentencesScrollPane(contentPanel:ParallelSentencesPanel) 
    extends JScrollPane(contentPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED) {
  
  this.setLayout(new ParallelSentencesScrollPaneLayout())
	this.getViewport().setBackground(contentPanel.getBackground());
	this.setBackground(contentPanel.getBackground());
  
}

class ParallelSentencesScrollPaneLayout extends ScrollPaneLayout {
  
  override def layoutContainer(parent:Container) {
    super.layoutContainer(parent)
    
    val view = viewport.getView()
    
    if (view != null) {
      val viewPortSize = viewport.getSize()
      val viewSize = view.getSize()
      
      if ((viewPortSize.width > viewSize.width) || (viewPortSize.height > viewSize.height)) {
        var spaceX = (viewPortSize.width - viewSize.width) / 2
        
        if (spaceX < 0) spaceX = 0
       
        viewport.setLocation(spaceX, 1);
        viewport.setSize(viewPortSize.width - spaceX, viewPortSize.height);
      }
    }
  }
  
}


class ParallelSentencesFrame(title:String) extends JFrame(title) {
  
  val parallelSentencesPanel = new ParallelSentencesPanel()
  
  this.setContentPane(new ParallelSentencesScrollPane(parallelSentencesPanel));
  this.pack();
  this.setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);
  this.pack()
}


sealed class FileMenuItem(title:String, shortcut:Option[KeyStroke]=None) extends JMenuItem(title) {
  
  // Set the key shortcut accelerator, if there is one
  shortcut match {
    case Some(key) => { this.setAccelerator(key) }
    case None      => { /* This space intentionally left blank */ }
  }
  
  def this(title:String, keyEventCode:Int) = {
    this(title,Some(KeyStroke.getKeyStroke(keyEventCode, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())))
  }
}

object FileMenu extends JMenu("File") {
  
  case object NextDocument     extends FileMenuItem("Next Document",     KeyEvent.VK_N)
  case object PreviousDocument extends FileMenuItem("Previous Document", KeyEvent.VK_P)
  case object SaveDocuments    extends FileMenuItem("Save",              KeyEvent.VK_S)
  case object GotoDocument     extends FileMenuItem("Goto Document...",  KeyEvent.VK_G)
  
  this.add(NextDocument)
  this.add(PreviousDocument)
  this.add(SaveDocuments)
  this.add(GotoDocument)
  
}

object MenuBar extends JMenuBar {
  this.add(FileMenu)
}

