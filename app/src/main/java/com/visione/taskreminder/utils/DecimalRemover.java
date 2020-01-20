package com.visione.taskreminder.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class DecimalRemover extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return Math.round(value)+"";
    }
}
