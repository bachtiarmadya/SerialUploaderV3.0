/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.bmp.app;

import id.bmp.configuration.CTSController;
import id.bmp.configuration.CloudConfiguration;
import id.bmp.controller.SerialDataController;
import id.bmp.controller.SerialUploadedController;
import id.bmp.controller.SerialWaitingListController;
import id.bmp.model.SerialDataQueue;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author permadi
 */
public class Runner implements Runnable {

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = new Runner();

        task.run();
        int initialDelay = 4;
        int periodicDelay = 10;

        scheduler.scheduleAtFixedRate(task, initialDelay, periodicDelay,
                TimeUnit.SECONDS);
    }

    @Override
    public void run() {

        try {
            read();
        } catch (IOException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void read() throws IOException, SQLException {
        SerialDataController reader = new SerialDataController();
        SerialWaitingListController queueController = new SerialWaitingListController();
        SerialUploadedController uploadController = new SerialUploadedController();
        CloudConfiguration config = new CloudConfiguration();
        List<SerialDataQueue> list = queueController.getWaitingList();
        if (!list.isEmpty()) {
            queueController.bindQueue(list);
        }
        try {
            boolean isReachable = sendPing(config.setIpAddr());
            if (isReachable) {
                boolean listOfUpload = uploadController.getWaitingList();
                if (listOfUpload) {
                    uploadController.pupulateData();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        }
        reader.read();
        for (double progressPercentage = 0.0; progressPercentage < 1.0; progressPercentage += 0.01) {
            try {
                runProgress(progressPercentage);

                Thread.sleep(100);
            } catch (IOException ex) {
                Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private static void runProgress(double progressPercentage) throws IOException {
        final int width = 10; // progress bar width in chars

        System.out.print("\r[");
        int i = 0;
        for (; i <= (int) (progressPercentage * width); i++) {
            System.out.print(".");
        }
        for (; i < width; i++) {
            System.out.print(" ");
        }
        System.out.print("]");
    }

    private static boolean sendPing(String ipAddress) throws UnknownHostException, IOException {
        boolean isReachable = false;
        InetAddress geek = InetAddress.getByName(ipAddress);
        if (geek.isReachable(5000)) {
            isReachable = true;

        }
        return isReachable;
    }

}
