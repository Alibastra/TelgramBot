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
    /**
     * Чтение курса валют из csv-файла
     *
     * @param fileName имя файла
     * @return списов валют (объектов Rate), полученных из csv-файла
     */
    //сделать чтение из ресурсов
    public List<Rate> readFromCsvFail(String fileName) throws IOException {
        List<Rate> resultList = new ArrayList<>();
        String row;

        InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedReader csvReader = new BufferedReader(new InputStreamReader(in));
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
        resultList = resultList.stream()
                .sorted((o1, o2) -> o1.getDayDate().compareTo(o2.getDayDate()))
                .collect(Collectors.toList());
        return resultList;
    }

    private List<Rate> ExchangeRate(List<Rate> historyRate, int countDays) {
        int CountAlg = 7;
        LocalDate nowDate = LocalDate.now();
        LocalDate currentDate = nowDate.plusDays(countDays);
        int cntAddRate, j;
        for (cntAddRate = 1; cntAddRate <= countDays; cntAddRate++) {
            BigDecimal cursNewRate = new BigDecimal("0"); // Прогнозируемый курс (пересчитывается для каждой даты - переименовано с "curs")
            int currentRate = historyRate.size() - CountAlg;
            while (currentRate < historyRate.size()) {
                cursNewRate = cursNewRate.add(historyRate.get(currentRate).getCurs());
                currentRate++;
            }
            cursNewRate = cursNewRate.divide(BigDecimal.valueOf(CountAlg), 4, RoundingMode.HALF_UP);

            historyRate.add(new Rate(historyRate.get(1).getNominal()
                    , nowDate.plusDays(cntAddRate)
                    , cursNewRate
                    , historyRate.get(1).getNameRate()));
        }

        return historyRate;
    }

    /**
     * Прогнозирование курса валют на указанный период
     *
     * @param changeFile индекс файла со списком валют, известных на текущий момент
     * @param countDays  кол-во дней для прогноза
     */
    public void ExchangeRateForecast(String changeFile, int countDays) throws IOException {
        List<Rate> historyRate = readFromCsvFail(changeFile);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        historyRate = ExchangeRate(historyRate, countDays);

        Locale localeRu = new Locale("ru", "RU");
        String nameRate = historyRate.get(1).getNameRate();
        String textHeader = "";
        if (countDays == 1) {
            textHeader = String.format("Курс %s на завтра", nameRate);
        } else {
            textHeader = String.format("Курс %s на неделю", nameRate);
        }
        System.out.println(textHeader);

        for (int currentRate = historyRate.size() - countDays; currentRate < historyRate.size(); currentRate++) {
            LocalDate curDate = historyRate.get(currentRate).getDayDate();
            String dayOfWeek = curDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, localeRu);
            String currentDate = curDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru")));
            String currentCurs = decimalFormat.format(historyRate.get(currentRate).getCurs());
            String newRow = String.format("\t%s %s - %s", dayOfWeek, currentDate, currentCurs);
            System.out.println(newRow);
        }
    }
}
