package com.empatica.sample.Persistence;

import android.util.Log;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;

public class Result {
    public static final double FREQUENCY_THRESHOLD = 0.15;
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

    public void calculateHR(ArrayList<SensorData> ibis) {
        double[] hrArr = dataListToArray(ibis);
        this.setMeanHR(StatUtils.mean(hrArr));
        this.setStdHR(FastMath.sqrt(StatUtils.variance(hrArr)));
        double[] hrArr32 = new double[32];
        for (int i = 0; i < 32; i++) {
            hrArr32[i] = 0;
        }
        for (int i = 0; i < Math.min(hrArr.length, 32); i++) {
            hrArr32[i] = hrArr[i];
        }

        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] complexes = transformer.transform(hrArr32, TransformType.FORWARD);
        double lfCount = 0;
        double hfCount = 0;

        for (int i = 0; i < complexes.length; i++) {
//            Log.d("IBI", String.format("Complex Number: %f + %fi abs: %f", complexes[i].getReal(), complexes[i].getImaginary(), complexes[i].abs()));
            if (Math.abs(complexes[i].getImaginary()) < FREQUENCY_THRESHOLD) {
                lfCount++;
            } else {
                hfCount++;
            }
        }
        this.setHfHR(hfCount);
        this.setLfHR(lfCount);
        if (hfCount != 0) {
            this.setLfhfHR(lfCount / hfCount);
        }
    }

    public void calculateAcc(ArrayList<SensorData> xs, ArrayList<SensorData> ys, ArrayList<SensorData> zs) {
        double[] xArr = dataListToArray(xs);
        this.setMeanX(StatUtils.mean(xArr));

        double[] yArr = dataListToArray(ys);
        this.setMeanY(StatUtils.mean(yArr));

        double[] zArr = dataListToArray(zs);
        this.setMeanZ(StatUtils.mean(zArr));

    }

    public void calculateEDA(ArrayList<SensorData> edas) {
        double[] edaArr = dataListToArray(edas);
        this.setMeanEDA(StatUtils.mean(edaArr));
        this.setStdEDA(FastMath.sqrt(StatUtils.variance(edaArr)));
    }

    public void calculateLight(ArrayList<SensorData> lights) {
        double[] lightArr = dataListToArray(lights);
        this.setMeanLight(StatUtils.mean(lightArr));
        this.setStdLight(FastMath.sqrt(StatUtils.variance(lightArr)));
    }

    public double[] dataListToArray(ArrayList<SensorData> list) {
        double[] arr = new double[list.size()];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i).getValue();
        }
        return arr;
    }

}
