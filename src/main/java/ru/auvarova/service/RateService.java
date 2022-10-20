package ru.auvarova.service;

import ru.auvarova.model.Rate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class RateService {
    private enum rateFile {
        USD("USD.csv"),
        TRY("TRY.csv"),
        EUR("EUR.csv"),
        AMD("AMD.csv"),
        BGN("BGN.csv");
        private final String nameFile;

        rateFile(String nameFile) {
            this.nameFile = nameFile;
        }

        public String getFileName() {
            return nameFile;
        }
    }

    /**
     * Чтение курса валют из csv-файла
     *
     * @param fileName имя файла
     * @return списов валют (объектов Rate), полученных из csv-файла
     */
    //сделать чтение из ресурсов
    public List<Rate> readFromCsvFail(String fileName) {
        List<Rate> resultList = new ArrayList<>();
        String row;

        InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedReader csvReader = new BufferedReader(new InputStreamReader(in));
        try {
            while ((row = csvReader.readLine()) != null) {
                String[] tokensCsv = row.split(";");
                if (!tokensCsv[0].equals("nominal")) {
                    Rate rate = new Rate(Integer.parseInt(tokensCsv[0])
                            , LocalDate.parse(tokensCsv[1], DateTimeFormatter.ofPattern("d.MM.yyyy"))
                            , new BigDecimal(tokensCsv[2].replace(',', '.'))
                            , tokensCsv[3]);
                    resultList.add(rate);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        resultList = resultList.stream()
                .sorted((o1, o2) -> o1.getDayDate().compareTo(o2.getDayDate()))
                .collect(Collectors.toList());
        return resultList;
    }

    /**
     * Прогнозирование курса валют - вы
     * @param historyRate списов истории валют (объектов Rate)
     * @param date дата прогнозирования (может равняться null, если countDays != 0 )
     * @param countDays кол-во дней для прогноза (может равняться 0, если date != null )
     * @param algoritm алгоритм, по которому осуществляется прогноз
     * @return Прогноз валюты за указанный период
     */
    private List<Rate> ExchangeRate(List<Rate> historyRate, String date, int countDays, String algoritm) {
        LocalDate forecastDate = null;
        Rate newRate;
        List<Rate> resultRates = null;
        CalculationAlgorithms calculationAlgorithms = new CalculationAlgorithms();
        if (date != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            forecastDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("d.MM.yyyy"));
        } else{
            forecastDate = LocalDate.now().plusDays(1);
        }

        for (int countAddRate = 1; countAddRate < countDays + 1; countAddRate++) {

            if (algoritm.equals("LastYear")) {
                newRate = calculationAlgorithms.algLastYear(historyRate, forecastDate);
            } else if (algoritm.equals("Mystical")) {
                newRate = calculationAlgorithms.algMystical(historyRate, forecastDate);
            } else {
                newRate = calculationAlgorithms.algLinearRegression(historyRate, forecastDate);
            }
            resultRates.add(newRate);
        }

        return resultRates;
    }
    /**
     * Вывод данных курса за указанный период для одного вида валюты
     * @param currency код валюты для прогнозировния
     * @param date дата прогнозирования (может равняться null, если countDays != 0 )
     * @param countDays кол-во дней для прогноза (может равняться 0, если date != null )
     * @param algoritm алгоритм, по которому осуществляется прогноз
     */
    public void ExchangeRateForecast(String currency, String date, int countDays, String algoritm) {

        List<Rate> historyRate = readFromCsvFail(rateFile.valueOf(currency).getFileName());
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        List<Rate> resultRates = ExchangeRate(historyRate, date, countDays, algoritm);

        Locale localeRu = new Locale("ru", "RU");

        for (Rate rate : resultRates) {
            LocalDate curDate = rate.getDayDate();
            String dayOfWeek = curDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, localeRu);
            String currentDate = curDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru")));
            String currentCurs = decimalFormat.format(rate.getCurs().divide(BigDecimal.valueOf(rate.getNominal()), 4, RoundingMode.HALF_UP));
            String newRow = String.format("\t%s %s - %s", dayOfWeek, currentDate, currentCurs);
            System.out.println(newRow);
        }
    }
}
