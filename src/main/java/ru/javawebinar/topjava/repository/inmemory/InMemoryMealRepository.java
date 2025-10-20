package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> mealsMap = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(this::save);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
        } else if (!isOwner(meal.getUserId(), meal.getId())) {
            return null;
        }
        mealsMap.put(meal.getId(), meal);
        return meal;
    }

    private boolean isOwner(int userId, int mealId) {
        Meal meal = mealsMap.get(mealId);
        return meal != null && userId == meal.getUserId();
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

    @Override
    public Collection<Meal> getAll(int userId) {
        return mealsMap.values().stream()
                .filter(meal -> meal.getUserId() == userId)
                .sorted(Comparator.comparing(Meal::getDateTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}

