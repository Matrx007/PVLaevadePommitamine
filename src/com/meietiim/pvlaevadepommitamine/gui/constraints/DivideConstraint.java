package com.meietiim.pvlaevadepommitamine.gui.constraints;


public class DivideConstraint extends Constraint {
    private Constraint A, B;

    public DivideConstraint(Constraint A, Constraint B) {
        this.A = A;
        this.B = B;
    }

    @Override
    public float calculate() {
        return A.calculate() / B.calculate();
    }
}
