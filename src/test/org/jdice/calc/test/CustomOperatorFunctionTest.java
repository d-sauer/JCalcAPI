package org.jdice.calc.test;

import java.math.BigDecimal;
import java.text.ParseException;

import org.jdice.calc.AbstractCalculator;
import org.jdice.calc.Calc;
import org.jdice.calc.Function;
import org.jdice.calc.Num;
import org.jdice.calc.Operator;
import org.jdice.calc.SingletonComponent;
import org.junit.Test;
import static org.junit.Assert.*;

public class CustomOperatorFunctionTest {
    @SingletonComponent
    public static class QuestionOperator implements Operator {

        @Override
        public int getPriority() {
            return 5;
        }

        @Override
        public String getSymbol() {
            return "?";
        }

        @Override
        public Num calc(AbstractCalculator arg0, Num arg1, Num arg2) throws Exception {
            BigDecimal value = arg1.toBigDecimal().add(arg1.toBigDecimal());
            value = value.multiply(new BigDecimal(2));
            Num result = new Num(value);
            return result;
        }

    }

    @SingletonComponent
    public static class SumFunction implements Function {

        @Override
        public String getSymbol() {
            return "sum";
        }

        @Override
        public Num calc(AbstractCalculator arg0, Num... arg1) throws Exception {
            BigDecimal sum = new BigDecimal(0);
            for (Num n : arg1) {
                sum = sum.add(n.toBigDecimal());
            }

            return new Num(sum);
        }

        @Override
        public int getFunctionAttributes() {
            return 0;
        }

    }

    @Test
    public void testQuestionOperator() throws ParseException {
        Calc calc = new Calc();
        calc.register(QuestionOperator.class);
        calc.register(SumFunction.class);

        calc.expression("2 ? 2 + 5 - 1 + sum(1,2,3,4)");

        Num res = calc.calcWithSteps(false);
        assertEquals("22", res.toString());
//        System.out.println(res);
//
//        for (String step : calc.getCalculationSteps())
//            System.out.println(step);
    }

    public static void main(String[] args) throws ParseException {
        CustomOperatorFunctionTest ccof = new CustomOperatorFunctionTest();
        ccof.testQuestionOperator();
    }
}
