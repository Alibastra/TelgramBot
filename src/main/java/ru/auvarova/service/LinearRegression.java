package ru.auvarova.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LinearRegression {
    private final BigDecimal intercept, slope;

    /**
     * Performs a linear regression on the data points {@code (y[i], x[i])}.
     *
     * @param x the values of the predictor variable
     * @param y the corresponding values of the response variable
     * @throws IllegalArgumentException if the lengths of the two arrays are not equal
     */
    public LinearRegression(BigDecimal[] x, BigDecimal[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("array lengths are not equal");
        }
        int n = x.length;

        // first pass
        BigDecimal sumx = BigDecimal.valueOf(0), sumy = BigDecimal.valueOf(0);
        for (int i = 0; i < n; i++) {
            sumx = sumx.add(x[i]);
            sumy = sumy.add(y[i]);
        }
        BigDecimal xbar = sumx.divide(BigDecimal.valueOf(n),4, RoundingMode.HALF_UP);
        BigDecimal ybar = sumy.divide(BigDecimal.valueOf(n),4, RoundingMode.HALF_UP);

        // second pass: compute summary statistics
        BigDecimal xxbar = BigDecimal.valueOf(0), xybar = BigDecimal.valueOf(0);
        for (int i = 0; i < n; i++) {
            xxbar = xxbar.add((x[i].subtract(xbar)).multiply(x[i].subtract(xbar)));
            xybar = xybar.add((x[i].subtract(xbar)).multiply(y[i].subtract(ybar)));
        }
        slope = xybar.divide(xxbar,4, RoundingMode.HALF_UP);
        intercept = ybar.subtract(slope.multiply( xbar));
    }

    /**
     * Returns the <em>y</em>-intercept &alpha; of the best of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>.
     *
     * @return the <em>y</em>-intercept &alpha; of the best-fit line <em>y = &alpha; + &beta; x</em>
     */
    public BigDecimal intercept() {
        return intercept;
    }

    /**
     * Returns the slope &beta; of the best of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>.
     *
     * @return the slope &beta; of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>
     */
    public BigDecimal slope() {
        return slope;
    }

    /**
     * Returns the expected response {@code y} given the value of the predictor
     * variable {@code x}.
     *
     * @param x the value of the predictor variable
     * @return the expected response {@code y} given the value of the predictor
     * variable {@code x}
     */
    public BigDecimal predict(BigDecimal x) {
        return slope.multiply(x.add(intercept));
    }
}
