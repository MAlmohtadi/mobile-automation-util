package jo.aspire.web.automationUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import jo.aspire.helper.EnvirommentManager;
public class SendEmailWithAttachment {
	 
  
    public static void sendEmailWithAttachments() throws IOException{   
    	
        String to = EnvirommentManager.getInstance().getProperty("email_to");
        String from = EnvirommentManager.getInstance().getProperty("email_from");
        final String username = EnvirommentManager.getInstance().getProperty("email_username");
        final String password = EnvirommentManager.getInstance().getProperty("email_password");
        String host = EnvirommentManager.getInstance().getProperty("email_host");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        if (EnvirommentManager.getInstance().getProperty("used_ssl").equals("true")) {
		 props.put("mail.smtp.ssl.trust", EnvirommentManager.getInstance().getProperty("email_host"));
        }
		props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", EnvirommentManager.getInstance().getProperty("email_port"));

        // Get the Session object.
        Session session = Session.getInstance(props,
           new javax.mail.Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication() {
                 return new PasswordAuthentication(username, password);
              }
           });

        try {
           // Create a default MimeMessage object.
           Message message = new MimeMessage(session);

           // Set From: header field of the header.
           message.setFrom(new InternetAddress(from));

           // Set To: header field of the header.
           message.setRecipients(Message.RecipientType.TO,
              InternetAddress.parse(to));
        
           // Set Subject: header field
           message.setSubject(EnvirommentManager.getInstance().getProperty("email_subject"));

           // Create the message part
           BodyPart messageBodyPart = new MimeBodyPart();

      
           // Now set the actual message           
           String htmlText = EnvirommentManager.getInstance().getProperty("email_body");
           messageBodyPart.setContent(htmlText, "text/html");
           
           
           // Create a multipar message
           Multipart multipart = new MimeMultipart();

           // Set text message part
           multipart.addBodyPart(messageBodyPart);

           // Part two is attachment

           messageBodyPart = new MimeBodyPart();
           DataSource source = new FileDataSource(lastFileModified(System.getProperty("user.dir") + File.separator +"reports" + File.separator));
           messageBodyPart.setDataHandler(new DataHandler(source));
           messageBodyPart.setFileName(EnvirommentManager.getInstance().getProperty("email_attachmentName"));
           multipart.addBodyPart(messageBodyPart);

           // Send the complete message parts
           message.setContent(multipart);

           // Send message
           Transport.send(message);

           System.out.println("Sent email successfully....");
    
        } catch (MessagingException e) {
           throw new RuntimeException(e);
        }
    	
        
}   
    
    public static File lastFileModified(String dir) {
        File fl = new File(dir);
        File[] files = fl.listFiles(new FileFilter() {          
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        return choice;
    }
}
