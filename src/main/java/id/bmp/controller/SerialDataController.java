/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.bmp.controller;

import id.bmp.configuration.DBConnectionManager;
import id.bmp.model.SerialData;
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

                String startDt = serialData.getStart();
                String endDt = serialData.getFinish();
                String gross = serialData.getGrossDeliver();
                String saleNumber = serialData.getSaleNumber();
                String meterNumber = serialData.getMeterNumber();
                String unitId = serialData.getUnitId();

                if (msByte.indexOf("TICKET NUMBER") > 0) {
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

                    String ticketNumber = msByte.substring(msByte.indexOf("TICKET NUMBER") + 13, msByte.length()).trim();
                    ticketId = rs.getLong("ID");
                    try {
                        Long i = Long.valueOf(ticketNumber);
                        serialData.setTicketNumber(ticketNumber);
                        serialData.setDataState("NORMAL");
                    } catch (NumberFormatException nfe) {
                        serialData.setDataState("ABNORMAL");
                    }
                }
                if ((ticketId + 1) == rs.getLong("ID") && (rs.getLong("ID") > 1)) {

                    String sData = msByte.substring(4, msByte.length()).trim();

                    if ((msByte.contains("VHS")) && (msByte.length() <= 44)) {
                        serialData.setOtherOne(sData);
                    } else {
                        serialData.setDataState("ABNORMAL");
                    }
                }

                if ((ticketId + 2) == rs.getLong("ID") && (rs.getLong("ID") > 1)) {
                    String sData = msByte.substring(4, msByte.length()).trim();
                    if ((msByte.indexOf("DIPOLOK")) >= 0 && (msByte.length() <= 44)) {
                        serialData.setOtherTwo(sData);
                    } else {
                        serialData.setDataState("ABNORMAL");
                    }
                }
                if (startDt == null) {
                    if (msByte.indexOf("START") > 0) {

                        if (msByte.indexOf("START COUNT") > 0) {

                            String start = msByte.substring(msByte.indexOf("START COUNT") + 11, msByte.length()).trim();
                            String[] s = start.split("\\s+");
                            if (2 == s.length) {
                                serialData.setStartCount(s[0].trim());
                                serialData.setStartCountUom(s[1].trim());
                            } else {
                                serialData.setDataState("ABNORMAL");
                            }
                        } else if (msByte.indexOf("SHIFT START") > 0) {
                        } else if (msByte.indexOf("START") > 0) {

                            String start = msByte.substring(msByte.indexOf("START") + 5, msByte.length()).trim();

                            serialData.setStart(start);
                        }

                    }
                }
                if (endDt == null) {
                    if (msByte.indexOf("FINISH") > 0) {
                        if (msByte.indexOf("SHIFT FINISH") > 0) {

                        } else if (msByte.indexOf("FINISH") > 0) {

                            String finish = msByte.substring(msByte.indexOf("FINISH") + 6, msByte.length()).trim();

                            serialData.setFinish(finish);

                        }
                    }
                }

                if (msByte.indexOf("END COUNT") > 0) {
                    String end = msByte.substring(msByte.indexOf("END COUNT") + 9, msByte.length()).trim();
                    String[] s = end.split("\\s+");
                    if (2 == s.length) {
                        serialData.setEndCount(s[0].trim());
                        serialData.setEndCountUom(s[1].trim());
                    } else {
                        serialData.setDataState("ABNORMAL");
                    }
                }
                if (gross == null) {
                    if (msByte.indexOf("GROSS DELIVER") > 0) {
                        String item = msByte.substring(msByte.indexOf("GROSS DELIVER") + 13, msByte.length()).trim();
                        String[] s = item.split("\\s+");
                        if (2 == s.length) {
                            serialData.setGrossDeliver(s[0].trim());
                            serialData.setGrossDeliverUom(s[1].trim());
                        } else {
                            serialData.setDataState("ABNORMAL");
                        }
                    }
                }
                if ((avgId + 1) == rs.getLong("ID") && (rs.getLong("ID") > 1)) {
                    String sData = msByte.substring(4, msByte.length()).trim();
                    try {
                        Integer i = Integer.valueOf(sData);
                        serialData.setAfterAvgFlowRate(sData);
                    } catch (NumberFormatException nfe) {
                        serialData.setDataState("ABNORMAL");
                    }
                    avgId = 0l;
                }
                if (msByte.indexOf("AVG FLOW RATE") > 0) {
                    avgId = rs.getLong("ID");
                    String sdata = msByte.substring(msByte.indexOf("AVG FLOW RATE") + 13, msByte.length()).trim();
                    String[] s = sdata.split("\\s+");
                    if (2 == s.length) {
                        serialData.setAvgFlowRate(s[0].trim());
                        serialData.setAvgFlowRateUom(s[1].trim());
                    } else {
                        serialData.setDataState("ABNORMAL");
                    }
                }
                if (saleNumber == null) {
                    if (msByte.indexOf("SALE NUMBER") > 0) {
                        String sData = msByte.substring(msByte.indexOf("SALE NUMBER") + 11, msByte.length()).trim();
                        try {
                            Long i = Long.valueOf(sData);
                            serialData.setSaleNumber(sData);
                        } catch (NumberFormatException nfe) {
                            serialData.setDataState("ABNORMAL");
                        }
                    }
                }
                if (meterNumber == null) {
                    if (msByte.indexOf("METER NUMBER") > 0) {
                        String sData = msByte.substring(msByte.indexOf("METER NUMBER") + 12, msByte.length()).trim();
                        try {
                            Long i = Long.valueOf(sData);
                            serialData.setMeterNumber(sData);
                        } catch (NumberFormatException nfe) {

                            serialData.setDataState("ABNORMAL");
                        }
                    }
                }
                if (unitId == null) {
                    if (msByte.indexOf("UNIT ID") > 0) {
                        String sData = msByte.substring(msByte.indexOf("UNIT ID") + 7, msByte.length()).trim();
                        if (!sData.equalsIgnoreCase("")) {
                            serialData.setUnitId(sData);
                        } else {
                            serialData.setDataState("ABNORMAL");
                        }
                    }
                }
                if (msByte.indexOf("DUPLICATE TICKET") > 0) {
                    serialData.setDuplicate("DUPLICATE TICKET");
                }
                if (serialData.getDataState() == null || serialData.getDataState() == "") {
                    serialData.setDataState("ABNORMAL");
                }
                ids.append(rs.getInt("ID"));
                ids.append(",");
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

                Long start = Long.valueOf(dd[0]);
                Long end = Long.valueOf(dd[dd.length - 1]);
                serial.get(i).setStartId(start);
                serial.get(i).setEndId(end);
                SerialWaitingListController controller = new SerialWaitingListController();

                String startDt = serialData.getStart();
                String endDt = serialData.getFinish();
                String gross = serialData.getGrossDeliver();
                String saleNumber = serialData.getSaleNumber();
                String meterNumber = serialData.getMeterNumber();
                String unitId = serialData.getUnitId();
                if (startDt != null && endDt != null && gross != null && saleNumber != null && meterNumber != null && unitId != null) {
                    controller.setToWaitingList(serialData);
                    SerialFlagController flagController = new SerialFlagController();
                    flagController.flagDownloaded(serialData.getStartId(), serialData.getEndId());
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(SerialDataController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
