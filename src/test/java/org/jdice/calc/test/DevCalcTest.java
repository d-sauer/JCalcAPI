package org.jdice.calc.test;

import java.math.BigDecimal;
import java.text.ParseException;

import org.jdice.calc.Calculator;
import org.jdice.calc.Num;
import org.jdice.calc.Step;
import org.junit.Test;

import static org.junit.Assert.*;

public class DevCalcTest {

    public static void example_1A() {
        System.out.println(2.00 - 1.10);
        
        
        // JCalc way...
        System.out.println(Calculator.builder().val(2.00).sub(1.10).calculate());
    }

    public static void example_1B() {
        BigDecimal payment = new BigDecimal(2.00);
        BigDecimal cost = new BigDecimal(1.10);
        System.out.println(payment.subtract(cost));
        
        Num p = new Num(2.00);
        Num c = new Num(1.10);
        System.out.println(Calculator.builder().val(p).sub(c).calculate());
    }

    public static void example_1C() throws ParseException {
        BigDecimal payment = new BigDecimal("2.00");
        BigDecimal cost = new BigDecimal("1.10");
        System.out.println(payment.subtract(cost));
        
        Num p = new Num("2.00");
        Num c = new Num("1.10");
        System.out.println(Calculator.builder().val(p).sub(c).setStripTrailingZeros(false).calculate());

        System.out.println(Calculator.builder("2.00 - 1.10").setStripTrailingZeros(false).calculate());
    }
    
    public static BigDecimal example_2A() {
        BigDecimal interestRate = new BigDecimal("6.5");    // fixed yearly interest rate in %
        BigDecimal P = new BigDecimal(200000);
        BigDecimal paymentYears = new BigDecimal(30);
        
        // monthly interest rate => 6.5 / 12 / 100 = 0.0054166667
        BigDecimal r = interestRate.divide(new BigDecimal(12), 10, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP);
        
        // numerator
        // => 0.005416666 * 200000 = 1083.3333400000
        BigDecimal numerator = r.multiply(P);
        
        // denominator
        r = r.add(new BigDecimal(1));   // => 1.0054166667
        BigDecimal pow = new BigDecimal(30 * 12);   // N = 30 * 12
        
        // => 1.005416666 ^ (-30 * 12)  ===> 1 / 1.005416666 ^ (30 * 12) 
        BigDecimal one = BigDecimal.ONE;
        BigDecimal r_pow = r.pow(pow.intValue());   // => 1.0054166667 ^ 360 = 6.991798057316914168045650152236411084215979195934314956373207624844772255420232239569859614996070657088270671683042964463117367500841637121263270461455907401080183973910668876483019104086355859669114192991680312954741135974300833574895423680366822182510023951073449793023698515496050087307262086910849262690017825502978457876876950917953799237087517382678154121988417976134443820346886608932600403835336247929477667509978222174496535137838368008801691784537354529561518795077776413555171895781748894529772774744716050023386170577064730997316420186446172810770470910963236389685290016939914840905227369068413079253902054690255308647947581204935165613739134276051578567487725888649635207754574310177321316301254527153207803713199397444750595211806455810954730486570218344742089727511756904124711154270613661656164245602369405030102919610468208034018246398214930625169970240642532721055960742623660262863664470568820791982021535417790766433964555355652572110990583146108574263565974779328089152175445737249182458732802945543750352748005113666304085810126040468937242881671122456390462952685081136271074454001347547198702844388261238480036094235915110827471671078191782541891213060307077539264438551958854498704389767927185048458676514683114465122462756365353898363748155704993882308218969347070774401104725718594213556485996069259455893731081709752553110192971958045739926632196142244457288321045127990999513350941684444798703310986690545098076563076964352619322826024193876287154243583773550348238713723353309458840711689039600426027879621150709878498054150184010035438567548065852362656063965021453603352070446601507816858902559563389322621426000564150719529551696890071466024345844658628911239582327970095919725468238979270296250122009433922126386970740138759716233775090660027274464131923889652391823917793764726371952161124599701022903613643199222453225576975297098679428170707897780975744083458107481414349410877046937034798815327407825556613303614290764397842639292211395302486996940994053681241285360485474843416143082544056891573611869199951158422552490865859870205795510153909257347542941412560729408615199386527676928510673737745605684218126451399640313948818506194543825278620297777924247037293099089941864250281894583134658333354668596244742897043799708550685713695930887905529376696094869717567401678857137615805538264868152793926921912242937633492955602921689568103016258847074308765252914620666385786869739355496880144264021822336038199112740552289509537942819779161379536703877222719215517602057403376723784601564949011080852855472613158333560963579530029976955717675631644212824534183983405342246461973451554451908521184881593799808529619107365882654979485128384021436629451283739424052685057446277722607101928393467109904778617845016936447312902905269294691196416998529769459573123790648927288687847898853084823573578455825808336361367463186749384089380315100410174545422446372563626342838840570177662801918234854884630975577164247634783444878395196102673857121349368808436237888022391569950688301420852258941108554352239229207356570014762709033036742335904337263935832641213205267726025154925364851467241200801382304141902650107474423697321968124880951423098896956945868731110558911430905955396249444191763530904261041235465526417768175400525631577022670129894267434844814251965464914190489456411874835952288439204746281514181176831626408044480902977165958889724323064912314010647171834772695315167770931579875801496583131143725094460411475206542492575493338096835029532293346008746370468786419774566876111803793143807379352153835113510979143479998842011000160635922179504752232440801
        r_pow = one.divide(r_pow, 10, BigDecimal.ROUND_HALF_UP);  // => 1 / 6.991798.. = 0.1430247258
       
        // => 1 - 0.1430247258 =  0.8569752742
        BigDecimal denominator = new BigDecimal(1);
        denominator = denominator.subtract(r_pow);
        
        // => 1083.3333400000 / 0.8569752742 = 1264.1360522464
        BigDecimal c = numerator.divide(denominator, 10, BigDecimal.ROUND_HALF_UP);
        
        c = c.setScale(2, BigDecimal.ROUND_HALF_UP);
        
        System.out.println("c = " + c);

        return c;
    }
    
    
   
