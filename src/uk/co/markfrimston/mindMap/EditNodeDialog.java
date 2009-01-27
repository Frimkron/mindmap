package uk.co.markfrimston.mindMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditNodeDialog extends JDialog implements ActionListener
{
	private JButton okButton;
	private JButton cancelButton;
	private JTextArea textArea;
	private JTextField nameField;
	
	private boolean okayed = false;
	
	public EditNodeDialog(Component parent,String title, String name, String value)
	{
		this.setTitle(title);
		this.setLayout(new BorderLayout(4,4));
		nameField = new JTextField(name);
		nameField.setBorder(BorderFactory.createTitledBorder("Name"));
		this.add(BorderLayout.NORTH, nameField);
		textArea = new JTextArea();
		textArea.setText(value);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane textScroll = new JScrollPane(textArea);
		textScroll.setBorder(BorderFactory.createTitledBorder("Info"));
		this.add(BorderLayout.CENTER,textScroll);
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		buttonPanel.add(okButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);
		this.add(BorderLayout.SOUTH,buttonPanel);
		this.setSize(new Dimension(300,380));
		this.setLocation(parent.getX()+parent.getWidth()/2-this.getWidth()/2,
				parent.getY()+parent.getHeight()/2-this.getHeight()/2);
		this.setModal(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==okButton)
		{
			okayed = true;
		}
		else
		{
			okayed = false;
		}
		this.dispose();
	}
	
	public String getName()
	{
		return nameField.getText();
	}
	
	public String getValue()
	{
		return textArea.getText();
	}
	
	public boolean wasOkayed()
	{
		return okayed;
	}
}
