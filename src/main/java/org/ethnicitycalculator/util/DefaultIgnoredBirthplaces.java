package org.ethnicitycalculator.util;

import java.util.Arrays;
import java.util.List;

public class DefaultIgnoredBirthplaces {

    public List<String> americanLocales = Arrays.asList(
            "United States",
            "USA",
            "U.S.A",
            "U.S.",
            "Canada",
            "Quebec",
            "Illinois",
            "Oregon",
            "Ohio",
            "Wisconsin",
            "New York",
            "Dakota",
            "Carolina",
            "Tennessee",
            "Pennsylvania",
            "Massachusetts",
            "Missouri",
            "New Jersey",
            "America",
            "Kentucky",
            "NJ",
            "NH",
            "VA"
    );

    private List<String> colonialLocales = Arrays.asList(
            "North Carolina",
            "United States",
            "New York",
            "Quebec",
            "Canada",
            "Atlantic ocean (en route to America)",
            "New Jersey",
            "Pennsylvania",
            "British Colonial America",
            "Connecticut",
            "Qbc.",
            "NH",
            "Virginia"
    );

    public List<String> getAll() {
        return americanLocales;
    }

    public void addItem(String birthplace) {
        americanLocales.add(birthplace);
    }

    public void removeItem(String birthplace) {
        americanLocales.remove(birthplace);
    }

    public List<String> getColonialLocales() {
        return colonialLocales;
    }
}
