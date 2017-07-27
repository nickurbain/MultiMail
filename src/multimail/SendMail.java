package multimail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	
	private static final String SMTPSERVER = "smtp.gmail.com";
	private static final String SMTPPORT = "25";
	
	private Properties config;
	String body;
	String addresses;
	
	/**
	 * 
	 * @param recipient email address
	 * @param message
	 */
	public SendMail(String recipient, String message)
	{
		addresses = recipient;
		body = message;
	}
	
	public void send() throws IOException
	{
		String username, password;
		//read username and password from a config file so that I can use git
		FileInputStream in = new FileInputStream("config");
		config = new Properties();
		config.load(in);
		username = config.getProperty("username");
		password = config.getProperty("password");
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", SMTPSERVER);
		props.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				  });
		
		try{
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("multimailtestmail@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(addresses));
			message.setText(body);
			
			Transport.send(message);
			
		} catch(MessagingException e){
			throw new RuntimeException(e);
		}
		
	}

}
