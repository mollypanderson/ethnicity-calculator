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
            "VA",
            "British Colonial America",
            "BRITISH COLONIAL AMERICA (William Stephen Woodrum I)",
            "BRITISH COLONIAL AMERICA (Martha Patsy  Moore)"
            //  "Qbc."
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
}
