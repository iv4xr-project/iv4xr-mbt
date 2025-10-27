package eu.fbk.iv4xr.mbt.efsm.exp;

public class IfThenElseOp<T> extends TernaryOp<T> {
    public IfThenElseOp(Exp<Boolean> condition, Exp<T> thenExp, Exp<T> elseExp) {
        super(condition, thenExp, elseExp);
    }

    @Override
    public Const<T> eval() {
        if (((Exp<Boolean>) getParameter1()).eval().getVal()) {
            return new Const<T>(((Exp<T>) getParameter2()).eval().getVal());
        } else {
            return new Const<T>(((Exp<T>) getParameter3()).eval().getVal());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof IfThenElseOp) {
            IfThenElseOp i = (IfThenElseOp) o;
            return  this.getParameter1().equals(i.getParameter1()) &&
                    this.getParameter2().equals(i.getParameter2()) &&
                    this.getParameter3().equals(i.getParameter3());
        }
        return false;
    }

    public boolean equalsUpToValue(Object o){
        if (o == this) {
            return true;
        }
        if (o instanceof IfThenElseOp) {
            IfThenElseOp i = (IfThenElseOp) o;
            return  this.getParameter1().equalsUpToValue(i.getParameter1()) &&
                    this.getParameter2().equalsUpToValue(i.getParameter2()) &&
                    this.getParameter3().equalsUpToValue(i.getParameter3());
        }
        return false;
    }


    public String toDebugString() {
        return getParameter1().toDebugString() + "?" + getParameter2().toDebugString() + ":"
                + getParameter3().toDebugString();
    }
}