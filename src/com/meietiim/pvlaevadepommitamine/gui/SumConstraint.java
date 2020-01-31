package com.meietiim.pvlaevadepommitamine.gui;

public class SumConstraint extends Constraint {
    private Constraint[] sourceConstraints;

    public SumConstraint(Constraint... constraints) {
        this.sourceConstraints = constraints;
    }

    @Override
    public float calculate() {
        float value = 0;

        for(Constraint constraint : sourceConstraints) {
            value += constraint.calculate();
        }

        return value;
    }
}
