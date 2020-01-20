package com.visione.taskreminder.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;

// Class to compare date and time so that items are sorted in ascending order
public class DateTimeComparator implements Comparator {
    private DateFormat f = new SimpleDateFormat("dd/mm/yyyy hh:mm a", Locale.getDefault());

    public int compare(Object a, Object b) {
        String o1 = ((DateTimeSorter)a).getDateTime();
        String o2 = ((DateTimeSorter)b).getDateTime();

        try {
            return f.parse(o1).compareTo(f.parse(o2));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
