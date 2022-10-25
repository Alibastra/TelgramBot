package ru.auvarova.service;

import ru.auvarova.model.Rate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

public class ImageProcessor {
    private static final int WIDTH = 960;
    private static final int HEIGHT = 540;

    private static final int OX = 40;
    private static final int OY = 500;
    private static final int MAX_X = 900;
    private static final int MAX_Y = 40;


    private enum Colors {
        AMD(new Color(255, 0, 0)),
        USD(new Color(0, 255, 0)),
        BGN(new Color(0, 0, 255)),
        TRY(new Color(255, 200, 20)),
        EUR(new Color(255, 20, 255)),
        BACKGROUND(new Color(249, 243, 221));
        private final Color color;

        Colors(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    /**
     * Вывод легенды для валют
     * @param currencies валюта
     * @param graphics Объект для вывода легенды
     */
    private static void printLegend(String currencies[], Graphics graphics) {
        int x = 800, y = 40;
        for (String currency : currencies) {
            graphics.setColor(Colors.valueOf(currency).getColor());
            graphics.drawString(currency, x, y);
            graphics.drawLine(x + 30, y - 5, x + 90, y - 5);
            y += 20;
        }
    }

    /**
     * Вывод осей графика
     * @param image изображение на которое произвоится вывод
     * @param countDays кол-вл дней на графике
     * @return объект рисования
     */
    private static Graphics printBase(BufferedImage image, int countDays) {
        Graphics graphics = image.getGraphics();
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.black);
        g2d.rotate(Math.toRadians(-90));
        //Создание осей графика

        int dx = 5, dy = 5;

        LocalDate date = LocalDate.now().plusDays(1);
        //Фон
        graphics.setColor(Colors.BACKGROUND.getColor());
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        graphics.setColor(Color.black);
        graphics.drawLine(OX, OY, MAX_X, OY);
        graphics.drawLine(MAX_X, OY, MAX_X - dx, OY + dy);
        graphics.drawLine(MAX_X, OY, MAX_X - dx, OY - dy);
        graphics.drawLine(OX, OY, OX, MAX_Y);
        graphics.drawLine(OX, MAX_Y, OX + dx, MAX_Y + dy);
        graphics.drawLine(OX, MAX_Y, OX - dx, MAX_Y + dy);
        String strY = MessageFormat.format("{0}.{1}", date.getDayOfMonth(), date.getMonthValue());
        int distX = (MAX_X - OX) / countDays;
        int x = OX + distX;

        int countDay = 1;
        g2d.drawString(String.valueOf(strY), -OY - 35, OX + 5);

        while (countDay < countDays) {

            graphics.drawLine(x, OY - dy, x, OY + dy);
            date = date.plusDays(1);
            strY = MessageFormat.format("{0}.{1}", date.getDayOfMonth(), date.getMonthValue());
            g2d.drawString(String.valueOf(strY), -OY + dy - 40, x + 5);
            x += distX;
            countDay++;
        }

        int distY = 30;
        int y = OY - distY;
        int strX = 0;
        int dxx = 0; //отступ при трехзначных числах (100 и более)
        graphics.drawString(String.valueOf(strX), OX - 10, OY);

        while (y > MAX_Y) {
            graphics.drawLine(OX - dx, y, OX + dx, y);
            strX += 10;
            if (strX > 90)
                dxx = 5;
            graphics.drawString(String.valueOf(strX), OX - 20 - dxx, y + dx);
            y -= distY;
        }
        graphics.drawString("Руб.", OX - 20, y + 10);
        graphics.drawString("Дни", x - 20, OY + 20);
        return graphics;
    }

    /**
     * Вывод кривой (по значением курса валюты)
     * @param currency валюта
     * @param rates лист значений курсов
     * @param graphics объект рисования
     */
    private static void PrintCurrencyLine(String currency, List<BigDecimal> rates, Graphics graphics) {
        graphics.setColor(Colors.valueOf(currency).getColor());
        int dx = (MAX_X - OX) / rates.size();
        int dy = 3;
        int y = OY- rates.get(0).multiply(BigDecimal.valueOf(dy)).toBigInteger().intValueExact();
        int x = OX;
        int newX, newY;
        for (BigDecimal rate : rates) {
            newX = x + dx;
            newY = OY - rate.multiply(BigDecimal.valueOf(dy)).toBigInteger().intValueExact();
            graphics.drawLine(x, y, newX, newY);
            x = newX;
            y = newY;

        }
    }

    /**
     * Программа для построения графика
     * @param rates валюты для выводв на графике
     * @param countDays кол-во дней (период на графике)
     * @param algoritm алгоритм по которому прогнозируется курс валюты
     * @param nameFile имя файла для вывода графика
     */
    public static void printGraph(String rates[], int countDays, String algoritm, String nameFile) {
        List<BigDecimal> rateValues;
        RateService rateService = new RateService();
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = printBase(image, countDays);
        printLegend(rates, g);
        for (String rate : rates) {
            rateValues = rateService.ExchangeRateForecastGraph(rate, countDays, algoritm);
            PrintCurrencyLine(rate, rateValues, g);

        }
        try {
            // Созраняем результат в новый файл
            File output = new File(nameFile);
            ImageIO.write(image, "jpg", output);

        } catch (IOException e) {
            System.out.println("Не удалось сохранить файл");
        }
    }
}