package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> userMealMap = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Meal save(Meal meal, int userId) {
            Map<Integer, Meal> userMeals = userMealMap.computeIfAbsent(userId,
                    uId -> new ConcurrentHashMap<>());
            if (meal.isNew()) {
                meal.setId(counter.incrementAndGet());
                Meal existing = userMeals.putIfAbsent(meal.getId(), meal);
                return existing == null ? meal : null;
            }
            return userMeals.replace(meal.getId(), meal) != null ? meal : null;
        }

    @Override
    public boolean delete(int id, int userId) {
        return userMealMap.computeIfPresent(userId, (uId, meals) -> {
            meals.remove(id);
            return meals.isEmpty() ? null : meals;
        }) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        return Optional.ofNullable(userMealMap.get(userId))
                .map(meals -> meals.get(id))
                .orElse(null);
    }

    public List<Meal> getAll(int userId) {
        return getBetweenHalfOpen(null, null, userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return Optional.ofNullable(userMealMap.get(userId))
                .map(Map::values)
                .filter(values -> !values.isEmpty())
                .map(values -> values.stream()
                        .filter(meal -> DateTimeUtil.isBetweenHalfOpen(meal.getDateTime(), startDateTime, endDateTime))
                        .sorted(Comparator.comparing(Meal::getDateTime, Comparator.reverseOrder()))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}

