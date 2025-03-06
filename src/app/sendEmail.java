/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;


import Common.Logging;
import Common.propertiesLoader;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import java.util.Arrays;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 *
 * @author Haikal
 */
public class sendEmail {
    //testing netbeans
    public static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    public static ch.qos.logback.classic.Logger logger = loggerContext.getLogger("app");
    public static propertiesLoader pl;

    /*public static void sendEmail(HashMap<String, String> messageText, HashMap<String, Integer> countMessage) throws AddressException {
        Properties prop = new Properties();
        Logging log = new Logging(); 
        log.configLog(pl,logger,loggerContext);
        
        String currentDate = dbController.getCurrentDate();
        String currentTime = dbController.getCurrentTime();
        String errorType = pl.errorType;
        String sender = pl.sender;
        String passwordSender = pl.passwordSender;
        String[] recipients = pl.recipients;
        String hostname = pl.hostname;
        String port = pl.port;
        String auth = pl.auth;
        String starttls = pl.starttls;

        System.out.println("Sender Email : " + sender);
        final String username = sender;
        final String password = passwordSender;
        prop.put("mail.smtp.host", hostname);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.auth", auth);
        prop.put("mail.smtp.starttls.enable", starttls);
        prop.put("mail.smtp.ssl.trust", "*");
        if (messageText.size() > 0) {
            try {
                for (String recipient : recipients) {
                    System.out.println("processing sending message to " + recipient + "...");
                    logger.info("processing sending message to " + recipient + "...");
                    Session session = Session.getInstance((Properties)prop, (Authenticator)new Authenticator(){

                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom((Address)new InternetAddress(sender));
                    message.setRecipients(Message.RecipientType.TO, (Address[])InternetAddress.parse((String)recipient));
                    String text = "Dear User,\n\nSystem founds issue on log process with details below:\n       Batch Process Date : " + currentDate + "\n       Batch Process Time : " + currentTime + "\n       Error Type : " + errorType;
                    String logMessage = "";
                    for (Map.Entry<String, String> pair : messageText.entrySet()) {
                        logMessage = logMessage + "\n       Total Message on server " + pair.getKey() + " : " + countMessage.get(pair.getKey());
                        logMessage = logMessage + "\n       CONTENT LOG ON SERVER " + pair.getKey() + " : \n" + pair.getValue();
                    }
                    message.setSubject(errorType + " LOG PROCESS");
                    message.setText(text + "\n" + logMessage);
                    Transport.send((Message)message);
                    System.out.println("Email sent!");
                    logger.info("Email sent to " + recipient);
                }
            }
            catch (MessagingException e) {
                e.printStackTrace();
                logger.info("Process send email get error" + e.getMessage());
            }
        } else {
            System.out.println("email not send, message null");
            logger.info("email not send, message null");
        }
    }*/
    
    public static void Email(HashMap<String, String> messageText, HashMap<String, Integer> countMessage) {
    // Initialize properties
    Properties prop = new Properties();
    //Logging log = new Logging();
    //log.configLog(pl, logger, loggerContext);

    // Fetch necessary parameters
    String currentDate = dbController.getCurrentDate();
    String currentTime = dbController.getCurrentTime();
    String errorType = pl.errorType;
    String sender = pl.sender;
    String passwordSender = pl.passwordSender;
    String[] recipients = pl.recipients;
    String hostname = pl.hostname;
    String port = pl.port;
    String auth = pl.auth;
    String starttls = pl.starttls;

    // Basic validation checks
    if (sender == null || passwordSender == null || recipients == null || recipients.length == 0) {
        System.out.println("Error: Missing critical email information (sender, password, or recipients)");
        logger.error("Error: Missing critical email information (sender, password, or recipients)");
        return;
    }

    if (messageText == null || messageText.isEmpty()) {
        System.out.println("Error: messageText is empty");
        logger.error("Error: messageText is empty");
        return;
    }

    if (countMessage == null || countMessage.isEmpty()) {
        System.out.println("Error: countMessage is empty");
        logger.error("Error: countMessage is empty");
        return;
    }

    // Log sender and recipients
    System.out.println("Sender Email: " + sender);
    System.out.println("Recipients: " + Arrays.toString(recipients));

    final String username = sender;
    final String password = passwordSender;

    // Setup email properties
    prop.put("mail.smtp.host", hostname);
    prop.put("mail.smtp.port", port);
    prop.put("mail.smtp.auth", auth);
    prop.put("mail.smtp.starttls.enable", starttls);
    prop.put("mail.smtp.ssl.trust", "*");

    // Send email if there is data to send
    if (messageText.size() > 0) {
        try {
            for (String recipient : recipients) {
                if (recipient == null || recipient.isEmpty()) {
                    System.out.println("Skipping empty recipient address.");
                    logger.warn("Skipping empty recipient address.");
                    continue;
                }

                // Email session setup
                Session session = Session.getInstance(prop, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                // Create email message
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

                // Construct email body
                String text = "Dear User,\n\nSystem found an issue with log processing:\n"
                        + "Batch Process Date: " + currentDate + "\n"
                        + "Batch Process Time: " + currentTime + "\n"
                        + "Error Type: " + errorType;

                StringBuilder logMessage = new StringBuilder();
                for (Map.Entry<String, String> pair : messageText.entrySet()) {
                    String key = pair.getKey();
                    String messageContent = pair.getValue();
                    Integer count = countMessage.get(key);

                    // If count is missing, default to 0
                    if (count == null) {
                        count = 0;
                    }

                    logMessage.append("\nTotal Messages on server ").append(key)
                            .append(": ").append(count)
                            .append("\nContent Log on server ").append(key)
                            .append(" : \n").append(messageContent);
                }

                message.setSubject(errorType + " LOG PROCESS");
                message.setText(text + "\n" + logMessage.toString());

                // Send the email
                Transport.send(message);
                System.out.println("Email sent to: " + recipient);
                logger.info("Email sent to " + recipient);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Error during email sending process: " + e.getMessage(), e);
        }
    } else {
        System.out.println("Email not sent, messageText is empty.");
        logger.info("Email not sent, messageText is empty.");
    }
}
}

