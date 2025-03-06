/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import Common.propertiesLoader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import app.sendEmail;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author Haikal
 */
public class dbController{

    /**
     * @param args the command line arguments
     */
    
    private static propertiesLoader pl;
    
    
    
    
    public static int checkingBatchTracking(Connection conn, String errorType) throws SQLException{
       
        int batchTrackingCount = 0;
        PreparedStatement preparedStatement = null;
        String strQueryBatchTrackingCount = "select count(*) from idb_data_user.IMPL_LOG_MONITORING_BATCH_TRACKING WHERE NOTIFICATION_TYPE = ? ";
        preparedStatement = conn.prepareStatement(strQueryBatchTrackingCount);
        preparedStatement.setString(1, errorType);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            batchTrackingCount = rs.getInt(1);
        }
        return batchTrackingCount;
        
    }
    
    public static int checkingLogRowNA(Connection conn, String errorType, String serverIP) throws SQLException{
       int logCount = 0;
        PreparedStatement preparedStatement = null;
        String strQueryLogCount = "select count(*) from idb_data_user.IMPL_IFMX_LOG where ERROR_TYPE = ? and SERVER_IP = ?";
        preparedStatement = conn.prepareStatement(strQueryLogCount);
        preparedStatement.setString(1, errorType);
        preparedStatement.setString(2, serverIP);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            logCount = rs.getInt(1);
        }
        return logCount;
    }
    
    public static List<String> checkingServerIPNA(Connection conn, String errorType) throws SQLException {
        ArrayList<String> serverIPList = new ArrayList<String>();
        PreparedStatement preparedStatement = null;
        String strQueryLogCount = "select SERVER_IP as SERVER_IP_LIST\nfrom idb_data_user.IMPL_IFMX_LOG where ERROR_TYPE = ? group by SERVER_IP";
        preparedStatement = conn.prepareStatement(strQueryLogCount);
        preparedStatement.setString(1, errorType);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            serverIPList.add(rs.getString("SERVER_IP_LIST"));
        }
        return serverIPList;
    }
    
    public static int checkingLogRow(Connection conn, String currentDate, String currentTime, String errorType, String serverIP) throws SQLException{
        int logCount = 0;
        PreparedStatement preparedStatement = null;
        String strQueryLogCount = "select count(*) from idb_data_user.IMPL_IFMX_LOG where ERROR_TYPE = ? and LOG_DATE >= ? and  LOG_TIME > (case when LOG_DATE > ? then '00:00:00'::time else ? end) and SERVER_IP = ?";
        preparedStatement = conn.prepareStatement(strQueryLogCount);
        
        LocalDate lastDate = LocalDate.parse(currentDate);
        LocalTime lastTime = LocalTime.parse(currentTime);
        
        preparedStatement.setString(1, errorType);
        preparedStatement.setDate(2, Date.valueOf(lastDate));
        preparedStatement.setDate(3, Date.valueOf(lastDate));
        preparedStatement.setTime(4, Time.valueOf(lastTime));
        preparedStatement.setString(5, serverIP);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            logCount = rs.getInt(1);
        }
        return logCount;
    }
    
    public static List<String> checkingServerIP(Connection conn, String errorType, String lastDate, String lastTime) throws SQLException {
        ArrayList<String> serverIPList = new ArrayList<String>();
        PreparedStatement preparedStatement = null;
        String strQueryLogCount = "select SERVER_IP as SERVER_IP_LIST\nfrom idb_data_user.IMPL_IFMX_LOG where ERROR_TYPE = ? and LOG_DATE >= ? and  LOG_TIME > (case when LOG_DATE > ? then '00:00:00.0000000' else ? end)\ngroup by SERVER_IP";
        preparedStatement = conn.prepareStatement(strQueryLogCount);
        preparedStatement.setString(1, errorType);
        preparedStatement.setString(2, lastDate);
        preparedStatement.setString(3, lastDate);
        preparedStatement.setString(4, lastTime);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            serverIPList.add(rs.getString("COUNTROW"));
        }
        return serverIPList;
    }
    
    public static String getCurrentDate(){
        DateTimeFormatter dDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        return dDate.format(localDate);
    }
    
    public static String getCurrentTime(){
        DateTimeFormatter dTime = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        LocalTime localTime = LocalTime.now();
        return dTime.format(localTime);    
    }
    
    public static String getLastDate(Connection conn, String errorType) throws SQLException{
        String lastDate = "";
        PreparedStatement preparedStatement = null;
        String strQuery = "select BATCH_DATE from idb_data_user.IMPL_LOG_MONITORING_BATCH_TRACKING where NOTIFICATION_TYPE = ? order by BATCH_DATE desc, BATCH_TIME desc LIMIT 1";
        preparedStatement = conn.prepareStatement(strQuery);
        preparedStatement.setString(1, errorType);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            lastDate = rs.getString("BATCH_DATE");
        }
        return lastDate;
    }
    
    public static String getLastTime(Connection conn, String errorType) throws SQLException{
        String lastTime = "";
        PreparedStatement preparedStatement = null;
        String strQuery = "select BATCH_TIME from idb_data_user.IMPL_LOG_MONITORING_BATCH_TRACKING where NOTIFICATION_TYPE = ? order by BATCH_DATE desc, BATCH_TIME desc LIMIT 1";
        preparedStatement = conn.prepareStatement(strQuery);
        preparedStatement.setString(1, errorType);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            lastTime = rs.getString("BATCH_TIME");
        }
        return lastTime;
    }
    
    
    public static void insertLogCountNA(Connection conn, String currentDate, String currentTime, String serverIP) throws SQLException, IOException{
        String sql = "INSERT INTO idb_data_user.IMPL_LOG_MONITORING_BATCH_TRACKING VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        prepstmt.setString(1, "N/A");
        LocalTime lastTime = LocalTime.parse(currentTime);
        prepstmt.setTime(2, Time.valueOf(lastTime));
        LocalDate lastDate = LocalDate.parse(currentDate);
        prepstmt.setDate(3, Date.valueOf(lastDate));
        prepstmt.setInt(4, 0);
        prepstmt.setString(5, "N/A");
        prepstmt.setString(6, serverIP);
        prepstmt.executeUpdate();
        prepstmt.close();
        System.out.println("Data successfully added!");
    }
    
    public static void insertLogCountNA(Connection conn, String errorType, String currentDate, String currentTime, String serverIP) throws SQLException, IOException{
        String sql = "INSERT INTO idb_data_user.IMPL_LOG_MONITORING_BATCH_TRACKING (BATCH_TYPE, BATCH_TIME, BATCH_DATE, IS_SEND_NOTIFICATION, NOTIFICATION_TYPE, SERVER_IP)\nSELECT LOG_CONTENT, ?, ?, ?, ERROR_TYPE, ? FROM idb_data_user.IMPL_IFMX_LOG  WHERE ERROR_TYPE = ? and SERVER_IP = ?";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        LocalTime lastTime = LocalTime.parse(currentTime);
        prepstmt.setTime(1, Time.valueOf(lastTime));
        LocalDate lastDate = LocalDate.parse(currentDate);
        prepstmt.setDate(2, Date.valueOf(lastDate));
        prepstmt.setInt(3, 1);
        prepstmt.setString(4, serverIP);
        prepstmt.setString(5, errorType);
        prepstmt.setString(6, serverIP);
        prepstmt.executeUpdate();
        prepstmt.close();
        System.out.println("Data successfully added!");
    }
    
    public static void insertLogCount(Connection conn, String currentDate, String currentTime, String errorType, String serverIP) throws SQLException, IOException{
        String sql = "INSERT INTO idb_data_user.IMPL_LOG_MONITORING_BATCH_TRACKING (BATCH_TYPE, BATCH_TIME, BATCH_DATE, IS_SEND_NOTIFICATION, NOTIFICATION_TYPE, SERVER_IP)\nSELECT LOG_CONTENT, LOG_TIME, LOG_DATE, ?, ERROR_TYPE, ? FROM idb_data_user.IMPL_IFMX_LOG  WHERE ERROR_TYPE = ? and LOG_DATE >= ? and  LOG_TIME > (case when LOG_DATE > ? then '00:00:00'::time else ? end) and SERVER_IP = ?";
        PreparedStatement prepstmt = conn.prepareStatement(sql);
        prepstmt.setInt(1, 1);
        prepstmt.setString(2, serverIP);
        prepstmt.setString(3, errorType);
        LocalDate lastDate = LocalDate.parse(currentDate);
        LocalTime lastTime = LocalTime.parse(currentTime);
        prepstmt.setDate(4, Date.valueOf(lastDate));
        prepstmt.setDate(5, Date.valueOf(lastDate));
        prepstmt.setTime(6, Time.valueOf(lastTime));
        prepstmt.setString(7, serverIP);
        prepstmt.executeUpdate();
        prepstmt.close();
        System.out.println("Data successfully added!");
    }
    
    public static List<String> emailMessageTextNA(Connection conn, String errorType, int limitErrorMessage, int limitCharacterErrorMessage, String serverIP) throws SQLException, IOException{
        ArrayList<String> messageText = new ArrayList<String>();
        PreparedStatement preparedStatement = null;
        String strQueryMessage = "select LOG_CONTENT from idb_data_user.IMPL_IFMX_LOG WHERE ERROR_TYPE = ? and SERVER_IP = ? LIMIT ?";
        preparedStatement = conn.prepareStatement(strQueryMessage);
        preparedStatement.setString(1, errorType);
        preparedStatement.setString(2, serverIP);
        preparedStatement.setInt(3, limitErrorMessage);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            messageText.add(dbController.getLimitChar(rs.getString("LOG_CONTENT"), limitCharacterErrorMessage));
        }
        return messageText;
    }
    
    public static int countMessageTextNA(Connection conn, String errorType, int limitErrorMessage, int limitCharacterErrorMessage, String serverIP) throws SQLException {
        int messageText = 0;
        PreparedStatement preparedStatement = null;
        String strQueryMessage = "select COUNT (*) as COUNT_MESSAGE from idb_data_user.IMPL_IFMX_LOG WHERE ERROR_TYPE = ? and SERVER_IP = ?";
        preparedStatement = conn.prepareStatement(strQueryMessage);
        preparedStatement.setString(1, errorType);
        preparedStatement.setString(2, serverIP);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            messageText = rs.getInt("COUNT_MESSAGE");
        }
        return messageText;
    }
    
    public static List<String> emailMessageTextNAWithRepetition(Connection conn, String errorType, int limitErrorMessage, int limitCharacterErrorMessage, int limitRepetitionErrorMessage, String serverIP) throws SQLException {
        ArrayList<String> messageText = new ArrayList<String>();
        PreparedStatement preparedStatement = null;
        String strQueryMessage = "select A.LOG_CONTENT from idb_data_user.IMPL_IFMX_LOG A\njoin \n(select LOG_CONTENT, COUNT(*) as TOTAL_ROW from idb_data_user.IMPL_IFMX_LOG where ERROR_TYPE = ? \n\tgroup by LOG_CONTENT) as B\non A.LOG_CONTENT = B.LOG_CONTENT\nwhere B.TOTAL_ROW <= ? \nand A.ERROR_TYPE = ? and SERVER_IP = ? LIMIT ?";
        preparedStatement = conn.prepareStatement(strQueryMessage);
        preparedStatement.setString(1, errorType);
        preparedStatement.setInt(2, limitRepetitionErrorMessage);
        preparedStatement.setString(3, errorType);
        preparedStatement.setString(4, serverIP);
        preparedStatement.setInt(5, limitErrorMessage);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            messageText.add(dbController.getLimitChar(rs.getString("LOG_CONTENT"), limitCharacterErrorMessage));
        }
        return messageText;
    }
    
    static String getLimitChar(String value, int limitCharacter) {
        if (value == null || value.length() < limitCharacter) {
            return value; // Safeguard against null values
        }
        return value.substring(0, limitCharacter);
    }
    
    public static List<String> emailMessageText(Connection conn,  String lastDate, String lastTime, String errorType, int limitErrorMessage, int limitCharacterErrorMessage, String serverIP) throws SQLException{
        ArrayList<String> messageText = new ArrayList<String>();
        PreparedStatement preparedStatement = null;
        String strQueryMessage = "select LOG_CONTENT from idb_data_user.IMPL_IFMX_LOG WHERE ERROR_TYPE = ?and LOG_DATE >= ? and  LOG_TIME > (case when LOG_DATE > ? then '00:00:00.0000000' else ? end) and SERVER_IP = ? LIMIT ?";
        preparedStatement = conn.prepareStatement(strQueryMessage);
        preparedStatement.setString(1, errorType);  // Set the errorType parameter
        preparedStatement.setString(2, lastDate);   // Set the lastDate parameter
        preparedStatement.setString(3, lastDate);   // Set the lastDate parameter for comparison
        preparedStatement.setString(4, lastTime);   // Set the lastTime parameter
        preparedStatement.setString(5, serverIP);
        preparedStatement.setInt(6, limitErrorMessage);

        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            messageText.add(dbController.getLimitChar(rs.getString("LOG_CONTENT"), limitCharacterErrorMessage));
        }
        return messageText;

    }
    
    public static int countMessageText(Connection conn, String currentDate, String currentTime, String errorType, int limitErrorMessage, int limitCharacterErrorMessage, String serverIP) throws SQLException {
        int messageText = 0;
        PreparedStatement preparedStatement = null;
        String strQueryMessage = "select count (*) as COUNT_MESSAGE from idb_data_user.IMPL_IFMX_LOG WHERE ERROR_TYPE = ? and LOG_DATE >= ? and  LOG_TIME > (case when LOG_DATE > ? then '00:00:00'::time else ? end) and SERVER_IP = ?";
        preparedStatement = conn.prepareStatement(strQueryMessage);
        LocalDate lastDate = LocalDate.parse(currentDate);
        LocalTime lastTime = LocalTime.parse(currentTime);
        preparedStatement.setString(1, errorType);
        preparedStatement.setDate(2, Date.valueOf(lastDate));
        preparedStatement.setDate(3, Date.valueOf(lastDate));
        preparedStatement.setTime(4, Time.valueOf(lastTime));
        preparedStatement.setString(5, serverIP);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            messageText = rs.getInt("COUNT_MESSAGE");
        }
        return messageText;
    }
    
    public static List<String> emailMessageTextWithRepetition(Connection conn, String currentDate, String currentTime, String errorType, int limitErrorMessage, int limitCharacterErrorMessage, int limitRepetitionErrorMessage, String serverIP) throws SQLException {
        ArrayList<String> messageText = new ArrayList<String>();
        PreparedStatement preparedStatement = null;
        String strQueryMessage = "select A.LOG_CONTENT from idb_data_user.IMPL_IFMX_LOG A\njoin \n(select LOG_CONTENT, COUNT(*) as TOTAL_ROW from idb_data_user.IMPL_IFMX_LOG where ERROR_TYPE = ? \nand LOG_DATE >=? \tgroup by LOG_CONTENT) as B\non A.LOG_CONTENT = B.LOG_CONTENT\nwhere B.TOTAL_ROW <= ? \nand A.LOG_DATE >= ? and  A.LOG_TIME > (case when A.LOG_DATE > ? then '00:00:00'::time else ? end) and A.ERROR_TYPE = ? and SERVER_IP = ? LIMIT ?";
        preparedStatement = conn.prepareStatement(strQueryMessage);
        LocalDate lastDate = LocalDate.parse(currentDate);
        LocalTime lastTime = LocalTime.parse(currentTime);
        preparedStatement.setString(1, errorType);
        preparedStatement.setDate(2, Date.valueOf(lastDate));
        preparedStatement.setInt(3, limitRepetitionErrorMessage);
        preparedStatement.setDate(4, Date.valueOf(lastDate));
        preparedStatement.setDate(5, Date.valueOf(lastDate));
        preparedStatement.setTime(6, Time.valueOf(lastTime));
        preparedStatement.setString(7, errorType);
        preparedStatement.setString(8, serverIP);
        preparedStatement.setInt(9, limitErrorMessage);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            messageText.add(dbController.getLimitChar(rs.getString("LOG_CONTENT"), limitCharacterErrorMessage));
        }
        return messageText;
    }
   
}
