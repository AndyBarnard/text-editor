/* DogEdit: a text editor with collapsible chat functionality. 
* Created by Andrew Barnard, Copyright 2015 to 2016. Note: this
* is an incomplete application, created with the intention of 
* becoming a better Java programmer.
*/


import javax.swing.*;
import javax.swing.text.*;
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.StringBuilder.*;

public class DogEdit implements Printable, KeyListener {
	
	ChangeListener foregroundChangeListener;
	ChangeListener backgroundChangeListener;
	ColorSelectionModel foreModel;
	ColorSelectionModel backModel;
	int foreRGB;
	int backRGB;
	Color newForegroundColor;
	Color newBackgroundColor;
	
	JFrame mainFrame;
	JFrame colorFrame;
	JFrame foreColorChooserFrame;
	JFrame backColorChooserFrame;
	
	JSplitPane mainSplit;
	JSplitPane chatSplit;
	
	JPanel mainPanel;
	WidgetPanel widgetPanel;
	JPanel chatPanel;
	JPanel shellPanel;
	
	JButton btnCompile;
	
	JButton btnRun;
	
	//URL urlBtnCloseTab = this.getClass().getResource("images\\redx.jpg");
	
	//JButton btnCloseTab;
	//ImageIcon closeImg = new ImageIcon(urlBtnCloseTab);
	
	JTextPane mainText;
	JTabbedPane mainTabs;
	JScrollPane mainTextScroll;
	JTextField filename = new JTextField(), dir = new JTextField();

	JMenuBar menuBar;
	
	JColorChooser foreColorChooser;
	JColorChooser backColorChooser;
	
	// File menu
	JMenu fileMenu;
	JMenuItem newMenuItem;
	JMenuItem saveMenuItem;
	JMenuItem saveAsMenuItem;
	JMenuItem openMenuItem;
	JMenuItem printMenuItem;
	// End File menu
	
	// Edit menu
	JMenu editMenu;
	JMenuItem copyMenuItem;
	JMenuItem cutMenuItem;
	JMenuItem pasteMenuItem;
	// End Edit menu
	
	// View menu
	JMenu viewMenu;
	JMenuItem zoomInMenuItem;
	JMenuItem zoomOutMenuItem;	
	JMenuItem foregroundColorMenuItem;
	JMenuItem backgroundColorMenuItem;
	JMenuItem showChatMenuItem;
	// End View menu
	
	String selected;
	
	String openedFile = "";
	int linesPerPage;
	static int numPages;
	
	JFileChooser fileChoose = new JFileChooser();
	JFileChooser fileOpen = new JFileChooser();
	JFileChooser fileSave = new JFileChooser();
	
	ArrayList<JTextPane> textPanes;							// used to store JTextPanes
	
	//CHAT**************************
		JPanel clientPanel;
		//JPanel outgoingPanel;
		JPanel sendPanel;
		JTextPane incoming;
		JScrollPane incomingScroll;
		JTextField outgoing;
		JButton btnSend;
		//JButton btnClear;
		Font usernameFont = new Font("Arial", Font.ITALIC, 12);
		
		boolean mainShown;
		boolean chatShown;
	//END CHAT****************************
	
	// NETWORKING********************
		Socket sock;
		BufferedReader reader;
		PrintWriter writer;
	// END NETWORKING********************
	
	Font font;
	
	int tabCounter = 2;
	int panelCounter = 2;
	
