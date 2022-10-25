package ru.auvarova.service;

import ru.auvarova.model.Rate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static java.time.Period.between;

public class CalculationAlgorithms {
    private static BigDecimal intercept = BigDecimal.ZERO;
    private static BigDecimal slope = BigDecimal.ZERO;

    /**
     * Вычисление курса валюты на заданную дату по алгоритму "Прошлогодний"
     * @param historyRate история курсов валюты
     * @param currentDate заданныая дата
     * @return Объект класса Rate - валюта с данными
     */
    public Rate algLastYear(List<Rate> historyRate, LocalDate currentDate) {
        LocalDate lastYearDate = currentDate.minusYears(1);
        while (true) {
            for (Rate rate : historyRate)
                if (rate.getDayDate().equals(lastYearDate)) {
                    return new Rate (rate.getNominal(),
                                     currentDate,
                                     rate.getCurs(),
                                     rate.getNameRate());
                }
            lastYearDate = lastYearDate.minusDays(1);
        }
    }

    /**
     * Вычисление курса валюты на заданную дату по алгоритму "Мистический"
     * @param historyRate история курсов валюты
     * @param currentDate заданныая дата
     * @return Объект класса Rate - валюта с данными
     */
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
    /**
     * Вычисление курса валюты на заданную дату по алгоритму "Из интернета(Линейной регресии)"
     * @param historyRate история курсов валюты
     * @param currentDate заданныая дата
     * @return Объект класса Rate - валюта с данными
     */
    public Rate algLinearRegression(List<Rate> historyRate, LocalDate currentDate) {
        int maxSize = historyRate.size();
        if (intercept.equals(BigDecimal.ZERO) || slope.equals(BigDecimal.ZERO)){
            BigDecimal curs[] = new BigDecimal[maxSize];
            BigDecimal countCurs[] = new BigDecimal[maxSize];
            int i = 1;
            for (Rate rate : historyRate) {
                countCurs[i - 1] = BigDecimal.valueOf(i);
                curs[i - 1] = rate.getCurs().divide(BigDecimal.valueOf(rate.getNominal()),4, RoundingMode.HALF_UP);
                i++;
            }
            LinearRegression linearRegression = new LinearRegression(countCurs, curs);
            intercept= linearRegression.intercept();
            slope = linearRegression.slope();
        }
        int daysBetween = between(LocalDate.now(), currentDate).getDays();
        BigDecimal predict = BigDecimal.valueOf(maxSize + daysBetween);
        BigDecimal newCurs = slope.multiply(predict.add(intercept));

        return new Rate (1,
                         currentDate,
                         newCurs,
                         historyRate.get(0).getNameRate());
    }
}
