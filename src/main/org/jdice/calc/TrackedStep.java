package org.jdice.calc;

public class TrackedStep {

    private String step;
    private String detailStep;
    
    public TrackedStep(String step, String detailStep) {
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
