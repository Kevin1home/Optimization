package org.BiT;

// Вспомогательный класс для хранения коэффициента полезности существа
// Запоминает индекс переменной X, что необходимо для сортировки
// Используется в GreedyAlgorithm
public class KValue {
    int i;
    double value;

    public KValue(int i, double value) {
        this.i = i;
        this.value = value;
    }

    @Override
    public String toString() {
        return "KValue{" +
                "i=" + i +
                ", value=" + value +
                '}';
    }

}
