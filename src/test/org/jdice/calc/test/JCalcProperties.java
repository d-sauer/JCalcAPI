package org.jdice.calc.test;

import org.jdice.calc.Cache;
import org.jdice.calc.Properties;
import org.jdice.calc.test.CustomOperatorFunctionTest.QuestionOperator;
import org.jdice.calc.test.CustomOperatorFunctionTest.SumFunction;
import org.jdice.calc.test.NumTest.CustomObject;
import org.jdice.calc.test.NumTest.CustomObjectNumConverter;

public class JCalcProperties {

    public void writeProperties() throws Exception {
         Cache.registerOperator(QuestionOperator.class);
         Cache.registerFunction(SumFunction.class);
         Cache.registerNumConverter(CustomObject.class, CustomObjectNumConverter.class);

         Properties p = new Properties();
         p.saveGlobalProperties();
    }
    
    public static void main(String [] args) throws Exception {
        JCalcProperties prop = new JCalcProperties();
        prop.writeProperties();
    }
    
}
