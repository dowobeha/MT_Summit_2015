import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import EventLogger;
import PostEditorControllerWithMenu;
import TabDelineatedTranslatedDocuments;
import TranslatedDocuments;


public class PostEditor implements Runnable {
	
	@SuppressWarnings("serial")
	private class FileChooser extends JPanel implements ActionListener, DocumentListener {
	    private final JButton dataButton, continueButton;
	    private final JLabel resultsLabel, logLabel, subjectLabel;
	    public final JCheckBox alignmentsButton, sourceButton, mtButton, timestampButton;
	    public final JTextArea dataArea, resultsArea, logArea;
	    private final JTextField subjectArea;
	    private final JFileChooser fc;
	    private final String timestamp;
//	    private String resultsPath, logPath;
	    
	    private final Preferences prefs;
	    
	    FileChooser() {
	        super(new GridLayout(0,2));
	        
            Calendar now = Calendar.getInstance();
            int year   = now.get(Calendar.YEAR);
            int month  = now.get(Calendar.MONTH);
            int day    = now.get(Calendar.DAY_OF_MONTH);
            int hour   = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);
            int second = now.get(Calendar.SECOND);
            this.timestamp = String.format("%04d-%02d-%02d_%02d-%02d-%02d", year, month, day, hour, minute, second);

	        
	        prefs = Preferences.userNodeForPackage(PostEditor.class);

	        dataArea = new JTextArea(1,50);
	        dataArea.setMargin(new Insets(5,5,5,5));
	        dataArea.setEditable(false);
	        
	        resultsArea = new JTextArea(1,50);
	        resultsArea.setMargin(new Insets(5,5,5,5));
	        resultsArea.setEditable(false);
	        
	        logArea = new JTextArea(1,50);
	        logArea.setMargin(new Insets(5,5,5,5));
	        logArea.setEditable(false);

	        subjectArea = new JTextField(50);
	        subjectArea.setMargin(new Insets(5,5,5,5));
	        subjectArea.setEditable(true);
	        subjectArea.getDocument().addDocumentListener(this);
	        
	        JPanel checkboxPanel = new JPanel();
	        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
	        
	        alignmentsButton = new JCheckBox("Display alignments", true);
	        sourceButton     = new JCheckBox("Display source", true);
	        mtButton         = new JCheckBox("Display machine translation", true);
	        timestampButton  = new JCheckBox("Include timestamp in file names", true);
	        alignmentsButton.addActionListener(this);
	        sourceButton.addActionListener(this);
	        mtButton.addActionListener(this);
	        timestampButton.addActionListener(this);
	        checkboxPanel.add(alignmentsButton);
	        checkboxPanel.add(sourceButton);
	        checkboxPanel.add(mtButton);
	        checkboxPanel.add(timestampButton);
	        
	        continueButton = new JButton("Continue");
	        continueButton.setEnabled(false);
	        continueButton.addActionListener(this);

	        fc = new JFileChooser();

	        dataButton = new JButton("Select data...");
	        dataButton.addActionListener(this);

	        resultsLabel = new JLabel("Translations will be saved here:");
	        logLabel     = new JLabel("Log file will be saved here:");
	        subjectLabel = new JLabel("Subject label:");
//	        resultsButton = new JButton("Specify output location for translations...");
//	        resultsButton.addActionListener(this);
//	        
//	        logButton = new JButton("Specify output location for log...");
//	        logButton.addActionListener(this);

	        add(subjectLabel);
	        add(subjectArea);
	        add(dataButton);
	        add(dataArea);
//	        add(resultsButton);
	        add(resultsLabel);
	        add(resultsArea);
//	        add(logButton);
	        add(logLabel);
	        add(logArea);
	        add(checkboxPanel);
	        add(continueButton);

	    }

	    private void rememberDirectory(File dir) {
	    	fc.setCurrentDirectory(dir);
	    	prefs.put("DATA_DIRECTORY", dir.getAbsolutePath());
	    }
	    
		@Override
		public void insertUpdate(DocumentEvent e) {
			updateFileNames();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateFileNames();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			updateFileNames();
		}
	    

		private void updateFileNames() {
			
			String dataPath = dataArea.getText();
			String subject = subjectArea.getText();
			
			if (dataPath==null || subject==null || dataPath.trim().isEmpty() || subject.trim().isEmpty()) {
	        	resultsArea.setText("");
            	logArea.setText("");
            	continueButton.setEnabled(false);
			} else {
	            subject = subjectArea.getText().replace(' ', '_');
	            String t = timestampButton.isSelected() ? "." + timestamp : "";
	            resultsArea.setText(dataPath + "." + subject + t + ".output.txt");
	            logArea.setText(dataPath + "." + subject + t + ".log.txt");
	        	
	        	continueButton.setEnabled(true);
	        } 
		}
		
