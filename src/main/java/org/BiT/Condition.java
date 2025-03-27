package org.BiT;

// Вспомогательный класс для обозначения дополнительных условий
// Используется в BranchAndBound
public class Condition {
    boolean moreOrEqual; // if false then lessOrEqual
    int value;

    public Condition(boolean moreOrEqual, int value) {
        this.moreOrEqual = moreOrEqual;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "moreOrEqual=" + moreOrEqual +
                ", value=" + value +
                '}';
    }
}
