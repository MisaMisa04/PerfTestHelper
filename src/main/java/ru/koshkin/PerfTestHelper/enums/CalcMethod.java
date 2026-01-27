package ru.koshkin.PerfTestHelper.enums;

public enum CalcMethod {
    AUTO, CTT, THREADS;

    public static CalcMethod fromOrdinal(Integer i) {
        if (i == null) return CalcMethod.AUTO;
        return CalcMethod.values()[i];
    }
}
