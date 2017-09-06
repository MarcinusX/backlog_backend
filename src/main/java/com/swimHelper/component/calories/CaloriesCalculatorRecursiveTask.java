package com.swimHelper.component.calories;

import com.swimHelper.model.ExerciseSeries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Created by mstobieniecka on 2017-09-05.
 */
public class CaloriesCalculatorRecursiveTask extends RecursiveTask<Integer> {
    private List<ExerciseSeries> exerciseSeriesList;
    private CaloriesCalculator caloriesCalculator;
    private double weight;

    private static final int THRESHOLD = 2;

    public CaloriesCalculatorRecursiveTask(List<ExerciseSeries> exerciseSeriesList, double weight, CaloriesCalculator caloriesCalculator) {
        this.exerciseSeriesList = exerciseSeriesList;
        this.weight = weight;
        this.caloriesCalculator = caloriesCalculator;
    }

    @Override
    protected Integer compute() {
        if (exerciseSeriesList.size() > THRESHOLD) {
            return ForkJoinTask.invokeAll(createSubtasks())
                    .stream()
                    .mapToInt(ForkJoinTask::join)
                    .sum();
        } else {
            return process(exerciseSeriesList);
        }
    }

    private Collection<CaloriesCalculatorRecursiveTask> createSubtasks() {
        List<CaloriesCalculatorRecursiveTask> dividedTasks = new ArrayList<>();
        dividedTasks.add(new CaloriesCalculatorRecursiveTask(
                exerciseSeriesList.subList(0, exerciseSeriesList.size() / 2),
                weight, caloriesCalculator));
        dividedTasks.add(new CaloriesCalculatorRecursiveTask(
                exerciseSeriesList.subList(exerciseSeriesList.size() / 2, exerciseSeriesList.size()),
                weight, caloriesCalculator));
        return dividedTasks;
    }

    private Integer process(List<ExerciseSeries> exerciseSeriesList) {
        return exerciseSeriesList
                .stream()
                .mapToInt(es -> caloriesCalculator.calculateCalories(es, weight))
                .sum();
    }
}
