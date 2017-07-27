package multimail;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;


public class MultiMail implements ActionListener{
	
	private final String[] templates = {"Custom", "Claim Info", "Change"};
	private final String CLAIM_INFO = "Urbain Insurance | \n Claim # |\n Adjustor |\n (Tel) |\n (Email)";
	private final String CHANGE = "Urbain Insurance | Confirmation of change";
	
	private TelToAddress tta;
	private SendMail mail;
	private JFrame frame;
	private JTextField numberTextField;
	private JTextField charCount;
	private JTextArea messageTextArea;
	private JButton numberButton;
	private JComboBox templateComboBox;
	private JLabel numberLabel, messageLabel, templateLabel;
	
	public MultiMail()
	{
		init();
	}
	
	public static void main(String args[]) 
	{
        new MultiMail();
	}

	private void init()
	{
		frame = new JFrame("MultiMail");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(900,500,300,200);
		frame.setLayout(new GridBagLayout());
		
		charCount = new JTextField(3);
		numberTextField = new JTextField(10);
		
		messageTextArea = new JTextArea(10, 10);
		messageTextArea.setPreferredSize(new Dimension(30,20));
		messageTextArea.setLineWrap(true);
		messageTextArea.setWrapStyleWord(true);
		messageTextArea.setBorder(new JTextField().getBorder());
		
		numberButton = new JButton("Go: ");
		numberLabel = new JLabel("Tel Number: ");
		messageLabel = new JLabel("0 / 160");
		templateLabel = new JLabel("Template ");
		
		templateComboBox = new JComboBox(templates);
		//Add template text to messageTextArea from templateComboBox
		templateComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e){
				if(e.getStateChange() == ItemEvent.SELECTED){
					if(templateComboBox.getSelectedItem() == templates[0]){
						messageTextArea.setText("Custom");
					}else if (templateComboBox.getSelectedItem() == templates[1]){
						messageTextArea.setText(CLAIM_INFO);
					}else if (templateComboBox.getSelectedItem() == templates[2]){
						messageTextArea.setText(CHANGE);
					}
				}
			}
		});
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(10, 10, 10, 10);
		
		//TELEPHONE NUMBER
		gc.gridx = 0;
		gc.gridy = 0;
		frame.add(numberLabel, gc);
		
		gc.gridx = 1;
		gc.gridy = 0;
		frame.add(numberTextField, gc);
		
		//TEMPLATE SELECTION
		gc.gridx = 2;
		gc.gridy = 1;
		frame.add(templateLabel);
		
		gc.gridx = 2;
		gc.gridy = 2;
		frame.add(templateComboBox);
		
		
		//MESSAGE BOX
		gc.gridx = 0;
		gc.gridy = 2;
		frame.add(messageLabel, gc);
		
		//Character Counter
		gc.gridx = 1;
		gc.gridy = 2;
		messageTextArea.getDocument().addDocumentListener(new DocumentListener(){

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
				messageLabel.setText(messageTextArea.getText().length()+"/ 160");
			}
			
		});
		frame.add(messageTextArea, gc);
		
		//SEND BUTTON
		gc.gridx = 1;
		gc.gridy = 3;
		frame.add(numberButton, gc);
		numberButton.addActionListener(this);
		
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String message = "";
		String body = messageTextArea.getText();
		
		String address = numberTextField.getText();
		tta = new TelToAddress(address);
		
		//If there is no message, copy the phone number as addresses to the clipboard
		if(body.equals("") || body.equals(" ")){
			address = tta.transform(0);
			StringSelection stringSelection = new StringSelection(address);
			Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			clpbrd.setContents(stringSelection, null);
			message = "Address copied to clipboard!";
		//Otherwise send email's out to those addresses
		}else{
			address = tta.transform(1);
			mail = new SendMail(address, body.replaceAll("[\r\n]+", " "));
			try {
				mail.send();
				message = "Text message has been sent";
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				message = "Config file error";
				e1.printStackTrace();
			}
		}
		//Message box to display an error or success
		JOptionPane.showMessageDialog(frame, message);
	}
}
