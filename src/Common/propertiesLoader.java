/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import main.Decryptor;

/**
 *
 * @author Haikal
 */
public class propertiesLoader {
    private Properties prop = null;
    
    public final String log_file_dir;
    
    public final String serverIPConfig;
    public final String db_host;
    public final String errorType;
    public final String db_port;
    public final String db_username;
    public final String db_name;
    public final String db_password;
    public final int limitErrorMessage;
    public final int limitCharacterErrorMessage;
    public final int limitRepetitionErrorMessage;
    public final String sender;
    public final String passwordSender;
    public final String[] recipients;
    public final String hostname;
    public final String port;
    public final String auth;
    public final String starttls;
    public final String strQueryBatchTrackingCount;
    public final String strQueryLogRowNA;
    public final String strQueryServerIPNA;
    public final String strQueryLogRow;
    public final String strQueryCheckingServerIP;
    public final String strQueryLastDate;
    public final String strQueryLastTime;
    public final String insertLogCountNA1;
    public final String insertLogCountNA2;
    public final String insertLogCountQuery;
    public final String getLogContentQueryNA;
    public final String strQueryCountMessageTextNA;
    public final String getLogContentQueryNAWithRepitition;
    public final String getLogContentQuery;
    public final String strQueryCountMessageText;
    public final String getLogContentQueryWithRepitition;
    
    public propertiesLoader(String prop_location) throws Exception{
        prop = new Properties();
        Decryptor dec = new Decryptor();
        
        try {
            FileInputStream propFile = new FileInputStream(prop_location);
            this.prop.load(propFile);  // Load the properties file
            propFile.close();  // Close the file input stream
        
        this.log_file_dir = this.prop.getProperty("LOG_FILE_DIR");
       
        this.serverIPConfig =this.prop.getProperty("ServerIP");
        this.db_host = this.prop.getProperty("DB_Host");
        this.errorType = this.prop.getProperty("Status_to_Monitor");
        this.db_port = this.prop.getProperty("DB_Port");
        this.db_username = this.prop.getProperty("DB_Username");
        this.db_name = this.prop.getProperty("DB_Name");
        
        
        this.limitErrorMessage = Integer.parseInt(this.prop.getProperty("Limit_Number_Error_Message"));
        this.limitCharacterErrorMessage = Integer.parseInt(this.prop.getProperty("Limit_Character_Error_Message"));
        this.limitRepetitionErrorMessage = Integer.parseInt(this.prop.getProperty("Limit_Repetition_Error_Message"));
        this.sender = this.prop.getProperty("Email_Address_Sender");
        this.recipients = this.prop.getProperty("Email_Address_Recipient").split(";");
        this.hostname = this.prop.getProperty("Email_Host");
        this.port = this.prop.getProperty("Email_Port");
        this.auth = this.prop.getProperty("Email_Auth");
        this.starttls = this.prop.getProperty("Email_Starttls_Enable");
        
        //query properties
        this.strQueryBatchTrackingCount = this.prop.getProperty("strQueryBatchTrackingCount");
        this.strQueryLogRowNA = this.prop.getProperty("strQueryLogRowNA");
        this.strQueryServerIPNA = this.prop.getProperty("strQueryServerIPNA");
        this.strQueryLogRow = this.prop.getProperty("strQueryLogRow");
        this.strQueryCheckingServerIP = this.prop.getProperty("strQueryCheckingServerIP");
        this.strQueryLastDate = this.prop.getProperty("strQueryLastDate");
        this.strQueryLastTime = this.prop.getProperty("strQueryLastTime");
        this.insertLogCountNA1 = this.prop.getProperty("insertLogCountNA1");
        this.insertLogCountNA2 = this.prop.getProperty("insertLogCountNA2");
        this.insertLogCountQuery = this.prop.getProperty("insertLogCountQuery");
        this.getLogContentQueryNA = this.prop.getProperty("getLogContentQueryNA");
        this.strQueryCountMessageTextNA = this.prop.getProperty("strQueryCountMessageTextNA");
        this.getLogContentQueryNAWithRepitition = this.prop.getProperty("getLogContentQueryNAWithRepitition");
        this.getLogContentQuery = this.prop.getProperty("getLogContentQuery");
        this.strQueryCountMessageText = this.prop.getProperty("strQueryCountMessageText");
        this.getLogContentQueryWithRepitition = this.prop.getProperty("getLogContentQueryWithRepitition");
        
        
        this.db_password = dec.decrypt(this.prop.getProperty("DB_Password"));
        this.passwordSender = dec.decrypt(this.prop.getProperty("Email_Sender_Password"));
        
        } catch (FileNotFoundException e) {
            throw new Exception("Properties file not found: " + prop_location, e);
        } catch (IOException e) {
            throw new Exception("Error loading properties file: " + prop_location, e);
        } catch (Exception e) {
            throw new Exception("An error occurred while initializing PropertiesLoader", e);
        }  
        
    }
}
