package org.BiT;

import java.util.Arrays;

// Вспомогательный класс для сокращения кода
// Поля аналогично классу Simplex
public class Task {
    double[][] costs;
    double[] resources;
    double[] unitsPower;
    double[] solution;
    double maxPower = 0;

    public Task(double[][] costs, double[] resources, double[] unitsPower) {
        this.costs = costs;
        this.resources = resources;
        this.unitsPower = unitsPower;
        this.solution = new double[costs.length + costs[0].length];
    }

    public Task clone() {
        Task cloneTask = new Task(Arrays.copyOf(costs, costs.length), Arrays.copyOf(resources, resources.length),
                Arrays.copyOf(unitsPower, unitsPower.length));
        cloneTask.solution = Arrays.copyOf(solution, solution.length);
        cloneTask.maxPower = maxPower;
        return cloneTask;
    }

    @Override
    public String toString() {
        return "Task{" +
                "costs=" + Arrays.deepToString(costs) +
                ", resources=" + Arrays.toString(resources) +
                ", unitsPower=" + Arrays.toString(unitsPower) +
                ", solution=" + Arrays.toString(solution) +
                ", maxPower=" + maxPower +
                '}';
    }

}
