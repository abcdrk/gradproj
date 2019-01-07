package com.empatica.sample.Persistence;

public class Result {
    private String timestamp;
    private Double meanHR;
    private Double stdHR;
    private Double lfHR;
    private Double hfHR;
    private Double lfhfHR;
    private Double meanX;
    private Double meanY;
    private Double meanZ;
    private Double energyAcc;
    private Double meanEDA;
    private Double stdEDA;
    private Double peaksPer;
    private Double meanLight;
    private Double stdLight;
    private Double step;
    private Double call;

    public Result() {

    }

    public Double getMeanHR() {
        return meanHR;
    }

    public void setMeanHR(Double meanHR) {
        this.meanHR = meanHR;
    }

    public Double getStdHR() {
        return stdHR;
    }

    public void setStdHR(Double stdHR) {
        this.stdHR = stdHR;
    }

    public Double getLfHR() {
        return lfHR;
    }

    public void setLfHR(Double lfHR) {
        this.lfHR = lfHR;
    }

    public Double getHfHR() {
        return hfHR;
    }

    public void setHfHR(Double hfHR) {
        this.hfHR = hfHR;
    }

    public Double getLfhfHR() {
        return lfhfHR;
    }

    public void setLfhfHR(Double lfhfHR) {
        this.lfhfHR = lfhfHR;
    }

    public Double getMeanX() {
        return meanX;
    }

    public void setMeanX(Double meanX) {
        this.meanX = meanX;
    }

    public Double getMeanY() {
        return meanY;
    }

    public void setMeanY(Double meanY) {
        this.meanY = meanY;
    }

    public Double getMeanZ() {
        return meanZ;
    }

    public void setMeanZ(Double meanZ) {
        this.meanZ = meanZ;
    }

    public Double getEnergyAcc() {
        return energyAcc;
    }

    public void setEnergyAcc(Double energyAcc) {
        this.energyAcc = energyAcc;
    }

    public Double getMeanEDA() {
        return meanEDA;
    }

    public void setMeanEDA(Double meanEDA) {
        this.meanEDA = meanEDA;
    }

    public Double getStdEDA() {
        return stdEDA;
    }

    public void setStdEDA(Double stdEDA) {
        this.stdEDA = stdEDA;
    }

    public Double getPeaksPer() {
        return peaksPer;
    }

    public void setPeaksPer(Double peaksPer) {
        this.peaksPer = peaksPer;
    }

    public Double getMeanLight() {
        return meanLight;
    }

    public void setMeanLight(Double meanLight) {
        this.meanLight = meanLight;
    }

    public Double getStdLight() {
        return stdLight;
    }

    public void setStdLight(Double stdLight) {
        this.stdLight = stdLight;
    }

    public Double getStep() {
        return step;
    }

    public void setStep(Double step) {
        this.step = step;
    }

    public Double getCall() {
        return call;
    }

    public void setCall(Double call) {
        this.call = call;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