	boolean changesMade = false;
	boolean saved = false;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);	//Turn off metal's use of bold fonts
			}
		});
		DogEdit d = new DogEdit();
		d.initGui();
	}
	
	public void initGui() {
		
		mainFrame = new JFrame(fileChoose.getCurrentDirectory().toString() + " - Dog Editor");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//mainFrame.setIconImage(mainIcon);
		//mainFrame.setSize(500, 650);
		
		// START MENU BAR ***********************************************
		
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		
		newMenuItem = new JMenuItem("New");
		newMenuItem.setMnemonic(KeyEvent.VK_N);
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newMenuItem.addActionListener(new NewMenuItemListener());
		
		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.setMnemonic(KeyEvent.VK_S);
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveMenuItem.addActionListener(new SaveMenuItemListener());
		
		saveAsMenuItem = new JMenuItem("Save As");
		saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
		saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		saveAsMenuItem.addActionListener(new SaveAsMenuItemListener());
		
		openMenuItem = new JMenuItem("Open");
		openMenuItem.setMnemonic(KeyEvent.VK_O);
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openMenuItem.addActionListener(new OpenMenuItemListener());
		
		printMenuItem = new JMenuItem("Print");
		printMenuItem.setMnemonic(KeyEvent.VK_P);
		printMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		printMenuItem.addActionListener(new PrintMenuItemListener());
		
		fileMenu.add(newMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		fileMenu.add(openMenuItem);
		fileMenu.add(printMenuItem);
		
		copyMenuItem = new JMenuItem("Copy");
		copyMenuItem.setMnemonic(KeyEvent.VK_C);
		copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copyMenuItem.addActionListener(new CopyMenuItemListener());											
		
		cutMenuItem = new JMenuItem("Cut");		
		cutMenuItem.setMnemonic(KeyEvent.VK_T);	
		cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));														
		cutMenuItem.addActionListener(new CutMenuItemListener());
		
		pasteMenuItem = new JMenuItem("Paste");
		pasteMenuItem.setMnemonic(KeyEvent.VK_P);
		pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));																
		pasteMenuItem.addActionListener(new PasteMenuItemListener());
		
		editMenu.add(copyMenuItem);
		editMenu.add(cutMenuItem);
		editMenu.add(pasteMenuItem);
		
		foregroundColorMenuItem = new JMenuItem("Text Color");
		foregroundColorMenuItem.setMnemonic(KeyEvent.VK_T);
		foregroundColorMenuItem.addActionListener(new ForeGroundColorMenuItemListener());
		
		backgroundColorMenuItem = new JMenuItem("Background Color");
		backgroundColorMenuItem.setMnemonic(KeyEvent.VK_B);
		backgroundColorMenuItem.addActionListener(new BackgroundColorMenuItemListener());
		
		showChatMenuItem = new JMenuItem("Toggle Chat");
		showChatMenuItem.setMnemonic(KeyEvent.VK_C);
		showChatMenuItem.addActionListener(new ShowChatMenuItemListener());
		
		zoomInMenuItem = new JMenuItem("Zoom In");
		zoomInMenuItem.setMnemonic(KeyEvent.VK_I);
		zoomInMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK));
		zoomInMenuItem.addActionListener(new ZoomInMenuItemListener());
		
		zoomOutMenuItem = new JMenuItem("Zoom Out");
		zoomOutMenuItem.setMnemonic(KeyEvent.VK_O);
		zoomOutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
		zoomOutMenuItem.addActionListener(new ZoomOutMenuItemListener());
		
		viewMenu.add(foregroundColorMenuItem);
		viewMenu.add(backgroundColorMenuItem);
		viewMenu.add(showChatMenuItem);
		viewMenu.add(zoomInMenuItem);
		viewMenu.add(zoomOutMenuItem);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		mainFrame.setJMenuBar(menuBar);
		
		// END MENU BAR ********************************************************
		
		btnCompile = new JButton("Compile");
		btnCompile.addActionListener(new BtnCompileListener());
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(new BtnRunListener());
		
		//btnCloseTab = new JButton();
		//btnCloseTab.setIcon(closeImg);
		
		mainText = new JTextPane();
		mainTabs = new JTabbedPane();
		JComponent panel1 = makeTextPanel("Untitled 1");
	
		mainText.setBounds(20, 20, 20, 20);
		mainText.addMouseListener(new HighlightListener());
		mainText.setBackground(Color.decode("#171717"));
		mainText.setForeground(Color.decode("#FFFFFF"));
		mainText.setCaretColor(Color.decode("#FFFFFF"));
		
		font = new Font("Lucida Console", Font.PLAIN, 15);
		mainText.setFont(font);
		
		mainText.addKeyListener(this);
		textPanes = new ArrayList<JTextPane>();
		textPanes.add(mainText);
		
		MutableAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setLineSpacing(set, 0.7f);
		textPanes.get(0).setParagraphAttributes(set, true); 
		
		mainTextScroll = new JScrollPane(mainText);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(mainTextScroll, BorderLayout.CENTER);
		mainPanel.setPreferredSize(new Dimension(900, 760));
		
		mainTabs.addTab("Untitled 1", mainPanel);
		//JLabel labelBtnClose = new JLabel();
		//labelBtnClose.add(btnCloseTab);
		//mainTabs.getTabComponentAt(0).setTabComponentAt(0, labelBtnClose);
		
		widgetPanel = new WidgetPanel();
		widgetPanel.add(btnCompile);
		widgetPanel.add(btnRun);
		
		chatPanel = new JPanel();
		chatPanel.setLayout(new GridLayout(1, 3));
		chatPanel.setPreferredSize(new Dimension(25, 140));
		
		clientPanel = new JPanel();
		clientPanel.setLayout(new BoxLayout(clientPanel, BoxLayout.Y_AXIS));
		sendPanel = new JPanel();
		sendPanel.setLayout(new GridLayout(1, 2));
		
		incoming = new JTextPane();
		incoming.setEditable(false);
		incoming.setPreferredSize(new Dimension(mainFrame.getWidth(), 2000));
		incomingScroll = new JScrollPane(incoming);
		
		outgoing = new JTextField(50);
		outgoing.addActionListener(new OutgoingListener());
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new BtnSendListener());
		
		sendPanel.add(outgoing);
		sendPanel.add(btnSend);
		clientPanel.add(incomingScroll);
		clientPanel.add(sendPanel);
		
		shellPanel = new JPanel();
		
		chatSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, chatPanel, shellPanel);
		chatSplit.setDividerLocation(500);
		chatSplit.setDividerSize(4);
		chatSplit.setVisible(false);
		chatShown = false;
		
		chatPanel.add(clientPanel);
		
		setUpNetworking();
		
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
		
		foreColorChooser = new JColorChooser();
		foreModel = foreColorChooser.getSelectionModel();
		backColorChooser = new JColorChooser();
		backModel = backColorChooser.getSelectionModel();
		
		foregroundChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent ev){
				newForegroundColor = foreColorChooser.getColor();
				mainText.setForeground(newForegroundColor);
				mainText.setCaretColor(newForegroundColor);
			}
		};
		
		backgroundChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent ev){
				newBackgroundColor = backColorChooser.getColor();
				mainText.setBackground(newBackgroundColor);
			}
		};
		
		//model.addChangeListener(changeListener);
		
		mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, mainTabs, chatSplit);
		mainSplit.setDividerLocation(820);
		mainSplit.setDividerSize(4);
		//mainSplit.setVisible(false);
		//mainShown = false;
		
		mainFrame.getContentPane().add(BorderLayout.CENTER, mainSplit);
		mainFrame.getContentPane().add(BorderLayout.NORTH, widgetPanel);
		
		foreColorChooserFrame = new JFrame("Color picker");
		foreColorChooserFrame.getContentPane().add(BorderLayout.CENTER, foreColorChooser);
		foreColorChooserFrame.setSize(600, 600);
		
		backColorChooserFrame = new JFrame("Color picker");
		backColorChooserFrame.getContentPane().add(BorderLayout.CENTER, backColorChooser);
		backColorChooserFrame.setSize(600, 600);
		
		chatPanel.setVisible(true);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	public void keyPressed(KeyEvent ev) {
		
	}
	
	public void keyReleased(KeyEvent ev) {
		changesMade = true;
	}
	
	public void keyTyped(KeyEvent ev) {
		
		int key = ev.getKeyCode();
		StyledDocument document = (StyledDocument) mainText.getDocument();
		try {
			if(key == KeyEvent.VK_BRACELEFT) {
				document.insertString(document.getLength(), Integer.toString(KeyEvent.VK_BRACERIGHT), null);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void setUpNetworking() {
		try {
			sock = new Socket("127.0.0.1", 5000);
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			writer = new PrintWriter(sock.getOutputStream());
			System.out.println("Client network connection established successfully");
		} catch(IOException ex) {
			
			ex.printStackTrace();
		}
	}
	
	protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = DogEdit.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            JFrame openErrorFrame = new JFrame();
			JOptionPane.showMessageDialog(openErrorFrame, "Icon not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
	
	protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

	public void saveFile(File file) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(mainText.getText());
			writer.close();
			
		} catch(IOException ex) {
			System.out.println("Unable to save file. Please contact Andy Barnard.");
			ex.printStackTrace();
		}
	}
	
	public void openFile(File file, String opened) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = reader.readLine()) != null) {
				opened += (line + "\n");
			}
			mainText.setText(opened);
			reader.close();
		} catch(IOException ex) {
			JFrame openErrorFrame = new JFrame();
			JOptionPane.showMessageDialog(openErrorFrame, "File not found!", "Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		g.drawString(mainText.getText(), 100, 100);
		
		return PAGE_EXISTS;
	}
	
	public int getNumberOfPages() {
		return numPages;
	}
	
	public void copyToClipboard(String s, ClipboardOwner owner) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferable = new StringSelection(s);
		clipboard.setContents(transferable, owner);
	}
	
	public void cutToClipboard(String s, ClipboardOwner owner) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferable = new StringSelection(s);
		clipboard.setContents(transferable, owner);
		mainText.replaceSelection("");
	}
	
	public void pasteFromClipboard() {
		String result = "";
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if(hasTransferableText) {
			try {
				result = (String)contents.getTransferData(DataFlavor.stringFlavor);
			} catch(UnsupportedFlavorException | IOException ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		mainText.replaceSelection(result);
	}
	
	public class IncomingReader implements Runnable {
		public void run() {
			String line;
			try {
				while((line = reader.readLine()) != null) {
					System.out.println("<Client reading> " + line);
					incoming.setText(incoming.getText() + (line + "\n"));
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public class NewMenuItemListener implements ActionListener{
		public void actionPerformed(ActionEvent ev) {
			
			JPanel mainPanel = new JPanel();
			JTextPane mainText = new JTextPane();
			textPanes.add(mainText);
			mainText.setBounds(20, 20, 20, 20);
			mainText.addMouseListener(new HighlightListener());
			mainText.setBackground(Color.decode("#171717"));
			mainText.setForeground(Color.decode("#FFFFFF"));
			mainText.setCaretColor(Color.decode("#FFFFFF"));
			mainText.setFont(font);
			//mainText.addKeyListener(this);
			
			
			JScrollPane mainTextScroll = new JScrollPane(mainText);
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(mainTextScroll, BorderLayout.CENTER);
			mainPanel.setPreferredSize(new Dimension(900, 760));
			
			mainTabs.addTab("Untitled " + tabCounter, mainPanel);

			
			/*for(int i = 0; i < n; ++i) {
				for(int j = 0; j < n; ++j) {
					if(mainTabs.getTitleAt(i)[j]) {
					
					}
				}
			}*/
			
			
				
			
			++tabCounter;
			++panelCounter;
		}
	}
	
	public void zoomIn() {
		for(int i = 0; i < textPanes.size(); ++i) {
			Font font = textPanes.get(i).getFont();
			float size = font.getSize() + 2.0f;
			textPanes.get(i).setFont(font.deriveFont(size));
		}
	}
	
	public void zoomOut() {
		for(int i = 0; i < textPanes.size(); ++i) {
			Font font = textPanes.get(i).getFont();
			float size = font.getSize() - 2.0f;
			textPanes.get(i).setFont(font.deriveFont(size));
		}
	}
		
	public class SaveMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			if(changesMade && !saved) {	
				fileSave.showSaveDialog(mainFrame);
				saveFile(fileSave.getSelectedFile());
				fileSave.setCurrentDirectory(fileSave.getCurrentDirectory());
				mainFrame.setTitle(fileSave.getCurrentDirectory().toString() + " - Dog Editor");
			
				saved = true;
				changesMade = false;
			} else if(changesMade && saved) {
				saveFile(fileSave.getSelectedFile());
				changesMade = false;
			}
		}
	}
	
	public class SaveAsMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			fileSave.showSaveDialog(mainFrame);
			saveFile(fileSave.getSelectedFile());
			fileSave.setCurrentDirectory(fileSave.getCurrentDirectory());
			mainFrame.setTitle(fileSave.getCurrentDirectory().toString() + " - Dog Editor");
			
			mainTabs.setTabComponentAt(0, new JLabel(fileSave.getSelectedFile().getName().toString()));
			saved = true;
			changesMade = false;
		}
	}

	public class OpenMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			try {
				//fileOpen = new JFileChooser();
				fileOpen.showOpenDialog(mainFrame);
				openFile(fileOpen.getSelectedFile(), openedFile);
				fileOpen.setCurrentDirectory(fileOpen.getCurrentDirectory());
				mainFrame.setTitle(fileOpen.getCurrentDirectory().toString() + " - Dog Editor");
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public class PrintMenuItemListener implements Printable, ActionListener {
		public void actionPerformed(ActionEvent ev) {
			PrinterJob job = PrinterJob.getPrinterJob();
			Book book = new Book();														//
			book.append(this, job.defaultPage());
			job.setPrintable(this);
			job.setPageable(book);
			boolean ok = job.printDialog();
			if(ok) {
				try {
					job.print();
				} catch(PrinterException ex) {
					System.out.println("Printing error");
					ex.printStackTrace();
				}
			}
		}
		
		public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
			Graphics2D g2d = (Graphics2D)g;
			g2d.translate(pf.getImageableX(), pf.getImageableY());
			g.drawString(mainText.getText(), 100, 100);
		
			return PAGE_EXISTS;
		}
	}
	
	public class CopyMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			copyToClipboard(selected, null);															
		}
	}
	
	public class CutMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			cutToClipboard(selected, null);
		}
	}
	
	public class PasteMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			pasteFromClipboard();
		}
	}
	
	public class ForeGroundColorMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			foreModel.addChangeListener(foregroundChangeListener);
			foreColorChooserFrame.pack();
			foreColorChooserFrame.setVisible(true);
		}
	}
	
	public class BackgroundColorMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			backModel.addChangeListener(backgroundChangeListener);
			backColorChooserFrame.pack();
			backColorChooserFrame.setVisible(true);
			
		}
	}
	
	public class ShowChatMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			if(!chatShown) {
				chatSplit.setVisible(true);
				chatShown = true;
			} else {
				chatSplit.setVisible(false);
				chatShown = false;
			}
		}
	}
	
	public class ZoomInMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			zoomIn();
		}
	}
	
	public class ZoomOutMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			zoomOut();
		}
	}
	
	public class BtnCompileListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			try {
				String strDir = fileSave.getCurrentDirectory().toString().substring(3, fileSave.getCurrentDirectory().toString().length());
				Runtime.getRuntime().exec("CMD /C cd " + strDir);
				System.out.println("CMD /C cd " + strDir);
				Runtime.getRuntime().exec("cmd /c javac *.java");
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public class BtnRunListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			try {
				Runtime.getRuntime().exec("cd " + fileSave.getCurrentDirectory().toString());
				Runtime.getRuntime().exec("java ");
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public class BtnSendListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			String currentTime = String.format("%tr", new Date());
			try {
				if(outgoing.getText().length() != 0) {
					writer.println(System.getProperty("user.name") + " " + currentTime + ": " + outgoing.getText());
					writer.flush();
				} else {
					System.out.println("No text in outgoing field. Nothing to send.");
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			outgoing.setText("");
			outgoing.requestFocus();
		}
	}
	
	public class BtnClearListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			incoming.setText("");
		}
	}
	
	public class HighlightListener implements MouseListener {
		public void mousePressed(MouseEvent ev) {
			
		}																					//
		
		public void mouseReleased(MouseEvent ev) {
			if(mainText.getSelectedText() != null) {
				selected = mainText.getSelectedText();
			}
		}
		
		public void mouseEntered(MouseEvent ev) {
			
		}	
		
		public void mouseExited(MouseEvent ev){
			
		}
		
		public void mouseClicked(MouseEvent ev) {
			
		}
	}

	public class OutgoingListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			String currentTime = String.format("%tr", new Date());
			try {
				if(outgoing.getText().length() != 0) {
					String username = System.getProperty("user.name");
					//username.setFont(usernameFont);
					writer.println(username + " " + currentTime + ": " + outgoing.getText());
					writer.flush();
				} else {
					System.out.println("No text in outgoing field. Nothing to send.");
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			outgoing.setText("");
			outgoing.requestFocus();
		}
	}
	
}














