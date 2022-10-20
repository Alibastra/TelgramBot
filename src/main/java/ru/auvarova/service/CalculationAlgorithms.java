package ru.auvarova.service;

import ru.auvarova.model.Rate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static java.time.Period.between;

public class CalculationAlgorithms {
    private static BigDecimal intercept = null, slope = null;
    public Rate algLastYear(List<Rate> historyRate, LocalDate currentDate) {
        LocalDate lastYearDate = currentDate.minusYears(1);
        while (true) {
            for (Rate rate : historyRate)
                if (rate.getDayDate().equals(lastYearDate)) {
                    return new Rate (historyRate.get(0).getNominal(),
                                     historyRate.get(0).getDayDate(),
                                     rate.getCurs(),
                                     historyRate.get(0).getNameRate());
                }
            lastYearDate = lastYearDate.minusDays(1);

        }
    }


    public Rate algMystical(List<Rate> historyRate, LocalDate currentDate) {
        int maxDate = currentDate.getYear();
        int minDate = maxDate;
        int mysticalYear;
        for (Rate rate : historyRate) {
            if (rate.getDayDate().getYear() < minDate)
                minDate = rate.getDayDate().getYear();
        }
        Random r = new Random();
        mysticalYear = r.nextInt((maxDate - minDate) - 1) + minDate + 1;
        LocalDate mysticalDate = LocalDate.of(mysticalYear, currentDate.getMonth(), currentDate.getDayOfMonth());
        return algLastYear(historyRate, mysticalDate);

    }

    public Rate algLinearRegression(List<Rate> historyRate, LocalDate currentDate) {
        int maxSize = historyRate.size();
        if (intercept.equals(null) || slope.equals(null)){
            BigDecimal curs[] = new BigDecimal[maxSize];
            BigDecimal countCurs[] = new BigDecimal[maxSize];
            int i = 1;
            for (Rate rate : historyRate) {
                curs[i - 1] = rate.getCurs();
                countCurs[i - 1] = BigDecimal.valueOf(i);
            }
            LinearRegression linearRegression = new LinearRegression(countCurs, curs);
            intercept= linearRegression.intercept();
            slope = linearRegression.slope();
        }
        int daysBetween = between(LocalDate.now(), currentDate).getDays();
        BigDecimal predict = BigDecimal.valueOf(maxSize + daysBetween);
        BigDecimal newCurs = slope.multiply(predict.add(intercept));

        return new Rate (historyRate.get(0).getNominal(),
                         historyRate.get(0).getDayDate(),
                         newCurs,
                         historyRate.get(0).getNameRate());
    }
}
