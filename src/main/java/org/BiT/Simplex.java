package org.BiT;

import java.util.Arrays;

// Краткая информация о симплекс-методе:
// Базисные переменные - изначально X4, X5, X6 (добавленные)
// Небазисные переменные - изначально X1, X2, X3 (имеющиеся сразу)
// Базисное решение канонической формы соответствует некой вершине симплекса
// Если все небазисные переменные приравнять к 0, то базисные переменные будут равны суммам ограничений
public class Simplex {
    double[][] costs; // матрица стоимости существ (т.е. матрица ограничительных неравенств)
    double[] resources;  // имеющиеся ресурсы (т.е. сами суммы ограничений неравенств)
    double[] unitsPower; // сила существ (т.е. целевая функция)
    double[] solution; // хранятся значения всех полученных X
    double maxPower = 0; // максимальная сила отряда (т.е.максимум целевой функции)

    public Simplex(Task task){
        this.costs = task.costs;
        this.resources = task.resources;
        this.unitsPower = task.unitsPower;
    }

    public void optimize() {
        System.out.println("\nНачало симплекс-метода"); // debug
        System.out.println("Начальные данные: "); // debug
        System.out.println("Costs (матрица ограничений): " + Arrays.deepToString(costs)); // debug
        System.out.println("Resources (суммы ограничений): " + Arrays.toString(resources)); // debug
        System.out.println("UnitsPower (целевая функция): " + Arrays.toString(unitsPower)+"\n"); // debug

        // Расширяем целевую функцию коэффициентами базисных переменных X4, X5, X6
        // Все коэффициенты пока что равны 0
        int rows = costs.length; // кол-во строк матрицы стоимости существ (т.е. ограничительных неравенств)
        int cols = costs[0].length; // кол-во столбцов матрицы стоимости существ (т.е. все X)
        double[] fullUnitsPower = new double[rows+cols];
        for (int i = 0; i < fullUnitsPower.length; i++){
            if (i < unitsPower.length){
                fullUnitsPower[i] = unitsPower[i];
            }
            else {
                fullUnitsPower[i] = 0;
            }
        }
        System.out.println("Расширенная целевая функция: " + Arrays.toString(fullUnitsPower)); // debug

        // Расширяем матрицу ограничительных неравенств базисными переменными X4, X5, X6 в виде единичной матрицы
        double[][] fullCosts = new double[rows][rows+cols];
        int k = cols; // вспомогательный коэф. для заполнения един. матрицы
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < rows + cols; j++) {
                if (j < cols) {
                    fullCosts[i][j] = costs[i][j];
                } else {
                    if(j == k) {
                        fullCosts[i][j] = 1;
                    } else {
                        fullCosts[i][j] = 0;
                    }
                }
            }
            k++;
        }
        System.out.println("Расширили матрицу единичной матрицей: " + Arrays.deepToString(fullCosts)); // debug

        // Теперь понимаем какой размер будет у решения вместе с дополнительными X (но нужны будут только изначальные)
        solution = new double[fullCosts[0].length];

        // Сохраняем индексы доп. Х, т.е. изначальных базисных переменных (изначально 0: 3(X4), 1: 4(X5), 2: 5(X6))
        int[] basis = new int[rows];
        int basInd = cols;
        for (int i = 0; i < rows; i++){
            basis[i] = basInd++;
        }
        System.out.println("Заполнили базис: " + Arrays.toString(basis)+"\n"); // debug

        // Создаём копию массива ограничений для дальнейших изменений
        double[] tmpResources = new double[resources.length];
        for (int i = 0; i < tmpResources.length; i++) {
            tmpResources[i] = resources[i];
        }

        // Техническая переменная для контроля над совместимостью ограничений
        // Если после замены всех имеющихся негативных ресурсов всё равно остаются негативные, то решения нет
        int interationCounter = getNumberOfNegativeResources();
        while (true) { // while is not optimal
            System.out.println("Начало интерации"); // debug

            // 0. Проверяем ограничения
            if (interationCounter < 0) {
                System.out.println("Решения для данных ограничений нет");
                this.maxPower = -1;
                return;
            }
            int leavingVarIndex = -1;
            int enteringVarIndex = -1;
            for (int i = 0; i < this.resources.length; i++) {
                if (tmpResources[i] < 0) {
                    System.out.println("Найден отрицательный ресурс: " + tmpResources[i]);
                    interationCounter--;
                    int nonZeroXCounter = 0;
                    for (int j = 0; j < this.costs[0].length; j++) {
                        if (fullCosts[i][j] != 0) {
                            enteringVarIndex = j;
                            nonZeroXCounter++;
                        }
                    }
                    if (nonZeroXCounter != 1) {
                        System.out.println("Решения для данных ограничений нет");
                        this.maxPower = -1;
                        return;
                    }
                    leavingVarIndex = i;
                    break;
                }
            }

            // 1. Выбираем наибольшую положительную небазисную переменную в целевой функции, которая войдёт в базис
            if (enteringVarIndex == -1) {
                double enteringVarValue = -1;
                for (int i = 0; i < cols; i++) {
                    if (fullUnitsPower[i] > 0) {
                        if (enteringVarValue < fullUnitsPower[i]) {
                            enteringVarValue = fullUnitsPower[i];
                            enteringVarIndex = i;
                        }
                    }
                }
            }
            // Если такой "входящей" переменной нет, значит переменные в цел. ф-ции не увеличивают решение
            // А значит, что решение оптимально
            if (enteringVarIndex == -1) {
                System.out.println("Конец интерации, нет входящих переменных\n"); // debug
                break;
            }
            System.out.println("Выбрали индекс входящей в базис переменной: " + enteringVarIndex); // debug

            // 2. Выбираем "выходящую" из базиса базисную переменную
            // Для этого остальные небазисные переменные будут 0 (кроме входящей)
            // И дальше выбираем по минимальному tmpResources[i] / fullCosts[i][enteringVarIndex]
            if (leavingVarIndex == -1) {
                double minResult = Double.POSITIVE_INFINITY; // если нужен минимум, то начинаем с полож. бесконечности
                for (int i = 0; i < rows; i++) {
                    if (fullCosts[i][enteringVarIndex] > 0) {
                        double result = tmpResources[i] / fullCosts[i][enteringVarIndex];
                        if (result < minResult) {
                            minResult = result;
                            leavingVarIndex = i;
                        }
                    }
                }
            }
            // Если для выходящей переменной нет строки с ограничением, то решение не ограничено
            if (leavingVarIndex == -1) {
                solution = new double[fullCosts[0].length];
                System.out.println("Конец интерации, нет строки с ограничением для выходящей переменной\n"); // debug
                System.out.println("Решение не ограничено!\n"); // debug
                break;
            }
            System.out.println("Выбрали индекс выходящей из базиса базисной переменной: " + leavingVarIndex); // debug

            // 3. Обновляем базис (меняем выходящую переменную на входящую)
            basis[leavingVarIndex] = enteringVarIndex; // Обновляем базис
            System.out.println("Обновили базис: " + Arrays.toString(basis)); // debug

            // 4. Обновляем каноническую форму всех неравенств и целевой функции

            // 4.1. Обновляем выбранное неравенство
            // Т.е. меняем выходящую из базиса и входящую в базис переменные местами во всех неравенствах и цел. ф-ции
            // Для инфо: процедура PIVOT используется для математических перестановок переменных
            // Например, как в данном случае, когда мы обновляем каноническую форму
            double pivot = fullCosts[leavingVarIndex][enteringVarIndex];
            for (int j = 0; j < rows+cols; j++) {
                fullCosts[leavingVarIndex][j] /= pivot;
            }
            tmpResources[leavingVarIndex] /= pivot;

            // 4.2. Обновление остальных неравенств
            for (int i = 0; i < rows; i++) {
                if (i != leavingVarIndex) {
                    double factor = fullCosts[i][enteringVarIndex];
                    for (int j = 0; j < rows+cols; j++) {
                        fullCosts[i][j] -= fullCosts[leavingVarIndex][j] * factor;
                    }
                    tmpResources[i] -= tmpResources[leavingVarIndex] * factor;
                }
            }

            // 4.3. Обновление целевой функции
            double factor = fullUnitsPower[enteringVarIndex];
            for (int j = 0; j < rows+cols; j++) {
                fullUnitsPower[j] -= fullCosts[leavingVarIndex][j] * factor;
            }
            System.out.println("Обновили матрицу ограничений: " + Arrays.deepToString(fullCosts)); // debug
            System.out.println("Обновили суммы ограничений: " + Arrays.toString(tmpResources)); // debug
            System.out.println("Обновили целевую функцию: " + Arrays.toString(fullUnitsPower)); // debug
            System.out.println("Конец интерации, данные обновлены\n"); // debug

        }

        if (this.maxPower == -1) {
            return;
        }

        // Заполняем итоговые значения переменных (значения для всех X)
        for (int i = 0; i < rows; i++) {
            solution[basis[i]] = tmpResources[i];
        }

        // Вывод
        System.out.println("Оптимальное решение:");
        for (int i = 0; i < rows+cols; i++) {
            System.out.println("X" + (i + 1) + " = " + solution[i]);
        }
        for (int i = 0; i < cols; i++) {
            maxPower += (solution[i] * unitsPower[i]);
        }
        System.out.println("Максимальная сила: " + maxPower);
        System.out.println("Конец симплекс-метода\n");

    }

    private int getNumberOfNegativeResources() {
        int counter = 0;
        for (int i = 0; i < this.resources.length; i++) {
            if (resources[i] < 0) {
                counter++;
            }
        }
        return counter;
    }

}
