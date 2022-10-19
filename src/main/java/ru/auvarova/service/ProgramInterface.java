package ru.auvarova.service;

import java.io.IOException;
import java.util.Scanner;

public class ProgramInterface {
    private static final String HEADER = "\nВыберете одну из предоженных валют:" +
            "\n\t USD - Доллар США" +
            "\n\t TRY - Турецкая лира" +
            "\n\t EUR - Евро" +
            "\nДля выхода q...";
    private static final String LINE = " 1 - Курс валют на завтра" +
            "\n 2 - Курс валют на 7 дней" +
            "\n Для выхода q...";
    private static final String ERROR_MSG = "Ошибка выбора символа, попробуйте снова!";
    private enum Option {
        DAY(1),
        WEEK(7),
        MONTH(30);
        private final int countDay;
        Option (int countDay){
            this.countDay = countDay;
        }
        public int getCountDay() {
            return countDay;
        }
    }

    private enum rateFile {
        USD("USD.csv"),
        TRY("TRY.csv"),
        EUR("EUR.csv"),
        AMD("AMD.csv"),
        BGN("BGN.csv");
        private final String nameFile;
        rateFile (String nameFile){
            this.nameFile = nameFile;
        }
        public String getFileName() {
            return nameFile;
        }
    }
    public void startInterface() throws IOException {

        String changeRate = "";
        String changePeriod = "";
        Scanner in;
        RateService rateService= new RateService();

        while (!changeRate.equals("q")) {
            System.out.println(HEADER);
            in = new Scanner(System.in);
            changeRate = in.next();

            if (changeRate.equals("USD")||changeRate.equals("TRY")
                    ||changeRate.equals("EUR")||changeRate.equals("AMD")
                    ||changeRate.equals("BGN")) {

                System.out.println(LINE);
                changePeriod = in.next();

                if (changePeriod.equals("1")) {
                    rateService.ExchangeRateForecast(rateFile.valueOf(changeRate).getFileName(), Option.DAY.getCountDay());
                } else if (changePeriod.equals("2")) {
                    rateService.ExchangeRateForecast(rateFile.valueOf(changeRate).getFileName(), Option.WEEK.getCountDay());
                } else if(!changePeriod.equals("q")) {
                    System.out.println(ERROR_MSG);
                }

            } else if (!changeRate.equals("q")){
                System.out.println(ERROR_MSG);
            }
        }
    }
}
