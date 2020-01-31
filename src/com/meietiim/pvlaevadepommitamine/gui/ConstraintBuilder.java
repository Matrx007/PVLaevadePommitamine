package com.meietiim.pvlaevadepommitamine.gui;

public class ConstraintBuilder {
    private Constraint constraint;

    public ConstraintBuilder(Constraint constraint) {
        this.constraint = constraint;
    }

    public void add(float fixedValue) {
        constraint = new SumConstraint(constraint, new FixedConstraint(fixedValue));
    }

    public void add(Constraint add) {
        constraint = new SumConstraint(constraint, add);
    }

    public void mul(float fixedValue) {
        constraint = new MultiplyConstraint(constraint, new FixedConstraint(fixedValue));
    }

    public void mul(Constraint mul) {
        constraint = new MultiplyConstraint(constraint, add);
    }
}
