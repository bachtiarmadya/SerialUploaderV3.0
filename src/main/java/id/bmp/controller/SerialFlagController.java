/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.bmp.controller;

import id.bmp.configuration.DBConnectionManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author permadi
 */
public class SerialFlagController {

    private Connection conn;

    public SerialFlagController() {
        try {
            this.conn = DriverManager.getConnection(DBConnectionManager.MYSQL_URL, DBConnectionManager.MYSQL_UNAME, DBConnectionManager.MYSQL_PASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(SerialFlagController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void flagDownloaded(Long start, Long finish) throws SQLException {

        try {
            String query = "UPDATE serial_data SET downloaded = 1 WHERE ID >= ? AND ID <= ?";
            PreparedStatement insertUpload = conn.prepareStatement(query);

            insertUpload.setLong(1, start);
            insertUpload.setLong(2, finish);

            insertUpload.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(SerialFlagController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

    }

    public void flagWaitingList(Long id) throws SQLException {

        try {
            String query = "UPDATE `serial_data_queue` SET `uploaded`= 1 WHERE `ID` = ?";
            PreparedStatement insertUpload = conn.prepareStatement(query);

            insertUpload.setLong(1, id);

            insertUpload.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(SerialFlagController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void flagUploaded(Long id) throws SQLException {

        try {
            String query = "UPDATE `serial_data_uploaded` SET `uploaded`= 1 WHERE `ID` = ?";
            PreparedStatement insertUpload = conn.prepareStatement(query);

            insertUpload.setLong(1, id);

            insertUpload.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(SerialFlagController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

}
