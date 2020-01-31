package com.meietiim.pvlaevadepommitamine.gui;

public class FixedConstraint extends Constraint {

    private float fixedValue;

    public FixedConstraint(float fixedValue) {
        this.fixedValue = fixedValue;
    }

    @Override
    public float calculate() {
        return fixedValue;
    }
}
