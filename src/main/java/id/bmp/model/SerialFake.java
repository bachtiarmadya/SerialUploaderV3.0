/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.bmp.model;

/**
 *
 * @author permadi
 */
public class SerialFake {

    private String ticketNo;
    private boolean docker;
    private String corporate;
    private String vhs;
    private String start;
    private String finish;
    private String startCount;
    private String endCount;
    private String grossDeliver;
    private String avgFlowRate;
    private String saleNumber;
    private String meterNumber;
    private String unitId;
    private String shiftStart;
    private String shiftFinish;
    private String calibrationNumber;
    private String shiftNet;
    private String shiftGross;
    private String endNetTotal;
    private String endTotalizer;
    private String deliveries;
    private String duplicate;

    public SerialFake() {
    }

    public SerialFake(String ticketNo, boolean docker, String corporate, String vhs, String start, String finish, String startCount, String endCount, String grossDeliver, String avgFlowRate, String saleNumber, String meterNumber, String unitId, String shiftStart, String shiftFinish, String calibrationNumber, String shiftNet, String shiftGross, String endNetTotal, String endTotalizer, String deliveries, String duplicate) {
        this.ticketNo = ticketNo;
        this.docker = docker;
        this.corporate = corporate;
        this.vhs = vhs;
        this.start = start;
        this.finish = finish;
        this.startCount = startCount;
        this.endCount = endCount;
        this.grossDeliver = grossDeliver;
        this.avgFlowRate = avgFlowRate;
        this.saleNumber = saleNumber;
        this.meterNumber = meterNumber;
        this.unitId = unitId;
        this.shiftStart = shiftStart;
        this.shiftFinish = shiftFinish;
        this.calibrationNumber = calibrationNumber;
        this.shiftNet = shiftNet;
        this.shiftGross = shiftGross;
        this.endNetTotal = endNetTotal;
        this.endTotalizer = endTotalizer;
        this.deliveries = deliveries;
        this.duplicate = duplicate;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public boolean isDocker() {
        return docker;
    }

    public void setDocker(boolean docker) {
        this.docker = docker;
    }

    public String getCorporate() {
        return corporate;
    }

    public void setCorporate(String corporate) {
        this.corporate = corporate;
    }

    public String getVhs() {
        return vhs;
    }

    public void setVhs(String vhs) {
        this.vhs = vhs;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getStartCount() {
        return startCount;
    }

    public void setStartCount(String startCount) {
        this.startCount = startCount;
    }

    public String getEndCount() {
        return endCount;
    }

    public void setEndCount(String endCount) {
        this.endCount = endCount;
    }

    public String getGrossDeliver() {
        return grossDeliver;
    }

    public void setGrossDeliver(String grossDeliver) {
        this.grossDeliver = grossDeliver;
    }

    public String getAvgFlowRate() {
        return avgFlowRate;
    }

    public void setAvgFlowRate(String avgFlowRate) {
        this.avgFlowRate = avgFlowRate;
    }

    public String getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getShiftStart() {
        return shiftStart;
    }

    public void setShiftStart(String shiftStart) {
        this.shiftStart = shiftStart;
    }

    public String getShiftFinish() {
        return shiftFinish;
    }

    public void setShiftFinish(String shiftFinish) {
        this.shiftFinish = shiftFinish;
    }

    public String getCalibrationNumber() {
        return calibrationNumber;
    }

    public void setCalibrationNumber(String calibrationNumber) {
        this.calibrationNumber = calibrationNumber;
    }

    public String getShiftNet() {
        return shiftNet;
    }

    public void setShiftNet(String shiftNet) {
        this.shiftNet = shiftNet;
    }

    public String getShiftGross() {
        return shiftGross;
    }

    public void setShiftGross(String shiftGross) {
        this.shiftGross = shiftGross;
    }

    public String getEndNetTotal() {
        return endNetTotal;
    }

    public void setEndNetTotal(String endNetTotal) {
        this.endNetTotal = endNetTotal;
    }

    public String getEndTotalizer() {
        return endTotalizer;
    }

    public void setEndTotalizer(String endTotalizer) {
        this.endTotalizer = endTotalizer;
    }

    public String getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(String deliveries) {
        this.deliveries = deliveries;
    }

    public String getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(String duplicate) {
        this.duplicate = duplicate;
    }

    
}
