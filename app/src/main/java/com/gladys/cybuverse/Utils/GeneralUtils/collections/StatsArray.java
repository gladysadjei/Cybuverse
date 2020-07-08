package com.gladys.cybuverse.Utils.GeneralUtils.collections;

import com.gladys.cybuverse.Utils.GeneralUtils.Funcs;

import java.util.List;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.SortedNumArray;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.range;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.shuffle;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.toDouble;

public class StatsArray extends MasterList<Double> {

    public StatsArray() {
    }

    StatsArray(double... numbers) {
        assign(numbers);
    }

    StatsArray(List<Double> numbers) {
        assign(numbers);
    }

    StatsArray(MasterList<Double> numbers) {
        assign(numbers);
    }

    public StatsArray(Double[] numbers) {
        assign(numbers);
    }

    public void assign(double... numbers) {
        super.clear();
        for (Double i : numbers) {
            append(i);
        }
    }

    private Double[] getSorted() {
        return new SortedNumArray(toDouble(super.toArray())).toDouble();
    }

    public Object[] getSortedArray() {
        return getSorted();
    }

    public Object[] getSet() {
        return super.asSet().toArray();
    }

    public Object[] getSortedSet() {
        return new StatsArray(getSorted()).asSet().toArray();
    }

    public Object getLCM() {
        return Funcs.getLCM(toDouble(super.asSet().toArray()));
    }

    public Object getHCF() {
        return Funcs.getHCF(toDouble(super.asSet().toArray()));
    }

    public Object[] getShuffle() {
        return shuffle(toDouble(toArray()));
    }

    public Object[] getSample(int end_index) {
        return super.sample(end_index).toArray();
    }

    public Object[] getSample(int start_index, int end_index) {
        return super.sample(start_index, end_index).toArray();
    }

    public Object[] getRandomSample(int size) {
        return super.randsample(size).toArray();
    }

    public Object[][] getRandomSample(int size, int groups) {
        return Funcs.randsample(toDouble(toArray()), size, groups);
    }


    public double getMean() {
        return getSum() / super.size();
    }

    public double getMax() {
        return getSorted()[size() - 1];
    }

    public double getMin() {
        return getSorted()[0];
    }

    public double getSum() {
        double total = 0.0;
        for (int i : range(super.size())) {
            total += super.pop(i);
        }
        return total;
    }

    public double getMedian() {
        double mMedian = 0.0;
        if (size() > 1) {
            if (size() % 2 == 0) {
                mMedian = (getSorted()[(size() / 2) - 1] + getSorted()[(size() / 2)]) / 2;
            } else {
                mMedian = getSorted()[((size() - 1) / 2)];
            }
        } else {
            if (size() == 1) {
                mMedian = super.pop(0);
            }
        }
        return mMedian;
    }

    public String getMode() {
        int maxCount = 0;
        boolean duplicateMode = false;
        Object mMode = 0.0;
        if (size() > 1) {
            for (int i : range(super.toSet().size())) {
                int counter = count(super.toSet().pop(i));
                if (counter == maxCount) {
                    duplicateMode = true;
                }
                if (counter > maxCount) {
                    duplicateMode = false;
                    maxCount = counter;
                    mMode = pop(i);
                }
            }
            if (duplicateMode) {
                return null;
            }
            return mMode.toString();
        } else {
            if (size() == 1) {
                mMode = pop(0);
            }
        }
        return mMode.toString();
    }

    public String getModeCount() {
        if (getMode() != null) {
            return Funcs.toStrings(count(Funcs.toDouble(getMode())));
        }
        return null;
    }


    public double getStandardDiv() {
        double stanDiv = 0;
        for (double num : toDouble(toArray())) {
            stanDiv += Math.pow(num - getMean(), 2);
        }
        return Math.sqrt(stanDiv / size());// or Math.sqrt(getVariance());
    }

    public double getVariance() {
        double mVariance = 0;
        for (double i : toDouble(toArray())) {
            mVariance += (i - getMean()) * (i - getMean());
        }
        mVariance /= size();
        return mVariance;// or getStandardDiv/size();
    }

    public double getCoVariance(Double array[]) {
        double sum = 0;
        if (size() == array.length) {
            StatsArray coArray = new StatsArray(array);
            for (int i : range(size())) {
                sum += (pop(i) - getMean()) * (coArray.pop(i) - coArray.getMean());
            }
        }
        return sum / (size() - 1);
    }

    public Enumerator<Double> getEnumerator() {
        return new Enumerator<>(this);
    }

}