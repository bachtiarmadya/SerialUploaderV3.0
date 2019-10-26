/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.bmp.controller;

import id.bmp.configuration.DBConnectionManager;
import id.bmp.configuration.FlowmeterConfiguration;
import id.bmp.configuration.SiteConfiguration;
import id.bmp.model.SerialData;
import id.bmp.model.SerialDataQueue;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author permadi
 */
public class SerialWaitingListController {

    public void setToWaitingList(SerialData input) throws IOException {
        Connection conn = null;
        String query = "INSERT INTO `serial_data_queue` "
                + "(`uploaded`, `ID_start`, `ID_end`, `ticket_no`, `start`, `finish`, `start_count`, `start_count_uom`, `end_count`, "
                + "`end_count_uom`, `gross_deliver`, `gross_deliver_uom`, `avg_flow_rate`, `avg_flow_rate_uom`, `after_avg_flow_rate`, `sale_number`, `meter_number`, "
                + "`unit_id`, `duplicate`, `other_one`, `other_two`, `other_three`, `other_four`, `other_five`, `data_state`) "
                + "VALUES (0, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conn = DriverManager.getConnection(DBConnectionManager.MYSQL_URL, DBConnectionManager.MYSQL_UNAME, DBConnectionManager.MYSQL_PASSWORD);

            FlowmeterConfiguration config = new FlowmeterConfiguration();

            PreparedStatement insertQueue = conn.prepareStatement(query);
            insertQueue.setLong(1, input.getStartId());
            insertQueue.setLong(2, input.getEndId());
            insertQueue.setString(3, input.getTicketNumber());
            insertQueue.setString(4, input.getStart());
            insertQueue.setString(5, input.getFinish());
            insertQueue.setString(6, input.getStartCount());
            insertQueue.setString(7, input.getStartCountUom());
            insertQueue.setString(8, input.getEndCount());
            insertQueue.setString(9, input.getEndCountUom());
            insertQueue.setString(10, input.getGrossDeliver());
            insertQueue.setString(11, input.getGrossDeliverUom());
            insertQueue.setString(12, input.getAvgFlowRate());
            insertQueue.setString(13, input.getAvgFlowRateUom());
            insertQueue.setString(14, input.getAfterAvgFlowRate());
            insertQueue.setString(15, input.getSaleNumber());
            insertQueue.setString(16, input.getMeterNumber());
            insertQueue.setString(17, input.getUnitId());
            insertQueue.setString(18, input.getDuplicate());
            insertQueue.setString(19, config.setFlowmeter());
            insertQueue.setString(20, input.getOtherTwo());
            insertQueue.setString(21, input.getOtherThree());
            insertQueue.setString(22, input.getOtherFour());
            insertQueue.setString(23, input.getOtherFive());
            insertQueue.setString(24, input.getDataState());

            insertQueue.executeUpdate();
            System.out.println("Bind to waiting list");

        } catch (SQLException | NumberFormatException e) {
            System.out.println(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SerialWaitingListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public List<SerialDataQueue> getWaitingList() {
        Connection conn = null;
        List<SerialDataQueue> listData = new ArrayList<>();
        String query = " SELECT `ID`, `uploaded`, `ID_start`, "
                + "`ID_end`, `data_state`, `ticket_no`, `start`, `finish`, `start_count`, `start_count_uom`, "
                + "`end_count`, `end_count_uom`, `gross_deliver`, `gross_deliver_uom`, `avg_flow_rate`, "
                + "`avg_flow_rate_uom`, `after_avg_flow_rate`, `sale_number`, `meter_number`, `unit_id`, "
                + "`duplicate`, `other_one`, `other_two`, `other_three`, `other_four`, `other_five` "
                + "FROM `serial_data_queue` WHERE `uploaded` = 0 ORDER BY ID ASC";

        try {
            conn = DriverManager.getConnection(DBConnectionManager.MYSQL_URL, DBConnectionManager.MYSQL_UNAME, DBConnectionManager.MYSQL_PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                SerialDataQueue item = new SerialDataQueue();
                item.setId(rs.getLong(1));
                item.setUploaded(rs.getShort(2));
                item.setIDstart(rs.getLong(3));
                item.setIDend(rs.getLong(4));
                item.setDataState(rs.getString(5));
                item.setTicketNo(rs.getString(6));
                item.setStart(rs.getString(7));
                item.setFinish(rs.getString(8));
                item.setStartCount(rs.getString(9));
                item.setStartCountUom(rs.getString(10));
                item.setEndCount(rs.getString(11));
                item.setEndCountUom(rs.getString(12));
                item.setGrossDeliver(rs.getString(13));
                item.setGrossDeliverUom(rs.getString(14));
                item.setAvgFlowRate(rs.getString(15));
                item.setAvgFlowRateUom(rs.getString(16));
                item.setAfterAvgFlowRate(rs.getString(17));
                item.setSaleNumber(rs.getString(18));
                item.setMeterNumber(rs.getString(19));
                item.setUnitId(rs.getString(20));
                item.setDuplicate(rs.getString(21));
                item.setOtherOne(rs.getString(22));
                item.setOtherTwo(rs.getString(23));
                item.setOtherThree(rs.getString(24));
                item.setOtherFour(rs.getString(25));
                item.setOtherFive(rs.getString(26));
                listData.add(item);

            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            try {
                conn.close();

            } catch (SQLException ex) {
                Logger.getLogger(SerialWaitingListController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return listData;
    }

    public void bindQueue(List<SerialDataQueue> uploadList) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DBConnectionManager.MYSQL_URL, DBConnectionManager.MYSQL_UNAME, DBConnectionManager.MYSQL_PASSWORD);
            String query = "INSERT INTO `serial_data_uploaded`"
                    + "(`uploaded`, `site_id`, `ID_start`, `ID_end`, `data_state`, `ticket_no`, `start`, `finish`, "
                    + "`start_count`, `start_count_uom`, `end_count`, `end_count_uom`, `gross_deliver`, "
                    + "`gross_deliver_uom`, `avg_flow_rate`, `avg_flow_rate_uom`, `after_avg_flow_rate`, "
                    + "`sale_number`, `meter_number`, `unit_id`, `duplicate`, `other_one`, `other_two`, "
                    + "`other_three`, `other_four`, `other_five`)"
                    + "VALUES (0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            Iterator<SerialDataQueue> it = uploadList.iterator();
            SiteConfiguration config = new SiteConfiguration();
            String siteId = config.gettingSiteName();

            while (it.hasNext()) {

                try {
                    SerialDataQueue serialData = it.next();
                    PreparedStatement insertUpload = conn.prepareStatement(query);
                    Long id = serialData.getId();
                    insertUpload.setString(1, siteId);
                    insertUpload.setLong(2, serialData.getIDstart());
                    insertUpload.setLong(3, serialData.getIDend());
                    insertUpload.setString(4, serialData.getDataState());
                    insertUpload.setString(5, serialData.getTicketNo());
                    insertUpload.setString(6, serialData.getStart());
                    insertUpload.setString(7, serialData.getFinish());
                    insertUpload.setString(8, serialData.getStartCount());
                    insertUpload.setString(9, serialData.getStartCountUom());
                    insertUpload.setString(10, serialData.getEndCount());
                    insertUpload.setString(11, serialData.getEndCountUom());
                    insertUpload.setString(12, serialData.getGrossDeliver());
                    insertUpload.setString(13, serialData.getGrossDeliverUom());
                    insertUpload.setString(14, serialData.getAvgFlowRate());
                    insertUpload.setString(15, serialData.getAvgFlowRateUom());
                    insertUpload.setString(16, serialData.getAfterAvgFlowRate());
                    insertUpload.setString(17, serialData.getSaleNumber());
                    insertUpload.setString(18, serialData.getMeterNumber());
                    insertUpload.setString(19, serialData.getUnitId());
                    insertUpload.setString(20, serialData.getDuplicate());
                    insertUpload.setString(21, serialData.getOtherOne());
                    insertUpload.setString(22, serialData.getOtherTwo());
                    insertUpload.setString(23, serialData.getOtherThree());
                    insertUpload.setString(24, serialData.getOtherFour());
                    insertUpload.setString(25, serialData.getOtherFive());

                    SerialFlagController controller = new SerialFlagController();
                    controller.flagWaitingList(id);
                    insertUpload.addBatch();

                    int[] numUpdates = null;
                    numUpdates = insertUpload.executeBatch();
                    for (int d = 0; d < numUpdates.length; d++) {
                        if (numUpdates[d] == -2) {
                            System.out.println("Execution " + d + ": unknown number of rows queued");

                        } else {
                            System.out.println("\nBind to upload");
                        }
                    }
                } catch (BatchUpdateException e) {
                    System.out.println(e);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SerialWaitingListController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SerialWaitingListController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SerialWaitingListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
