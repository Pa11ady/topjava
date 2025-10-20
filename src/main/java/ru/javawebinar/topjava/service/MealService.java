package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.Collection;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal save(Meal meal) {
        Meal result = repository.save(meal);
        if (result == null) {
            throw new NotFoundException("The food does not belong to the user or does not exist");
        }
        return result;
    }

    public boolean delete(int id, int userId) {
        if (!repository.delete(id, userId)) {
            throw new NotFoundException("The food does not belong to the user or does not exist");
        }
        return true;
    }

    public Meal get(int id, int userId) {
        Meal result = repository.get(id, userId);
        if (result == null) {
            throw new NotFoundException("The food does not belong to the user or does not exist");
        }
        return result;
    }

    public Collection<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }
}