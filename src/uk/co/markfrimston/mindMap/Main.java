package uk.co.markfrimston.mindMap;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.util.*;
import java.awt.*;

public class Main extends JFrame implements WindowListener, ActionListener
{
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e){}
		
		Main main = new Main();
	}
	
	public static final String PROGRAM_TITLE = "Mind Map";
	
	private JScrollPane diagramScroll;
	private DiagramPanel diagramPanel;
	private Diagram diagram;
	private JMenuBar menuBar;
	private JMenuItem newMenuItem;
	private JMenuItem quitMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem openMenuItem;
	private JMenuItem saveAsMenuItem;
	private File currentFile = null;
	
	public Main()
	{
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationByPlatform(true);
		this.setTitle(PROGRAM_TITLE);
		
		diagram = new Diagram();
		diagramPanel = new DiagramPanel(diagram, this);
		diagramScroll = new JScrollPane(diagramPanel);
		this.add(diagramScroll);
		this.addWindowListener(this);
		centreWindow();
		
		menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		
		newMenuItem = new JMenuItem("New");
		newMenuItem.setMnemonic('N');
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		newMenuItem.addActionListener(this);	
		fileMenu.add(newMenuItem);
		
		openMenuItem = new JMenuItem("Open");
		openMenuItem.setMnemonic('O');
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		openMenuItem.addActionListener(this);
		fileMenu.add(openMenuItem);
		
		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.setMnemonic('S');
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		saveMenuItem.addActionListener(this);
		fileMenu.add(saveMenuItem);
		
		saveAsMenuItem = new JMenuItem("Save As");
		saveAsMenuItem.setMnemonic('A');
		saveAsMenuItem.addActionListener(this);
		fileMenu.add(saveAsMenuItem);
		
		quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.setMnemonic('Q');
		quitMenuItem.addActionListener(this);
		fileMenu.add(quitMenuItem);
		menuBar.add(fileMenu);
		
		this.setJMenuBar(menuBar);
		
		this.setSize(640,480);
		this.setVisible(true);
	}

	public void windowActivated(WindowEvent arg0) 
	{
		
	}

	public void windowClosed(WindowEvent arg0) 
	{
		
	}

	public void windowClosing(WindowEvent arg0) 
	{
		quitOnConfirm();
	}

	public void windowDeactivated(WindowEvent arg0) 
	{
		
	}

	public void windowDeiconified(WindowEvent arg0) 
	{
		
	}

	public void windowIconified(WindowEvent arg0) 
	{
		
	}

	public void windowOpened(WindowEvent arg0) 
	{
		
	}

	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getSource()==newMenuItem)
		{
			if(JOptionPane.showConfirmDialog(this,"Are you sure you want to start a new file?",
					"New",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)
					== JOptionPane.YES_OPTION)
			{
				setCurrentFile(null);
				diagram.clear();
				centreWindow();
				diagramPanel.repaint();
			}
		}
		else if(arg0.getSource()==openMenuItem)
		{
			if(JOptionPane.showConfirmDialog(this,"Are you sure you want to open a file?",
					"Open",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)
					== JOptionPane.YES_OPTION)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(this);
				File selectedFile = chooser.getSelectedFile();
				if(selectedFile != null)
				{
					if(load(selectedFile))
					{
						setCurrentFile(selectedFile);
						diagramPanel.setDiagram(this.diagram);
						centreWindow();
						diagramPanel.repaint();
					}
				}
			}
		}
		else if(arg0.getSource()==saveMenuItem)
		{
			if(currentFile!=null)
			{
				save(diagram, currentFile);
			}
			else
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setApproveButtonText("Save");
				chooser.showOpenDialog(this);
				File selectedFile = chooser.getSelectedFile();
				if(selectedFile != null)
				{
					if(save(diagram, selectedFile))
					{
						setCurrentFile(selectedFile);
					}
				}
			}
		}
		else if(arg0.getSource()==saveAsMenuItem)
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setApproveButtonText("Save");
			chooser.showOpenDialog(this);
			File selectedFile = chooser.getSelectedFile();
			if(selectedFile != null)
			{
				if(!selectedFile.exists() || (selectedFile.exists() 
					&& JOptionPane.showConfirmDialog(this,
					"The selected file already exists! Would you like to overwrite it?",
					"Open",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)
					== JOptionPane.YES_OPTION))
				{
					if(save(diagram, selectedFile))
					{
						setCurrentFile(selectedFile);						
					}
				}
			}
		}
		else if(arg0.getSource()==quitMenuItem)
		{
			quitOnConfirm();
		}
	}
	
	public boolean load(File file)
	{
		boolean success = false;
		FileInputStream fileIn = null;
		try
		{
			fileIn = new FileInputStream(file);
			
			DocumentBuilderFactory builderFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFact.newDocumentBuilder();
			Document doc = builder.parse(fileIn);
			
			Diagram diagram = new Diagram();
			HashMap<Integer,Node> idNodeLookup = new HashMap<Integer,Node>();
			
			Element elDiag = doc.getDocumentElement();
			org.w3c.dom.Node elDiagChild = elDiag.getFirstChild();
			while(elDiagChild!=null)
			{
				//System.out.println(elDiagChild.getNodeName());
				if(elDiagChild.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE
						&& elDiagChild.getNodeName().equals("nodes"))
				{
					org.w3c.dom.Node elNodesChild = elDiagChild.getFirstChild();
					while(elNodesChild!=null)
					{
						//System.out.println(elNodesChild.getNodeName());
						if(elNodesChild.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE
								&& elNodesChild.getNodeName().equals("node"))
						{
							Element elNode = (Element)elNodesChild;
							
							Node newNode = new Node();
							newNode.name = elNode.getAttribute("name");
							newNode.x = Integer.parseInt(elNode.getAttribute("x"));
							newNode.y = Integer.parseInt(elNode.getAttribute("y"));
							
							if(elNode.getFirstChild()!=null 
									&& elNode.getFirstChild().getNodeType()==org.w3c.dom.Node.TEXT_NODE)
							{
								newNode.data = elNode.getFirstChild().getNodeValue();
							}
							
							//System.out.println("adding node");
							diagram.nodes.add(newNode);
							
							int nodeId = Integer.parseInt(elNode.getAttribute("id"));
							idNodeLookup.put(nodeId, newNode);
						}
						
						elNodesChild = elNodesChild.getNextSibling();
					}
					//System.out.println(diagram.nodes);
				}
				else if(elDiagChild.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE
						&& elDiagChild.getNodeName().equals("joins"))
				{
					org.w3c.dom.Node elJoinsChild = elDiagChild.getFirstChild();
					while(elJoinsChild!=null)
					{
						if(elJoinsChild.getNodeType()==org.w3c.dom.Node.ELEMENT_NODE
								&& elJoinsChild.getNodeName().equals("join"))
						{
							Element elJoin = (Element)elJoinsChild;
							
							Join newJoin = new Join();
							int startId = Integer.parseInt(elJoin.getAttribute("start"));
							Node startNode = idNodeLookup.get(startId);
							int endId = Integer.parseInt(elJoin.getAttribute("end"));
							Node endNode = idNodeLookup.get(endId);
							if(startNode!=null && endNode!=null)
							{
								newJoin.start = startNode;
								newJoin.end = endNode;
								diagram.joins.add(newJoin);
							}
						}
						elJoinsChild = elJoinsChild.getNextSibling();
					}
					//System.out.println(diagram.joins);
				}
				elDiagChild = elDiagChild.getNextSibling();
			}
			
			this.diagram = diagram;
			
			success = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,"Could not open file: "+e.getMessage(),
					"Open",JOptionPane.ERROR_MESSAGE);
			success = false;
		}
		finally
		{
			if(fileIn!=null){
				try{
					fileIn.close();
				}catch(Exception e){}
			}
		}
		return success;
	}
	
	public boolean save(Diagram diagram, File file)
	{
		boolean success = false;
		FileOutputStream fileOut = null;
		try
		{
			fileOut = new FileOutputStream(file);
			
			DocumentBuilderFactory buildFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = buildFact.newDocumentBuilder();
			Document doc = builder.newDocument();
			
			HashMap<Node,Integer> nodeIdLookup = new HashMap<Node,Integer>();
			int nextNodeId = 0;
			
			Element elDiag = doc.createElement("diagram");
			Element elNodes = doc.createElement("nodes");
			for(Node node : diagram.nodes)
			{	
				Element elNode = doc.createElement("node");
				elNode.setAttribute("x", String.valueOf(node.x));
				elNode.setAttribute("y", String.valueOf(node.y));
				elNode.setAttribute("name", node.name);
				elNode.setAttribute("id", String.valueOf(nextNodeId));
				if(node.data!=null){
					elNode.appendChild(doc.createTextNode(node.data));
				}
				elNodes.appendChild(elNode);
				
				nodeIdLookup.put(node, nextNodeId);
				nextNodeId++;
			}
			elDiag.appendChild(elNodes);
			Element elJoins = doc.createElement("joins");
			for(Join join : diagram.joins)
			{
				Element elJoin = doc.createElement("join");
				
				Integer startId = nodeIdLookup.get(join.start);
				Integer endId = nodeIdLookup.get(join.end);
				if(startId!=null && endId!=null)
				{
					elJoin.setAttribute("start", String.valueOf(startId));
					elJoin.setAttribute("end", String.valueOf(endId));
					elJoins.appendChild(elJoin);
				}
			}
			elDiag.appendChild(elJoins);
			doc.appendChild(elDiag);
			
			TransformerFactory transFact = TransformerFactory.newInstance();
			transFact.setAttribute("indent-number", new Integer(2));
			Transformer trans = transFact.newTransformer();
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(fileOut)));
			
			success = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,"Could not save file: "+e.getMessage(),
					"Save",JOptionPane.ERROR_MESSAGE);
			success = false;
		}
		finally
		{
			if(fileOut!=null){
				try{
					fileOut.close();
				}catch(Exception e){}
			}
		}
		return success;
	}
	
	public void quitOnConfirm()
	{
		if(JOptionPane.showConfirmDialog(this,"Are you sure you want to quit?",
				"Quit",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
				== JOptionPane.YES_OPTION)
		{
			this.dispose();
		}
	}
	
	public void setCurrentFile(File file)
	{
		currentFile = file;
		if(file!=null){
			this.setTitle(PROGRAM_TITLE+" - "+file.getName());
		}else{
			this.setTitle(PROGRAM_TITLE);
		}
	}
	
	public void centreWindow()
	{
		int x0 = 1000/2-diagramScroll.getWidth()/2;
		int y0 = 1000/2-diagramScroll.getHeight()/2;
		int x1 = 1000/2+diagramScroll.getWidth()/2;
		int y1 = 1000/2+diagramScroll.getHeight()/2;
		diagramPanel.scrollRectToVisible(new Rectangle(x0,y0,x1,y1));
	}
}
