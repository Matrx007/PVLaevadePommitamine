package com.meietiim.pvlaevadepommitamine.gui.constraints;

public class LinkedConstraint extends Constraint {
    
    private PassByReference value;
    
    public LinkedConstraint(PassByReference value) {
        this.value = value;
    }
    
    @Override
    public float calculate() {
        return value.value();
    }
}
