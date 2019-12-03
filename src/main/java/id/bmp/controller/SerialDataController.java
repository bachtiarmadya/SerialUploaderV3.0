/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.bmp.controller;

import id.bmp.configuration.DBConnectionManager;
import id.bmp.model.SerialData;
import id.bmp.model.SerialFake;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author permadi
 */
public class SerialDataController {

    private Connection conn;

    public void read() throws IOException, SQLException {

        conn = DriverManager.getConnection(DBConnectionManager.MYSQL_URL, DBConnectionManager.MYSQL_UNAME, DBConnectionManager.MYSQL_PASSWORD);

        SerialData serialData = new SerialData();
        SerialFake serialParser = new SerialFake();
        boolean firstFound = false;
        StringBuilder ids = new StringBuilder();
        StringBuilder message = new StringBuilder();
        String query = "SELECT ID, `data`, created_date, downloaded FROM serial_data WHERE `downloaded` = 0;";

        ArrayList<SerialData> serial = new ArrayList<>();
        Long avgId = 0l;
        Long ticketId = 0l;

        Statement stmt;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String msByte = new String(rs.getBytes("data"));

                if (msByte.indexOf("TICKET NUMBER") > 0) {
                    String ticketNumber = msByte.substring(msByte.indexOf("TICKET NUMBER") + 13, msByte.length()).trim();
                    if (!firstFound) {
                        firstFound = true;
                    } else {
                        if (ids.length() > 0) {
                            serialData.setIds(ids.toString());
                            serialData.setFullMessage(message.toString());
                            serial.add(serialData);

                            //reset serialData and gangs
                            ids.setLength(0);
                            message.setLength(0);
                            serialData = null;
                            serialData = new SerialData();
                        }
                    }
                    ticketId = rs.getLong("ID");
                    try {
                        Long i = Long.valueOf(ticketNumber);

                        serialData.setTicketNumber(ticketNumber);
                        serialData.setDataState("NORMAL");
                        serialParser.setTicketNo(ticketNumber);
                    } catch (NumberFormatException nfe) {
                        serialData.setDataState("ABNORMAL");
                    }
                }

                if (msByte.indexOf(
                        "LOADING DOCKER") > 0) {
                    serialParser.setDocker(true);
                }

                if (msByte.indexOf(
                        "PT.PERTAMINA PATRA NIAGA") > 0) {
                    serialParser.setCorporate("PT.PERTAMINA PATRA NIAGA");
                }

                if (msByte.indexOf(
                        "VHS KAI") > 0) {
                    serialParser.setVhs("VHS KAI");
                }

                if (msByte.indexOf(
                        "START") > 0) {

                    if (msByte.indexOf("START COUNT") > 0) {

                        String start = msByte.substring(msByte.indexOf("START COUNT") + 11, msByte.length()).trim();
                        String[] s = start.split("\\s+");
                        if (2 == s.length) {
                            serialData.setStartCount(s[0].trim());
                            serialData.setStartCountUom(s[1].trim());
                            serialParser.setStartCount(start);
                        } else {
                            serialData.setDataState("ABNORMAL");
                        }
                    } else if (msByte.indexOf("SHIFT START") > 0) {
                        String shiftStart = msByte.substring(msByte.indexOf("SHIFT START") + 11, msByte.length()).trim();
                        serialParser.setShiftStart(shiftStart);

                    } else if (msByte.indexOf("START") > 0) {

                        String start = msByte.substring(msByte.indexOf("START") + 5, msByte.length()).trim();
                        serialData.setStart(start);
                        serialParser.setStart(start);
                    }

                }

                if (msByte.indexOf(
                        "FINISH") > 0) {
                    if (msByte.indexOf("SHIFT FINISH") > 0) {
                        String shuftFinish = msByte.substring(msByte.indexOf("SHIFT FINISH") + 12, msByte.length()).trim();
                        serialParser.setShiftFinish(shuftFinish);
                    } else if (msByte.indexOf("FINISH") > 0) {

                        String finish = msByte.substring(msByte.indexOf("FINISH") + 6, msByte.length()).trim();
                        serialData.setFinish(finish);
                        serialParser.setFinish(finish);
                    }
                }

                if (msByte.indexOf(
                        "END COUNT") > 0) {
                    String end = msByte.substring(msByte.indexOf("END COUNT") + 9, msByte.length()).trim();
                    String[] s = end.split("\\s+");
                    if (2 == s.length) {
                        serialData.setEndCount(s[0].trim());
                        serialData.setEndCountUom(s[1].trim());
                        serialParser.setEndCount(s[0].trim());
                    } else {
                        serialData.setDataState("ABNORMAL");
                    }
                }

                if (msByte.indexOf(
                        "GROSS DELIVER") > 0) {
                    String item = msByte.substring(msByte.indexOf("GROSS DELIVER") + 13, msByte.length()).trim();
                    String[] s = item.split("\\s+");
                    if (2 == s.length) {
                        serialData.setGrossDeliver(s[0].trim());
                        serialData.setGrossDeliverUom(s[1].trim());
                        serialParser.setGrossDeliver(s[0].trim());
                    } else {
                        serialData.setDataState("ABNORMAL");
                    }
                }

                if (msByte.indexOf(
                        "AVG FLOW RATE") > 0) {
                    avgId = rs.getLong("ID");
                    String sdata = msByte.substring(msByte.indexOf("AVG FLOW RATE") + 13, msByte.length()).trim();
                    String[] s = sdata.split("\\s+");
                    if (2 == s.length) {
                        serialData.setAvgFlowRate(s[0].trim());
                        serialData.setAvgFlowRateUom(s[1].trim());
                        serialParser.setAvgFlowRate(s[0].trim());
                    } else {
                        serialData.setDataState("ABNORMAL");
                    }
                }

                if (msByte.indexOf(
                        "SALE NUMBER") > 0) {
                    String sData = msByte.substring(msByte.indexOf("SALE NUMBER") + 11, msByte.length()).trim();
                    try {
                        Long i = Long.valueOf(sData);
                        serialData.setSaleNumber(sData);
                        serialParser.setSaleNumber(sData);
                    } catch (NumberFormatException nfe) {
                        serialData.setDataState("ABNORMAL");
                    }
                }

                if (msByte.indexOf(
                        "METER NUMBER") > 0) {
                    String sData = msByte.substring(msByte.indexOf("METER NUMBER") + 12, msByte.length()).trim();
                    try {
                        Long i = Long.valueOf(sData);
                        serialData.setMeterNumber(sData);
                        serialParser.setMeterNumber(sData);
                    } catch (NumberFormatException nfe) {

                        serialData.setDataState("ABNORMAL");
                    }
                }

                if (msByte.indexOf(
                        "UNIT ID") > 0) {
                    String sData = msByte.substring(msByte.indexOf("UNIT ID") + 7, msByte.length()).trim();
                    if (!sData.equalsIgnoreCase("")) {
                        serialData.setUnitId(sData);
                        serialParser.setUnitId(sData);
                    } else {
                        serialData.setDataState("ABNORMAL");
                    }
                }

                if (msByte.indexOf("CALIBRATION NUMBER") > 0) {
                    String sData = msByte.substring(msByte.indexOf("CALIBRATION NUMBER") + 18, msByte.length()).trim();
                    serialParser.setCalibrationNumber(sData);
                }

                if (!msByte.contains("CALIBRATION NUMBER")) {
                    serialParser.setCalibrationNumber(" ");
                }

                if (msByte.indexOf("SHIFT NET") > 0) {
                    String sData = msByte.substring(msByte.indexOf("SHIFT NET") + 9, msByte.length()).trim();
                    serialParser.setShiftNet(sData);

                }

                if (!msByte.contains("SHIFT NET")) {

                    serialParser.setShiftNet(" ");
                }

                if (msByte.indexOf("SHIFT GROSS") > 0) {
                    String sData = msByte.substring(msByte.indexOf("SHIFT GROSS") + 11, msByte.length()).trim();
                    serialParser.setShiftGross(sData);

                }

                if (!msByte.contains("SHIFT GROSS")) {

                    serialParser.setShiftGross(" ");
                }

                if (msByte.indexOf("END NET TOTAL") > 0) {
                    String sData = msByte.substring(msByte.indexOf("END NET TOTAL") + 13, msByte.length()).trim();
                    serialParser.setEndNetTotal(sData);

                }

                if (!msByte.contains("END NET TOTAL")) {

                    serialParser.setEndNetTotal(" ");
                }

                if (msByte.indexOf("END TOTALIZER") > 0) {
                    String sData = msByte.substring(msByte.indexOf("END TOTALIZER") + 13, msByte.length()).trim();
                    serialParser.setEndTotalizer(sData);

                }

                if (!msByte.contains("END TOTALIZER")) {

                    serialParser.setEndTotalizer(" ");
                }

                if (msByte.indexOf("DELIVERIES") > 0) {
                    String sData = msByte.substring(msByte.indexOf("DELIVERIES") + 10, msByte.length()).trim();
                    serialParser.setDeliveries(sData);

                }

                if (!msByte.contains("DELIVERIES")) {

                    serialParser.setDeliveries(" ");
                }
                if ((ticketId
                        + 1) == rs.getLong(
                                "ID") && (rs.getLong("ID") > 1)) {

                    String sData = msByte.substring(4, msByte.length()).trim();

                    if ((msByte.contains("VHS")) && (msByte.length() <= 44)) {
                        serialData.setOtherOne(sData);
                    } else {
                        serialData.setDataState("ABNORMAL");
                    }
                }

                if ((ticketId
                        + 2) == rs.getLong(
                                "ID") && (rs.getLong("ID") > 1)) {
                    String sData = msByte.substring(4, msByte.length()).trim();
                    if ((msByte.indexOf("DIPOLOK")) >= 0 && (msByte.length() <= 44)) {
                        serialData.setOtherTwo(sData);
                    } else {
                        serialData.setDataState("ABNORMAL");
                    }
                }

                if ((avgId
                        + 1) == rs.getLong(
                                "ID") && (rs.getLong("ID") > 1)) {
                    String sData = msByte.substring(4, msByte.length()).trim();
                    try {
                        Integer i = Integer.valueOf(sData);
                        serialData.setAfterAvgFlowRate(sData);
                    } catch (NumberFormatException nfe) {
                        serialData.setDataState("ABNORMAL");
                    }
                    avgId = 0l;
                }

                if (msByte.indexOf(
                        "DUPLICATE TICKET") > 0) {
                    serialData.setDuplicate("DUPLICATE TICKET");
                    serialParser.setDeliveries("DUPLICATE TICKET");
                }
                if (!msByte.contains("DUPLICATE TICKET")) {
                    serialParser.setDeliveries(" ");
                }

                if (serialData.getDataState()
                        == null || serialData.getDataState() == "") {
                    serialData.setDataState("ABNORMAL");
                }
                ids.append(rs.getInt("ID"));
                ids.append(
                        ",");
                message.append(msByte);

            }
            if (ids.length() > 0) {

                LocalDateTime dtNow = LocalDateTime.now();
                DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
                String formatDateTime = dtNow.format(format);
                if (serialData.getFinish() == null) {
                    serialData.setFinish(formatDateTime);
                }
                serialData.setIds(ids.toString());
                serialData.setFullMessage(message.toString());
                serial.add(serialData);
                System.out.println("\nReading ...");
            }

            for (int i = 0; i < serial.size(); i++) {

                String[] dd = serial.get(i).getIds().replaceAll("^[,\\s]+", "").split("[,\\s]+");

                Long starts = Long.valueOf(dd[0]);
                Long ends = Long.valueOf(dd[dd.length - 1]);
                serial.get(i).setStartId(starts);
                serial.get(i).setEndId(ends);
                Long idStart = serial.get(i).getStartId();
                Long idEnd = serial.get(i).getEndId();
                SerialWaitingListController controller = new SerialWaitingListController();

                controller.setToWaitingList(serial.get(i));
                SerialFlagController flagController = new SerialFlagController();
                flagController.flagDownloaded(idStart, idEnd);

            }

        } catch (SQLException ex) {
            Logger.getLogger(SerialDataController.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
