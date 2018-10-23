package br.nataliakt.e2048.model;

public enum MovimentEnum {

    LEFT(0),
    UP(1),
    RIGHT(2),
    DOWN(3);

    private int value;

    MovimentEnum(int value) {
        this.value = value;
    }

    public static MovimentEnum find(int value) {
        for (MovimentEnum movimentEnum : MovimentEnum.values()) {
            if (movimentEnum.getValue() == value) {
                return movimentEnum;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
