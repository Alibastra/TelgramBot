package ru.auvarova.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Rate {
    private final Integer nominal;
    private final LocalDate dayDate;
    private final BigDecimal curs;
    private final String nameRate;

    public Rate(Integer nominal, LocalDate dayDate, BigDecimal curs, String nameRate) {
        this.nominal = nominal;
        this.dayDate = dayDate;
        this.curs = curs;
        this.nameRate = nameRate;
    }

    public LocalDate getDayDate(){
        return dayDate;
    }
    public Integer getNominal(){
        return nominal;
    }
    public BigDecimal getCurs(){
        return curs;
    }
    public String getNameRate(){
        return nameRate;
    }

}

