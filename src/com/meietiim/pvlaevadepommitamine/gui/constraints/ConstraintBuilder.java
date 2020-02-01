package com.meietiim.pvlaevadepommitamine.gui.constraints;

public class ConstraintBuilder {
    public Constraint constraint;

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
        constraint = new MultiplyConstraint(constraint, mul);
    }

    public void sub(float fixedValue) {
        constraint = new SubtractConstraint(constraint, new FixedConstraint(fixedValue));
    }

    public void sub(Constraint sub) {
        constraint = new SubtractConstraint(constraint, sub);
    }

    public void div(float fixedValue) {
        constraint = new DivideConstraint(constraint, new FixedConstraint(fixedValue));
    }

    public void div(Constraint div) {
        constraint = new DivideConstraint(constraint, div);
    }
}
