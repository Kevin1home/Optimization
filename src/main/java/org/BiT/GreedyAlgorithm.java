package org.BiT;

import java.util.Arrays;
import java.util.Comparator;

// Об идее с "жадным алгоритмом":
// Идея заключается в оптимизированном переборе "около" оптимального симплекс решения
// Округления вверх начинается с того существа, которое самое полезное по отношению стоимость / сила
// Алгоритм оптимально подходит под подсчёты для тематики героев, но может быть не всегда оптимальным в иных случаях
public class GreedyAlgorithm {
    Task task; // данные по задаче
    double[] bestIntegerSolution; // лучшее целочисленное решение (смотрим по максимальной силе)
    int integerSolutionsCounter = 0; // кол-во целочисленных решений
    double bestIntegerMaxPower = -1; // наивысший максимум целевой функции, учитывая все ограничения и целочисленность
    int calculationsCounter = 0; // подсчёт внутренних итераций калькуляции

    public GreedyAlgorithm(Task task){
        this.task = task;
    }

    public void optimize() {
        System.out.println("\nВыбран вариант решения через \"жадный алгоритм\"");

        // Решаем задачу симплекс-методом
        Simplex initial_simplex = new Simplex(this.task);
        initial_simplex.optimize();
        this.task.solution = initial_simplex.solution;
        this.task.maxPower = initial_simplex.maxPower;

        // Проверяем решение на целочисленность
        if (isIntegerSolution(this.task.clone())) {
            System.out.println("Изначальное решение симплекс-методом уже целочисленное"); // debug
            return;
        }
        System.out.println("Изначальное решение НЕцелочисленное, используем жадный алгоритм\n"); // debug

        // Округляем решение вниз
        double[] newSolution = new double[this.task.solution.length];
        for (int i = 0; i < newSolution.length; i++) {
            newSolution[i] = (int) this.task.solution[i];
        }
        bestIntegerSolution = newSolution;
        System.out.println("Округлённое вниз решение: " + Arrays.toString(newSolution)); // debug
        integerSolutionsCounter++; // первое целочисленное решение

        // Высчитываем коэф. полезности существа (сила существа / сумма ресурсов на существо)
        KValue[] kValues = new KValue[this.task.unitsPower.length];
        for (int i = 0; i < kValues.length; i++) {
            double sumResourcesUnit = 0;
            for (int j = 0; j < this.task.resources.length; j++) {
                sumResourcesUnit += this.task.costs[i][j];
            }
            double kValueResult = this.task.unitsPower[i]/sumResourcesUnit;
            kValues[i] = new KValue(i, kValueResult);
        }
        Arrays.sort(kValues, Comparator.comparingDouble(k -> -k.value));

        // Подбираем новые решения и проверяем их
        for (int i = 0; i < kValues.length; i++) {
            calculationsCounter++;
            newSolution[kValues[i].i] += 1;
            System.out.println("\nНовое решение: " + Arrays.toString(newSolution)); // debug
            if (!checkSolution(newSolution)) {
                newSolution[kValues[i].i] -= 1;
            } else { // если решение подошло, то попробуем ещё поднять
                System.out.println("Решение не противоречит ограничениям"); // debug
                integerSolutionsCounter++;
                i--;
            }
        }

        // Подсчитываем итоговую силу
        for (int j = 0; j < this.task.unitsPower.length; j++) {
            this.bestIntegerMaxPower += (newSolution[j] * this.task.unitsPower[j]);
        }

        System.out.println("\nКонец подсчёта \"жадного алгоритма\""); // debug
        System.out.println("Количество итераций симлекс-метода: " + 1); // debug
        System.out.println("Количество внутренних итераций калькуляции: " + calculationsCounter); // debug
        System.out.println("Количество целочисленных решений: " + integerSolutionsCounter); // debug
        System.out.println("Полученное целочисленное решение: " + Arrays.toString(bestIntegerSolution)); // debug
        System.out.println("Максимальная сила с полученным целочисленным решением: " + bestIntegerMaxPower); // debug
    }

    private boolean checkSolution(double[] solution) {
        for (int i = 0; i < this.task.costs.length; i++) {
            double rowResult = 0;
            for (int j = 0; j < this.task.costs[0].length; j++) {
                rowResult += this.task.costs[i][j] * solution[j];
            }
            if (rowResult > this.task.resources[i]) {
                System.out.println("При проверке решения возникла ошибка. Условия нарушены");
                return false;
            }
        }
        return true;
    }

    private boolean isIntegerSolution(Task task) {
        for (int i = 0; i < this.task.costs[0].length; i++) { // перебираем только изначальные X
            if (task.solution[i] != (int) task.solution[i]) {
                return false;
            }
        }
        return true;
    }

}
