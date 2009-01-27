package uk.co.markfrimston.mindMap;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class DiagramPanel extends JPanel implements MouseListener,
	MouseMotionListener, KeyListener
{
	public static final int NODE_RADIUS = 32;
	
	private Diagram diagram;
	private JFrame frame;
	private Node nodeDragged = null;
	private Node nodeJoinStart = null; 
	private int mouseX, mouseY;
	
	public DiagramPanel(Diagram diagram, JFrame frame)
	{
		this.frame = frame;
		this.diagram = diagram;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setFocusable(true);
		this.addKeyListener(this);
		this.setPreferredSize(new Dimension(2000,2000));
	}
	
	public void setDiagram(Diagram diagram)
	{
		this.diagram  = diagram;
	}
	
	public void paint(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.fillRect(0,0,this.getWidth(),this.getHeight());
		
		g.setColor(Color.BLACK);
		for(Join join : diagram.joins)
		{
			g.drawLine(join.start.x,join.start.y,join.end.x,join.end.y);
		}
		
		for(Node node : diagram.nodes)
		{
			g.setColor(Color.WHITE);
			g.fillOval(node.x-NODE_RADIUS, node.y-NODE_RADIUS, 
					NODE_RADIUS*2, NODE_RADIUS*2);
			g.setColor(Color.BLACK);
			g.drawOval(node.x-NODE_RADIUS, node.y-NODE_RADIUS, 
					NODE_RADIUS*2, NODE_RADIUS*2);
			int nameWidth = g.getFontMetrics().stringWidth(node.name);
			g.drawString(node.name, node.x-nameWidth/2, node.y);
		}	
		
		if(nodeJoinStart!=null)
		{
			//dragging join
			g.drawLine(nodeJoinStart.x, nodeJoinStart.y, mouseX, mouseY);
		}
	}
	
	public Node nearbyNode(int x, int y, double threshold)
	{
		double bestDist = -1;
		Node bestNode = null;
		for(Node node : diagram.nodes)
		{
			double dist = Math.sqrt(Math.pow(Math.abs(node.x-x),2)
					+ Math.pow(Math.abs(node.y-y),2));
			if(dist < threshold && (bestNode==null || dist<bestDist))
			{
				bestNode = node;
				bestDist = dist;
			}
		}
		return bestNode;
	}

	public void mouseClicked(MouseEvent arg0) 
	{	
		if(arg0.getButton()==MouseEvent.BUTTON1
				&& arg0.getClickCount()==2)
		{
			Node nearbyNode = nearbyNode(mouseX,mouseY,NODE_RADIUS);
			if(nearbyNode != null)
			{
				//show edit dialog
				EditNodeDialog dialog = new EditNodeDialog(frame, "Edit Node", 
						nearbyNode.name, nearbyNode.data);
				dialog.setVisible(true);
				if(dialog.wasOkayed())
				{
					nearbyNode.name = dialog.getName();
					nearbyNode.data = dialog.getValue();
				}
			}
			else
			{
				EditNodeDialog dialog = new EditNodeDialog(frame, "Create New Node", 
						"New Node", "");
				dialog.setVisible(true);
				if(dialog.wasOkayed())
				{				
					Node newNode = new Node();
					newNode.name = dialog.getName();
					newNode.data = dialog.getValue();
					newNode.x = arg0.getX();
					newNode.y = arg0.getY();
					diagram.nodes.add(newNode);
					this.repaint();
				}
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) 
	{	
	}

	public void mouseExited(MouseEvent arg0) 
	{	
	}

	public void mousePressed(MouseEvent arg0) 
	{	
		//System.out.println("Mouse pressed");
		if(arg0.getButton()==MouseEvent.BUTTON3)
		{
			if(nodeDragged==null && nodeJoinStart==null)
			{
				Node nearbyNode = nearbyNode(arg0.getX(),arg0.getY(),NODE_RADIUS);
				if(nearbyNode!=null)
				{
					//pick up node
					//System.out.println("Picking up node");
					nodeDragged = nearbyNode;
				}
			}
		}
		else if(arg0.getButton() == MouseEvent.BUTTON1)
		{
			if(nodeDragged==null && nodeJoinStart==null)
			{
				Node nearbyNode = nearbyNode(arg0.getX(),arg0.getY(),NODE_RADIUS);
				if(nearbyNode!=null)
				{
					//start join
					nodeJoinStart = nearbyNode;
				}
			}
		}
	}

	public void mouseReleased(MouseEvent arg0) 
	{
		if(arg0.getButton()==MouseEvent.BUTTON3)
		{
			if(nodeDragged!=null)
			{
				//drop node
				//System.out.println("Dropping node");
				nodeDragged = null;
			}
		}
		else if(arg0.getButton() == MouseEvent.BUTTON1)
		{
			if(nodeJoinStart!=null)
			{
				//drop join
				Node nearbyNode = nearbyNode(arg0.getX(),arg0.getY(),NODE_RADIUS);
				if(nearbyNode!=null && nearbyNode!=nodeJoinStart)
				{
					//connected to other node
					Join newJoin = new Join();
					newJoin.start = nodeJoinStart;
					newJoin.end = nearbyNode;
					
					//check if join exists
					Join existingJoin = null;
					for(Join join : diagram.joins)
					{
						if(join.equals(newJoin))
						{
							existingJoin = join;
							break;
						}
					}
					if(existingJoin!=null)
					{
						//join exists - remove it
						diagram.joins.remove(existingJoin);
					}
					else
					{
						//add it as new
						diagram.joins.add(newJoin);
					}
				}
				nodeJoinStart=null;
				repaint();
			}
		}
	}

	public void mouseDragged(MouseEvent arg0) 
	{
		mouseX = arg0.getX();
		mouseY = arg0.getY();
		
		if(nodeDragged!=null)
		{
			nodeDragged.x = arg0.getX();
			nodeDragged.y = arg0.getY();
			this.repaint();
		}
		if(nodeJoinStart!=null)
		{
			this.repaint();
		}
	}

	public void mouseMoved(MouseEvent arg0) 
	{		
		mouseX = arg0.getX();
		mouseY = arg0.getY();
	}

	public void keyPressed(KeyEvent arg0) 
	{
	
	}

	public void keyReleased(KeyEvent arg0) 
	{
		if(arg0.getKeyCode()==KeyEvent.VK_DELETE)
		{
			//System.out.println("delete typed");
			Node nearbyNode = nearbyNode(mouseX,mouseY,NODE_RADIUS);
			if(nearbyNode!=null)
			{
				if(JOptionPane.showConfirmDialog(frame,"Are you sure you want delete \""+nearbyNode.name+"\"",
						"Delete Node",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
						== JOptionPane.YES_OPTION)
				{
					//remove node
					diagram.nodes.remove(nearbyNode);
					
					//remove joins
					Iterator<Join> it = diagram.joins.iterator();
					while(it.hasNext())
					{
						Join join = it.next();
						if(join.start == nearbyNode 
								|| join.end == nearbyNode)
						{
							it.remove();
						}
					}
					repaint();
				}
			}
		}
	}

	public void keyTyped(KeyEvent arg0) 
	{
		
	}
	
}
