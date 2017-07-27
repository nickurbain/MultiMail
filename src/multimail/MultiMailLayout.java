package multimail;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.*;

import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.StyledDocument;

import com.sun.javafx.css.Style;

public class MultiMailLayout implements ActionListener {
	
	private final String successCopy = "Address copied to clipboard";
	private final String successSend = "Text message sent!";
	private final String configFail = "Error with config file";
	
	private final String[] templates = {"Custom", "Claim Info", "Change"};
	private final String CLAIM_INFO = "Urbain Insurance | \n Claim # |\n Adjustor |\n (Tel) |\n (Email)";
	private final String CHANGE = "Urbain Insurance | Confirmation of change";
	
	JFrame frame;
	TitledBorder numBorder, messageBorder;
	JTextField numField;
	JTextArea messageArea;
	JComboBox templateBox;
	JLabel numLabel, templateLabel, messageLabel, charCountLabel;
	JButton sendButton;
	InputVerifier checkIn;
	
	TelToAddress toAddress;
	SendMail mail;
	
	MultiMailLayout()
	{
		init();
		frame = new JFrame("MultiMail");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setSize(250,300);
		
		JPanel gui = new JPanel(new BorderLayout(2, 2));
		gui.setBackground(new Color(33,150,243));
		
		JPanel inNum = new JPanel(new GridLayout(1,0));
		inNum.setBorder(numBorder);
		inNum.add(new JLabel("", JLabel.LEFT));
		inNum.add(numField);
		inNum.add(new JLabel(" ", JLabel.RIGHT));
		inNum.setBackground(new Color(33,150,243));
		
		JPanel inMessage = new JPanel(new BorderLayout(2,2));
		inMessage.setBorder(messageBorder);
		inMessage.add(templateBox, BorderLayout.NORTH);
		inMessage.add(messageLabel, BorderLayout.WEST);
		inMessage.add(messageArea, BorderLayout.CENTER);
		inMessage.add(charCountLabel, BorderLayout.EAST);
		inMessage.setBackground(new Color(100,181,246));
		
		JPanel send = new JPanel(new GridLayout(1, 0));
		send.setBorder(new BevelBorder(-1));
		send.add(new JLabel("", JLabel.LEFT));
		send.add(sendButton);
		send.add(new JLabel("", JLabel.RIGHT));
		send.setBackground(new Color(33,150,243));
		
		gui.add(inNum, BorderLayout.NORTH);
		gui.add(inMessage, BorderLayout.CENTER);
		gui.add(send, BorderLayout.SOUTH);
		frame.add(gui);
		frame.setVisible(true);
	}
	
	private void init()
	{
		//BORDERS
		numBorder = new TitledBorder("Cellphone Number");
		numBorder.setTitleJustification(TitledBorder.CENTER);
		messageBorder = new TitledBorder("Message");
		messageBorder.setTitleJustification(TitledBorder.CENTER);
		
		numField = new JTextField();
		//Checks that the input contains only valid phone characters
		numField.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				update();
			}
			public void update(){
				if(!numField.getText().matches("[0-9()-]+")){
					numField.setBackground(Color.RED);
					sendButton.setEnabled(false);
				}else{
					numField.setBackground(Color.WHITE);
					sendButton.setEnabled(true);
				}
			}
			
		});
		templateBox = new JComboBox(templates);
		//Adds template text to message field
		templateBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					if(templateBox.getSelectedItem() == templates[0]){
						messageArea.setText("Custom");
					}else if (templateBox.getSelectedItem() == templates[1]){
						messageArea.setText(CLAIM_INFO);
					}else if (templateBox.getSelectedItem() == templates[2]){
						messageArea.setText(CHANGE);
					}
				}
			}
			
		});
		messageLabel = new JLabel("Msg:  ");
		charCountLabel = new JLabel("  /160");
		messageArea = new JTextArea(30,20);
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true);
		//Keep track of character count
		messageArea.getDocument().addDocumentListener(new DocumentListener(){
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				update();
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				update();
			}
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				update();
			}
			public void update(){
				charCountLabel.setText(messageArea.getText().length() + "/160");
				if (messageArea.getText().length() > 160){
					charCountLabel.setForeground(Color.RED);
				}else{
					charCountLabel.setForeground(Color.black);
				}
			}
			
		});
		
		sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		
		
	}
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				new MultiMailLayout();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String userMessage = "";
		String address = "";
		String message = messageArea.getText();
		String number = numField.getText();
		
		toAddress = new TelToAddress(number);
		//If message is blank copy the addresses to clipboard
		if(message.isEmpty() || message.equals(" ")){
			address = toAddress.transform(0);
			StringSelection stringSelection = new StringSelection(address);
			Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			clpbrd.setContents(stringSelection, null);
			userMessage = successCopy;
		}else{
			address = toAddress.transform(1);
			mail = new SendMail(address, message.replaceAll("[\r\n]+", " "));
			try{
				mail.send();
				userMessage = successSend;
			}catch (IOException e1){
				userMessage = configFail;
				e1.printStackTrace();
			}
		}
		JOptionPane.showMessageDialog(frame, userMessage);
		
	}
}
