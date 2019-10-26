/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.bmp.controller;

import id.bmp.configuration.CloudConfiguration;
import id.bmp.configuration.DBConnectionManager;
import id.bmp.configuration.SiteConfiguration;
import id.bmp.model.SerialDataUploaded;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author permadi
 */
public class SerialUploadedController {

    public boolean getWaitingList() throws SQLException {

        boolean isExist = false;

        String queueResults = "SELECT `uploaded`, `site_id`,"
                + " `ID_start`, `ID_end`, `data_state`, `ticket_no`, `start`, `finish`, `start_count`, "
                + "`start_count_uom`, `end_count`, `end_count_uom`, `gross_deliver`, `gross_deliver_uom`, "
                + "`avg_flow_rate`, `avg_flow_rate_uom`, `after_avg_flow_rate`, `sale_number`, `meter_number`, "
                + "`unit_id`, `duplicate`, `other_one`, `other_two`, `other_three`, `other_four`, `other_five` "
                + "FROM `serial_data_uploaded` WHERE `uploaded` = 0 ";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DBConnectionManager.MYSQL_URL, DBConnectionManager.MYSQL_UNAME, DBConnectionManager.MYSQL_PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queueResults);
            while (rs.next()) {
                isExist = true;

            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            try {
                conn.close();

            } catch (SQLException ex) {
                Logger.getLogger(SerialUploadedController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return isExist;
    }

    public void pupulateData() throws SQLException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DBConnectionManager.MYSQL_URL, DBConnectionManager.MYSQL_UNAME, DBConnectionManager.MYSQL_PASSWORD);

            String query = "SELECT `ID`, `uploaded`, `site_id`, `ID_start`, `ID_end`, "
                    + "`data_state`, `ticket_no`, `start`, `finish`, `start_count`, `start_count_uom`, `end_count`, "
                    + "`end_count_uom`, `gross_deliver`, `gross_deliver_uom`, `avg_flow_rate`, `avg_flow_rate_uom`, "
                    + "`after_avg_flow_rate`, `sale_number`, `meter_number`, `unit_id`, `duplicate`, `other_one`, "
                    + "`other_two`, `other_three`, `other_four`, `other_five` "
                    + "FROM `serial_data_uploaded` WHERE `uploaded` = 0";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {

                Long id = rs.getLong("ID");
                SerialDataUploaded serialData = new SerialDataUploaded();
                serialData.setIDstart(rs.getLong("ID_start"));
                serialData.setIDend(rs.getLong("ID_end"));
                serialData.setTicketNo(rs.getString("ticket_no"));
                serialData.setStart(rs.getString("start"));
                serialData.setFinish(rs.getString("finish"));
                serialData.setStartCount(rs.getString("start_count"));
                serialData.setStartCountUom(rs.getString("start_count_uom"));
                serialData.setEndCount(rs.getString("end_count"));
                serialData.setEndCountUom(rs.getString("end_count_uom"));
                serialData.setGrossDeliver(rs.getString("gross_deliver"));
                serialData.setGrossDeliverUom(rs.getString("gross_deliver_uom"));
                serialData.setAvgFlowRate(rs.getString("avg_flow_rate"));
                serialData.setAvgFlowRateUom(rs.getString("avg_flow_rate_uom"));
                serialData.setSaleNumber(rs.getString("sale_number"));
                serialData.setMeterNumber(rs.getString("meter_number"));
                serialData.setUnitId(rs.getString("unit_id"));
                serialData.setDuplicate(rs.getString("duplicate"));
                serialData.setOtherOne(rs.getString("other_one"));
                serialData.setOtherTwo(rs.getString("other_two"));
                serialData.setOtherThree(rs.getString("other_three"));
                serialData.setOtherFour(rs.getString("other_four"));
                serialData.setOtherFive(rs.getString("other_five"));
                serialData.setDataState(rs.getString("data_state"));

                try {
                    bindUpload(id, serialData);
                } catch (IOException ex) {
                    Logger.getLogger(SerialUploadedController.class.getName()).log(Level.SEVERE, null, ex);
                }
                SerialFlagController controller = new SerialFlagController();
                controller.flagUploaded(id);
            }
            conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(SerialUploadedController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void bindUpload(Long id, SerialDataUploaded input) throws IOException, SQLException {
        String line = "";
        StringBuilder sb = new StringBuilder();
        CloudConfiguration config = new CloudConfiguration();
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://" + config.setIpAddr() + "/serial.php");
        SiteConfiguration site = new SiteConfiguration();
        String siteId = site.gettingSiteName();

        try {

            List<NameValuePair> nameValuePairs = new ArrayList<>(1);
            nameValuePairs.add(new BasicNameValuePair("site-id", siteId));
            nameValuePairs.add(new BasicNameValuePair("start-id", input.getIDstart().toString()));
            nameValuePairs.add(new BasicNameValuePair("end-id", input.getIDend().toString()));
            nameValuePairs.add(new BasicNameValuePair("ticket-no", input.getTicketNo()));
            nameValuePairs.add(new BasicNameValuePair("start", input.getStart()));
            nameValuePairs.add(new BasicNameValuePair("finish", input.getFinish()));
            nameValuePairs.add(new BasicNameValuePair("start-count", input.getStartCount()));
            nameValuePairs.add(new BasicNameValuePair("start-count-uom", input.getStartCountUom()));
            nameValuePairs.add(new BasicNameValuePair("end-count", input.getEndCount()));
            nameValuePairs.add(new BasicNameValuePair("end-count-uom", input.getEndCountUom()));
            nameValuePairs.add(new BasicNameValuePair("gross-deliver", input.getGrossDeliver()));
            nameValuePairs.add(new BasicNameValuePair("gross-deliver-uom", input.getGrossDeliverUom()));
            nameValuePairs.add(new BasicNameValuePair("avg-flow-rate", input.getAvgFlowRate()));
            nameValuePairs.add(new BasicNameValuePair("avg-flow-rate-uom", input.getAvgFlowRateUom()));
            nameValuePairs.add(new BasicNameValuePair("after-avg-flow-rate", input.getAfterAvgFlowRate()));
            nameValuePairs.add(new BasicNameValuePair("sale-number", input.getSaleNumber()));
            nameValuePairs.add(new BasicNameValuePair("meter-number", input.getMeterNumber()));
            nameValuePairs.add(new BasicNameValuePair("unit-id", input.getUnitId()));
            nameValuePairs.add(new BasicNameValuePair("duplicate", input.getDuplicate()));
            nameValuePairs.add(new BasicNameValuePair("other-one", input.getOtherOne()));
            nameValuePairs.add(new BasicNameValuePair("other-two", input.getOtherTwo()));
            nameValuePairs.add(new BasicNameValuePair("other-three", input.getOtherThree()));
            nameValuePairs.add(new BasicNameValuePair("other-four", input.getOtherFour()));
            nameValuePairs.add(new BasicNameValuePair("other-five", input.getOtherFive()));
            nameValuePairs.add(new BasicNameValuePair("data-state", input.getDataState()));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(post);
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                while ((line = rd.readLine()) != null) {
                    System.out.println("\n" + line);
                    sb.append(line);

                }
                int a = response.getStatusLine().getStatusCode();
                System.out.println("--> " + a);
                post.releaseConnection();

            }
            client.close();

        } catch (IOException | PatternSyntaxException e) {
            System.out.println("No internet present !!!");
        } finally {
            if (sb.toString().trim().equalsIgnoreCase("SUCCESS")) {
                SerialFlagController controller = new SerialFlagController();
                controller.flagUploaded(id);
            }
        }
    }

}