    public static BigDecimal example_2B() throws ParseException {
        Num interestRate = new Num(6.5);    // fixed yearly interest rate in %
        Num P = new Num(200000);
        Num paymentYears = new Num(30);
        
        // monthly interest rate : r = 6.5 / 100 / 12
        Num r = Calculator.builder().openBracket().val(interestRate).div(100).closeBracket().div(12).calculate();

        // N = 30 * 12 -1
        Num N = Calculator.builder().val(paymentYears).mul(12).mul(-1).calculate();
        
        // c = (r * P) / (1 / (1 + r)^N
        Calculator c = new Calculator()     
                        .openBracket()
                            .val(r).mul(P)
                        .closeBracket()     //    numerator
                        .div()              // ---------------
                        .openBracket()      //    denumerator
                            .val(1).sub().openBracket().val(1).add(r).closeBracket().pow(N)
                        .closeBracket();
        
        Num result = c.calculate().setScale(2);
        
        System.out.println("c = " + result);
        return result.toBigDecimal();
    }
    
    // TODO: NOT WORKING : http://en.wikipedia.org/wiki/Mortgage_calculator
    public static BigDecimal example_2C() throws ParseException {
        Num interestRate = new Num("A", 6.5);
        Num P = new Num("B", 200000);
        Num paymentYears = new Num("C", -30);
        
        Calculator c = Calculator.builder("((A / 100 / 12) * B) / (1 - ((1 + (A / 100 / 12)) ^ (C * 12)))", interestRate, P, paymentYears);
        c.setScale(10);
        
        Num result = c.setTracingSteps(true).calculate();
        
        for(Step step : c.getTracedSteps())
            System.out.println(step);
        
        System.out.println("c = " + result.setScale(2));

        return result.toBigDecimal();
    }

    public static BigDecimal example_2D() throws ParseException {
        Num interestRate = new Num("A", 6.5);
        Num loan = new Num("B", 200000);
        Num paymentYears = new Num("C", 30);
        Num monthlyPayment = new Num();
        
        //Radi: C * -12
//        Calc calcMP = Calc.builder("((A / 100 / 12) * B) / (1 - ((1 + (A / 100 / 12)) ^ (C * -12)))", interestRate, loan, paymentYears); 
        
        // Ne radi: -C * 12
        Calculator calcMP = Calculator.builder("((A / 100 / 12) * B) / (1 - ((1 + (A / 100 / 12)) ^ (C * 12)))", interestRate, loan, paymentYears);
//        Calc calcMP = Calc.builder("((6.5 / 100 / 12) * 200000) / (1 - ((1 + (6.5 / 100 / 12)) ^ (-30 * 12)))"); // 6.5 100 / 12 / 200000 * 1 1 6.5 100 / 12 / + -30 12 * ^ - /
//        Calc calcMP = Calc.builder("((6.5 / 100 / 12) * 200000) / (1 - ((1 + (6.5 / 100 / 12)) ^ (-360)))");  // 6.5 100 / 12 / 200000 * 1 1 6.5 100 / 12 / + -360 ^ - /
        
//        Calc calcMP = Calc.builder("-2 * 3");
        System.out.println(calcMP.getInfix());
        System.out.println(calcMP.getPostfix());
        calcMP.setScale(5);
        monthlyPayment = calcMP.calculate();
        System.out.println(monthlyPayment);
        
        return monthlyPayment.toBigDecimal();
    }

    
    @Test
    public void testMortageCalculator() throws Exception {
        BigDecimal result1 = example_2A();
        BigDecimal result2 = example_2B();
        
        assertEquals(result1, result2);
        
    }
    
    public static void main(String [] args ) throws ParseException {
        example_2A();
        example_2B();
        example_2C();
    }
    
}