		@Override
	    public void actionPerformed(ActionEvent e) {
//	    	System.err.println("Text:\t'"+subjectArea.getText()+"'");
	    	String dataDirectory = prefs.get("DATA_DIRECTORY", System.getProperty("user.dir"));
	    	fc.setCurrentDirectory(new File(dataDirectory));
	    	
	        if (e.getSource() == dataButton) {
	            int returnVal = fc.showOpenDialog(FileChooser.this);

	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                rememberDirectory(file.getParentFile());
	                String dataPath = file.getAbsolutePath();
	                dataArea.setText(dataPath);
	                updateFileNames();
	            } else {
	            	dataArea.setText("");
	            	resultsArea.setText("");
	            	logArea.setText("");
	            }

	        }  else if (e.getSource() == sourceButton || e.getSource() == mtButton || e.getSource() == alignmentsButton) {
	        	if (!sourceButton.isSelected() || !mtButton.isSelected()) {
	        		alignmentsButton.setSelected(false);
	        	}
	        } else if (e.getSource() == timestampButton) {
	        	updateFileNames();
	        }
//	        else if (e.getSource() == resultsButton) {
//	            int returnVal = fc.showSaveDialog(FileChooser.this);
//	            if (returnVal == JFileChooser.APPROVE_OPTION) {
//	                File file = fc.getSelectedFile();
//	                rememberDirectory(file.getParentFile());
//	                resultsArea.setText(file.getAbsolutePath());
//	            } else {
//	            	resultsArea.setText("");
//	            }
//	        } else if (e.getSource() == logButton) {
//	            int returnVal = fc.showSaveDialog(FileChooser.this);
//	            if (returnVal == JFileChooser.APPROVE_OPTION) {
//	                File file = fc.getSelectedFile();
//	                rememberDirectory(file.getParentFile());
//	                logArea.setText(file.getAbsolutePath());
//	            } else {
//	            	logArea.setText("");
//	            }
//	        } 
	        else if (e.getSource() == continueButton) {
	        	PostEditor.this.frame.setVisible(false);
//	        	PostEditor.this.showPostEditor(dataArea.getText(), resultsPath, logPath, alignmentsButton.isSelected());
	        	PostEditor.this.showPostEditor(dataArea.getText(), resultsArea.getText(), logArea.getText(), subjectArea.getText(), timestamp, alignmentsButton.isSelected(), sourceButton.isSelected(), mtButton.isSelected());
	        }
	        
	        
//	        if (dataArea.getText() != "" && resultsArea.getText() != "" && logArea.getText() != "" && 
//	        	!dataArea.getText().equals(logArea.getText()) && !resultsArea.getText().equals(dataArea.getText()) && !resultsArea.getText().equals(logArea.getText())) {
	        if (dataArea.getText() != "" && !subjectArea.getText().isEmpty()) {
	        	
	        	continueButton.setEnabled(true);
	        }
	        
	    }

	}

	
	public static void main(String[] args) {
//		System.err.println("Running 1...");
		SwingUtilities.invokeLater(new PostEditor());
//		System.err.println("Running 3...");
		/*
		System.err.println("foo");
		if (args.length==5) {
			TranslatedDocuments documents = AFRL_WMT_2014_ParallelDocumentsReader.collateTranslatedDocuments(args[0], args[1], args[2], args[3]);
			PostEditorControllerWithMenu controller = new PostEditorControllerWithMenu(documents);
			EventLogger logger = new EventLogger(args[4]);
			logger.applyListener(controller.window());
		} else {
			System.err.println("Usage: PostEditor postEdit.txt source.txt mtOutput.txt alignments.txt");
		}
		*/
	}

	private final JFrame frame = new JFrame("FileChooserDemo");

	private void showPostEditor(String dataPath, String resultsPath, String logPath, String subject, String timestamp, boolean showAlignments, boolean showSource, boolean showMT) {
//		System.err.println(dataPath);
//		System.err.println(resultsPath);
//		System.err.println(logPath);
//		System.err.println(showAlignments);
		TranslatedDocuments documents = TabDelineatedTranslatedDocuments.get(dataPath, resultsPath, showAlignments, showSource, showMT);
		
		PostEditorControllerWithMenu controller = new PostEditorControllerWithMenu(documents,subject);
		EventLogger logger = new EventLogger(logPath);
		logger.log("# Subject:\t" + subject + "\n");
		logger.log("# Timestamp:\t" + timestamp + "\n");
		logger.log("# Data:\t" + dataPath + "\n");
		logger.log("# Output:\t" + resultsPath + "\n");
		logger.log("# Log:\t" + logPath + "\n");
		logger.log("# Showing alignments:\t"+showAlignments+"\n");
		logger.applyListener(controller.window());
		
	}
	
	@Override
	public void run() {
//		System.err.println("Running 2...");
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new FileChooser());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
		
	}
}
