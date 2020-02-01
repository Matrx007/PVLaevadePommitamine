package com.meietiim.pvlaevadepommitamine.gui.constraints;

public class SubtractConstraint extends Constraint {
    private Constraint[] sourceConstraints;

    public SubtractConstraint(Constraint... constraints) {
        this.sourceConstraints = constraints;
    }

    @Override
    public float calculate() {
        float value = 0;

        for(Constraint constraint : sourceConstraints) {
            value -= constraint.calculate();
        }

        return value;
    }
}
