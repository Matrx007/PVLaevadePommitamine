package com.meietiim.pvlaevadepommitamine.gui.constraints;

public class SubtractConstraint extends Constraint {
    private Constraint[] sourceConstraints;

    public SubtractConstraint(Constraint... constraints) {
        this.sourceConstraints = constraints;
    }

    @Override
    public float calculate() {
        boolean initialized = false;
        float value = 0;

        for(Constraint constraint : sourceConstraints) {
            if(!initialized) {
                value = constraint.calculate();
                initialized = true;
            } else {
                value -= constraint.calculate();
            }
        }

        return value;
    }
}
