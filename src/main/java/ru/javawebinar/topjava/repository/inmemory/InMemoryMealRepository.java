package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> mealsMap = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Meal save(Meal meal, int userId) {
            if (meal.isNew()) {
                meal.setId(counter.incrementAndGet());
                meal.setUserId(userId);
                Meal existing = mealsMap.putIfAbsent(meal.getId(), meal);
                return existing == null ? meal : null;
            } else {
                Meal result = mealsMap.compute(meal.getId(), (id, existing) -> {
                    if (existing == null || existing.getUserId() != userId) {
                        return existing;
                    }
                    meal.setId(id);
                    meal.setUserId(userId);
                    return meal;
                });

                if (result != null && result.getUserId() == userId && Objects.equals(result.getId(), meal.getId())) {
                    return meal;
                }
                return null;
            }
        }

    @Override
    public boolean delete(int id, int userId) {
        return isOwner(userId, id) && mealsMap.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        if (!isOwner(userId, id)) {
            return null;
        }
        return mealsMap.get(id);
    }

    private boolean isOwner(int userId, int mealId) {
        Meal meal = mealsMap.get(mealId);
        return meal != null && userId == meal.getUserId();
    }

    @Override
    public List<Meal> getAll(int userId) {
        return mealsMap.values().stream()
                .filter(meal -> meal.getUserId() == userId)
                .sorted(Comparator.comparing(Meal::getDateTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}

