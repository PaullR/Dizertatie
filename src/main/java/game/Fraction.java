package game;

import com.google.common.math.IntMath;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by Paul on 5/19/2018.
 */
public class Fraction {
    public final static Fraction ZERO = new Fraction(0, 1);
    public final static Fraction ONE = new Fraction(1, 1);
    private int numerator;
    private int denominator;

    public Fraction() {
    }

    public Fraction(int numerator, int denominator) {
        if (denominator == 0)
            throw new ArithmeticException("Division by zero.");

        int gcd = IntMath.gcd(numerator, denominator);
        this.numerator = numerator / gcd;
        this.denominator = denominator / gcd;

    }

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Fraction number = (Fraction) o;

        if (numerator != number.numerator) return false;
        return denominator == number.denominator;
    }

    @Override
    public int hashCode() {
        int result = numerator;
        result = 31 * result + denominator;
        return result;
    }

    public static Fraction multiplyFractionsList(List<Fraction> nrs) {
        BiFunction<Fraction, Fraction, Fraction> productFunction = (p1, p2) -> {
            int numerator = p1.getNumerator() * p2.getNumerator();
            int denominator = p1.getDenominator() * p2.getDenominator();
            return new Fraction(numerator, denominator);
        };

        return nrs.stream()
                .reduce(Fraction.ONE, (p1, p2) -> productFunction.apply(p1, p2));
    }

    public static final Fraction addFractionsList(List<Fraction> nrs) {

        BiFunction<Fraction, Fraction, Fraction> sumFunction = (p1, p2) -> {
            int numerator = p1.getNumerator() * p2.getDenominator() + p2.getNumerator() * p1.getDenominator();
            int denominator = p1.getDenominator() * p2.getDenominator();
            return new Fraction(numerator, denominator);
        };

        return nrs.stream()
                .reduce(Fraction.ZERO, (p1, p2) -> sumFunction.apply(p1, p2));
    }

    public static final Fraction addTwoFractions(Fraction fraction1, Fraction fraction2) {

        BiFunction<Fraction, Fraction, Fraction> sumFunction = (p1, p2) -> {
            int numerator = p1.getNumerator() * p2.getDenominator() + p2.getNumerator() * p1.getDenominator();
            int denominator = p1.getDenominator() * p2.getDenominator();
            return new Fraction(numerator, denominator);
        };

        return sumFunction.apply(fraction1, fraction2);
    }

    public static Fraction multiplyTwoFractions(Fraction fraction1, Fraction fraction2) {
        BiFunction<Fraction, Fraction, Fraction> productFunction = (p1, p2) -> {
            int numerator = p1.getNumerator() * p2.getNumerator();
            int denominator = p1.getDenominator() * p2.getDenominator();
            return new Fraction(numerator, denominator);
        };

        return productFunction.apply(fraction1, fraction2);
    }

    @Override
    public String toString() {
        if (numerator == 0) {
            return "0";
        }

        if (numerator == 1 && denominator == 1) {
            return "1";
        }

        return numerator + "/" + denominator;
    }
}
