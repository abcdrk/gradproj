package com.empatica.sample.Persistence;

import android.util.Log;
import android.widget.Toast;

import com.empatica.sample.MainActivity;

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    public double calculateHR(ArrayList<SensorData> ibis) {
        double[] hrArr = dataListToArray(ibis);

        double meanHR = StatUtils.mean(hrArr);
        this.setMeanHR(meanHR);

        this.setStdHR(FastMath.sqrt(StatUtils.variance(hrArr)));


        /**
         * Fourier Transform Operations START
         * Not used at the moment.
         */
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
        /**
         * Fourier Transform Operations END
         */

        return meanHR;
    }

    public double calculateAcc(ArrayList<SensorData> xs, ArrayList<SensorData> ys, ArrayList<SensorData> zs) {
        double[] xArr = dataListToArray(xs);
        double[] yArr = dataListToArray(ys);
        double[] zArr = dataListToArray(zs);

        int length = Math.min(Math.min(xArr.length, yArr.length), zArr.length);

        ArrayList<Double> xSqrArr = new ArrayList<>();
        ArrayList<Double> ySqrArr = new ArrayList<>();
        ArrayList<Double> zSqrArr = new ArrayList<>();

        for (double x : xArr) {
            xSqrArr.add(x * x);
        }

        for (double y : yArr) {
            ySqrArr.add(y * y);
        }

        for (double z : zArr) {
            zSqrArr.add(z * z);
        }

        double[] pointMagnitude = new double[length];

        for (int i = 0 ; i < length ; i++) {
            pointMagnitude[i] = Math.sqrt(xSqrArr.get(i) + ySqrArr.get(i) + zSqrArr.get(i));
        }

        double energy = StatUtils.mean(pointMagnitude);

        // The MAGNITUDE = ENERGY
        this.setEnergyAcc(energy);
        this.setMeanX(StatUtils.mean(xArr));
        this.setMeanY(StatUtils.mean(yArr));
        this.setMeanZ(StatUtils.mean(zArr));

        return energy;
    }


    public void calculateEDA(ArrayList<SensorData> edas) {
        double[] edaArr = dataListToArray(edas);
        this.setMeanEDA(StatUtils.mean(edaArr));
        this.setStdEDA(FastMath.sqrt(StatUtils.variance(edaArr)));
    }


    public double[] dataListToArray(ArrayList<SensorData> list) {
        double[] arr = new double[list.size()];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i).getValue();
        }
        return arr;
    }

}
