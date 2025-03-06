/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import Common.Logging;
import Common.propertiesLoader;
import ch.qos.logback.classic.LoggerContext;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Haikal
 */
public class app {
    //TEstings
    //test
    public static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    public static ch.qos.logback.classic.Logger logger = loggerContext.getLogger("app");
    public static propertiesLoader pl;

    public static void main(String[] args) throws Exception {
        pl = new propertiesLoader(args[0]);
        Properties prop = new Properties();
        Logging log = new Logging(); 
        log.configLog(pl,logger,loggerContext);
        
        Connection conn = dbConn.getdbconn(pl.db_host, pl.db_port, pl.db_username, pl.db_name, pl.db_password);
        try {
            logger.info("Starting IFMX Error Log Monitoring - server IP " + pl.serverIPConfig);
            int batchTrackingRow = dbController.checkingBatchTracking(conn, pl.errorType);
            System.out.println("Checking Batch Tracking Row...");
            logger.info("Counting Batch Tracking table row..");
            if (batchTrackingRow == 0) {
                System.out.println("found " + batchTrackingRow + " rows");
                logger.info("found " + batchTrackingRow + " rows");
                System.out.println("Checking Log Row....");
                logger.info("Counting Log Loader table row..");
                boolean isSendEmail = false;
                HashMap<String, String> values = new HashMap<String, String>();
                HashMap<String, Integer> count_values = new HashMap<String, Integer>();
                List<String> serverIPList = dbController.checkingServerIPNA(conn, pl.errorType);
                for (String serverIP : serverIPList) {
                    List<String> messageText;
                    int logRow = dbController.checkingLogRowNA(conn, pl.errorType, serverIP);
                    System.out.println("found " + logRow + " row");
                    logger.info("found " + logRow + " rows");
                    if (logRow == 0) {
                        dbController.insertLogCountNA(conn, dbController.getCurrentDate(), dbController.getCurrentTime(), serverIP);
                        continue;
                    }
                    dbController.insertLogCountNA(conn, pl.errorType, dbController.getCurrentDate(), dbController.getCurrentTime(), serverIP);
                    dbController.emailMessageTextNA(conn, pl.errorType, pl.limitErrorMessage, pl.limitCharacterErrorMessage, serverIP);
                    logger.info("new log detected");
                    int countMessage = dbController.countMessageTextNA(conn, pl.errorType, pl.limitErrorMessage, pl.limitCharacterErrorMessage, serverIP);
                    //System.out.println(countMessage);
                    count_values.put(serverIP, countMessage);
                    isSendEmail = true;
                    if (pl.limitRepetitionErrorMessage <= 0) {
                        messageText = dbController.emailMessageTextNA(conn, pl.errorType, pl.limitErrorMessage, pl.limitCharacterErrorMessage, serverIP);   
                        if (messageText.isEmpty()) continue;
                        values.put(serverIP, String.join("\n", messageText));
                        continue;
                    }
                    messageText = dbController.emailMessageTextNAWithRepetition(conn, pl.errorType, pl.limitErrorMessage, pl.limitCharacterErrorMessage, pl.limitRepetitionErrorMessage, serverIP);
                    //System.out.println(messageText);
                    if (messageText.isEmpty()) continue;
                    values.put(serverIP, String.join("\n", messageText));
                    //System.out.println("LEWAT");
                }
                if (isSendEmail && !values.isEmpty()) {
                    //System.out.println("Send Mail Proces"); 
                    //System.out.println("CP");
                    //System.out.println(values);
                    //System.out.println(count_values);
                     try {
                         System.out.println("Send Email!!!");
                        //sendEmail.Email(values, count_values);
                    } catch (Exception e) {
                        e.printStackTrace();  // Capture any potential exceptions thrown before the method
                        System.out.println("Error during email sending: " + e.getMessage());
                    }
                    System.out.println("LEWAT");
                }
            } else {
                System.out.println("found " + batchTrackingRow + " row");
                logger.info("found " + batchTrackingRow + " rows");
                String lastTimeLog = dbController.getLastTime(conn, pl.errorType);
                String lastDateLog = dbController.getLastDate(conn, pl.errorType);
                System.out.println("Checking Log Row....");
                logger.info("Last timestamp from table batch tracking " + lastDateLog + " " + lastTimeLog);
                boolean isSendEmail = false;
                HashMap<String, String> values = new HashMap<String, String>();
                HashMap<String, Integer> count_values = new HashMap<String, Integer>();
                List<String> serverIPList = dbController.checkingServerIPNA(conn, pl.errorType);
                for (String serverIP : serverIPList) {
                    List<String> messageText;
                    int logRow = dbController.checkingLogRow(conn, lastDateLog, lastTimeLog, pl.errorType, serverIP);
                    System.out.println("found " + logRow + " logRows");
                    logger.info("found " + logRow + " logRows");
                    if (logRow == 0) {
                        dbController.insertLogCountNA(conn, dbController.getCurrentDate(), dbController.getCurrentTime(), serverIP);
                        continue;
                    }
                    dbController.insertLogCount(conn, lastDateLog, lastTimeLog, pl.errorType, serverIP);
                    logger.info("new log detected");
                    int countMessage = dbController.countMessageText(conn, lastDateLog, lastTimeLog, pl.errorType, pl.limitErrorMessage, pl.limitCharacterErrorMessage, serverIP);
                    count_values.put(serverIP, countMessage);
                    //System.out.println("LEWAT");
                    isSendEmail = true;
                    if (pl.limitRepetitionErrorMessage <= 0) {
                        messageText = dbController.emailMessageText(conn, lastDateLog, lastTimeLog, pl.errorType, pl.limitErrorMessage, pl.limitCharacterErrorMessage, serverIP);
                        //System.out.println("LEWAT");
                        if (messageText.isEmpty()) continue;
                        values.put(serverIP, String.join((CharSequence)"\n", messageText));
                        continue;
                    }
                    //System.out.println("LEWAT");
                    messageText = dbController.emailMessageTextWithRepetition(conn, lastDateLog, lastTimeLog, pl.errorType, pl.limitErrorMessage, pl.limitCharacterErrorMessage, pl.limitRepetitionErrorMessage, serverIP);
                    System.out.println(messageText);
                    if (messageText.isEmpty()) continue;
                    values.put(serverIP, String.join((CharSequence)"\n", messageText));
                    
                }
                if (isSendEmail && !values.isEmpty()) {
                    //sendEmail.sendEmail(values, dbController.getCurrentDate(), dbController.getCurrentTime(), pl.errorType, count_values, pl.sender, pl.passwordSender, pl.recipients, pl.hostname, pl.port, pl.auth, pl.starttls);
                    try {
                        System.out.println("Send Email!!!");
                        //sendEmail.Email(values, count_values);
                    } catch (Exception e) {
                        e.printStackTrace();  // Capture any potential exceptions thrown before the method
                        System.out.println("Error during email sending: " + e.getMessage());
                    }
                }
            }
            logger.info("Finished run IFMX Error Log Monitoring");
            conn.close();
        }
        catch (Exception e) {
            System.out.println("System get Error " + e.getMessage());
            logger.error("System get error : " + e.getMessage());
        }
    }
}

