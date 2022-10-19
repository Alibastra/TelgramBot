package ru.auvarova.service;

import ru.auvarova.model.Rate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CalculationAlgorithms {
    public BigDecimal algLastYear(List<Rate> historyRate, LocalDate currentDate) {
        LocalDate lastYearDate = currentDate.minusYears(1);
        while (true) {
            for (Rate rate : historyRate)
                if (rate.getDayDate().equals(lastYearDate)) {
                    return rate.getCurs();
                }
            lastYearDate = lastYearDate.minusDays(1);
            //проверку на дату
        }
    }


    public BigDecimal algMystical(List<Rate> historyRate, LocalDate currentDate) {
        int maxDate = currentDate.getYear();
        int minDate = maxDate;
        int mysticalYear;
        for (Rate rate: historyRate){
            if (rate.getDayDate().getYear() < minDate)
                minDate = rate.getDayDate().getYear();
        }
        Random r = new Random();
        mysticalYear = r.nextInt((maxDate - minDate)-1) + minDate+1;
        LocalDate mysticalDate = LocalDate.of(mysticalYear, currentDate.getMonth(),currentDate.getDayOfMonth());
        return algLastYear(historyRate,mysticalDate);

    }

    /*public BigDecimal algLinearRegression(List<Rate> historyRate) {
        return Collections.emptyList();
    }*/

}
