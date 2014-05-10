package org.jdice.calc;

/**
 * Describe individual step of calculation process.
 * Provided by {@link AbstractCalculator#getTracedSteps()} 
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public class Step {

    private String step;
    private String detailStep;
    
    public Step(String step, String detailStep) {
        this.step = step;
        this.detailStep = detailStep;
    }
    
    public String getDetail() {
        return detailStep;
    }
    
    @Override
    public String toString() {
        return step;
    }
    
}
