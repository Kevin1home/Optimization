package org.BiT;

import java.util.Arrays;

// О методе "ветвей и границ":
// Идея заключается в оптимизированном переборе при помощи доп. ограничений для переменных X
// При X1>=7 будет добавлена дополнительная ограничительная строка с новой переменной X1-X5=7
public class BranchAndBound {
    Task task; // данные по задаче
    double[][] integerSolutions; // полученные целочисленные решения
    int integerSolutionsCounter = 0; // кол-во целочисленных решений
    double[] bestIntegerSolution; // лучшее целочисленное решение (смотрим по максимальной силе)
    double bestIntegerMaxPower = -1; // наивысший максимум целевой функции, учитывая все ограничения и целочисленность
    int calculationsCounter = 0; // подсчёт итераций симплекс-метода

    public BranchAndBound(Task task){
        this.task = task;
        integerSolutions = new double[10][task.costs.length + task.costs[0].length]; // число 10 было выбрано вручную
    }

    public double optimize() {
        System.out.println("\nВыбран вариант решения через метод \"ветвей и границ\""); // debug
        // Создаём массив условий для дальнейшей проработки
        Condition[] conditions = new Condition[task.costs[0].length];
        calculateMethod(this.task.clone(), conditions);

        System.out.println("\nКонец подсчёта метода \"ветвей и границ\""); // debug
        System.out.println("Количество итераций симплекс-метода: " + calculationsCounter); // debug
        System.out.println("Количество целочисленных решений: " + integerSolutionsCounter); // debug
        System.out.println("Полученные целочисленные решения: " + Arrays.deepToString(integerSolutions)); // debug
        System.out.println("Лучшее целочисленное решение: " + Arrays.toString(bestIntegerSolution)); // debug
        System.out.println("Максимальная сила с лучшим целочисленным решением: " + bestIntegerMaxPower); // debug

        return bestIntegerMaxPower;
    }

    private void calculateMethod(Task task, Condition[] conditions) {
        this.calculationsCounter++;
        System.out.println("\nЗаданные ограничения: " + Arrays.toString(conditions)); // debug
        // Решаем задачу симплекс-методом
        Simplex simplex = new Simplex(task.clone());
        simplex.optimize();
        // Сохраняем результаты в task
        task.maxPower = simplex.maxPower;
        task.solution = simplex.solution;

        // Проверяем имеется ли решение (если нет, то все изначальные X=0)
        if (task.maxPower == -1) {
            System.out.println("Нет решения с выбранными ограничениями, ветка отброшена"); // debug
            return;
        }
        // Проверяем решение на целочисленность
        if (isIntegerSolution(task.clone())) {
            integerSolutions[integerSolutionsCounter] = task.solution;
            integerSolutionsCounter++;
            System.out.println("Сохраняем целочисленное решение, ветка отброшена"); // debug
            if (bestIntegerMaxPower < task.maxPower) {
                bestIntegerMaxPower = task.maxPower;
                bestIntegerSolution = task.solution;
            }
            return;
        }
        System.out.println("Решение симплекс-метода НЕ целочисленное, проводим разветвление\n"); // debug

        // Находим индекс того X, значение которого самое близкое к следующему выше порядком числу
        int indexXHighestFraction = findHighestFraction(task.clone());
        System.out.println("indexXHighestFraction: " + indexXHighestFraction); // debug
        // Создаём новые условия
        if (indexXHighestFraction == -1) {
            System.out.println("Все условия были применены\n"); // debug
            return;
        }
        Condition conditionMore = new Condition(true, (int) task.solution[indexXHighestFraction] + 1);
        Condition[] conditionsMore = Arrays.copyOf(conditions, conditions.length);
        conditionsMore[indexXHighestFraction] = conditionMore;
        System.out.println("conditionsMore: " + Arrays.toString(conditionsMore)); // debug
        Condition conditionLess = new Condition(false, (int) task.solution[indexXHighestFraction]);
        Condition[] conditionsLess = Arrays.copyOf(conditions, conditions.length);
        conditionsLess[indexXHighestFraction] = conditionLess;
        System.out.println("conditionsLess: " + Arrays.toString(conditionsLess)); // debug
        // Создаём новые task с новыми неравенствами
        Task taskMore = addConditionToTask(task.clone(), conditionsMore);
        System.out.println("taskMore: " + taskMore); // debug
        Task taskLess = addConditionToTask(task.clone(), conditionsLess);
        System.out.println("taskLess: " + taskLess); // debug
        // Снова просчитываем
        calculateMethod(taskMore, conditionsMore);
        calculateMethod(taskLess, conditionsLess);
    }

    private Task addConditionToTask(Task task, Condition[] conditions) {
        // Подсчитываем кол-во условий
        int numberOfConditions = 0;
        for (Condition condition : conditions) {
            if (condition != null) {
                numberOfConditions++;
            }
        }
        // Обновляем task
        task.costs = this.task.costs;
        task.resources = this.task.resources;
        // Добавляем условия в матрицу расходов
        double[][] costsModified = new double[this.task.costs.length + numberOfConditions][task.costs[0].length];
        Condition[] conditionsTmp = Arrays.copyOf(conditions, conditions.length);
        for (int i = 0; i < costsModified.length; i++) {
            for (int j = 0; j < costsModified[0].length; j++) {
                if (i < this.task.costs.length) {
                    costsModified[i][j] = this.task.costs[i][j];
                } else {
                    // Для инфо:
                    // Если условие X1>=7, то получаем уравнение 1*X1 + 0*X2 + 0*X3 - 1*X6 = 7 (где Х6 доп. переменная)
                    // Но нам нужно чтобы было <=, а значит -1*X1 - 0*X2 - 0*X3 + 1*X6 = -7
                    if (conditionsTmp[j] == null) {
                        costsModified[i][j] = 0;
                    } else {
                        if (conditionsTmp[j].moreOrEqual) {
                            costsModified[i][j] = -1;
                        } else {
                            costsModified[i][j] = 1;
                        }
                        conditionsTmp[j] = null;
                        break;
                    }
                }
            }
        }
        // Добавляем условия в результаты неравенств
        double[] resourcesModified = new double[this.task.resources.length + numberOfConditions];
        for (int i = 0; i < resourcesModified.length; i++) {
            if (i < this.task.resources.length) {
                resourcesModified[i] = this.task.resources[i];
            } else {
                for (int j = 0; j < conditions.length; j++) {
                    if (conditions[j] != null) {
                        if (conditions[j].moreOrEqual) {
                            resourcesModified[i] = conditions[j].value * -1;
                        } else {
                            resourcesModified[i] = conditions[j].value;
                        }
                        i++;
                    }
                }
            }
        }
        // Возвращаем новую задачу
        return new Task(costsModified, resourcesModified, task.unitsPower);
    }

    private int findHighestFraction(Task task) {
        int indexOfHighestFraction = -1;
        double highestFraction = -1;
        for (int i = 0; i < this.task.costs[0].length; i++) { // перебираем только изначальные X
            if (highestFraction < task.solution[i] - (int) task.solution[i]) {
                highestFraction = task.solution[i] - (int) task.solution[i];
                indexOfHighestFraction = i;
            }
        }
        return indexOfHighestFraction;
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
