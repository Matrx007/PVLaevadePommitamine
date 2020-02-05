package com.meietiim.pvlaevadepommitamine.gui.constraints;

public class ConstraintBuilder {
    public Constraint constraint;

    // Initialize the constraint
    public ConstraintBuilder(Constraint constraint) {
        this.constraint = constraint;
    }

    // Initialize the constraint as a FixedConstraint
    public ConstraintBuilder(float value) {
        this.constraint = new FixedConstraint(value);
    }
    
    // Return the final constraint
    public Constraint get() {
        return constraint;
    }

    public ConstraintBuilder add(float fixedValue) {
        constraint = new SumConstraint(constraint, new FixedConstraint(fixedValue));
        return this;
    }

    public ConstraintBuilder add(Constraint add) {
        constraint = new SumConstraint(constraint, add);
        return this;
    }

    public ConstraintBuilder add(PassByReference value) {
        constraint = new SumConstraint(constraint, new LinkedConstraint(value));
        return this;
    }

    public ConstraintBuilder mul(float fixedValue) {
        constraint = new MultiplyConstraint(constraint, new FixedConstraint(fixedValue));
        return this;
    }

    public ConstraintBuilder mul(Constraint mul) {
        constraint = new MultiplyConstraint(constraint, mul);
        return this;
    }
    
    public ConstraintBuilder mul(PassByReference value) {
        constraint = new MultiplyConstraint(constraint, new LinkedConstraint(value));
        return this;
    }

    public ConstraintBuilder sub(float fixedValue) {
        constraint = new SubtractConstraint(constraint, new FixedConstraint(fixedValue));
        return this;
    }

    public ConstraintBuilder sub(Constraint sub) {
        constraint = new SubtractConstraint(constraint, sub);
        return this;
    }
    
    public ConstraintBuilder sub(PassByReference value) {
        constraint = new SubtractConstraint(constraint, new LinkedConstraint(value));
        return this;
    }

    public ConstraintBuilder div(float fixedValue) {
        constraint = new DivideConstraint(constraint, new FixedConstraint(fixedValue));
        return this;
    }

    public ConstraintBuilder div(Constraint div) {
        constraint = new DivideConstraint(constraint, div);
        return this;
    }
    
    public ConstraintBuilder div(PassByReference value) {
        constraint = new DivideConstraint(constraint, new LinkedConstraint(value));
        return this;
    }
}
