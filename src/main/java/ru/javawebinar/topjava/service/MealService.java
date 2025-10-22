package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFound;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, int userId) {
        return repository.save(meal, userId);
    }

    public void update(Meal meal, int userId) {
        checkNotFound(repository.save(meal, userId), meal.getId());
    }

    public void delete(int id, int userId) {
        checkNotFound(repository.delete(id, userId), id);
    }

    public Meal get(int id, int userId) {
        return checkNotFound(repository.get(id, userId), id);
    }

    public List<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }

    public List<Meal> getBetween(LocalDate startDate, LocalDate endDate, int userId) {
        LocalDateTime start = Optional.ofNullable(startDate)
                .map(LocalDate::atStartOfDay)
                .orElse(LocalDateTime.MIN);
        LocalDateTime end = Optional.ofNullable(endDate)
                .map(date -> date.plusDays(1).atStartOfDay())
                .orElse(LocalDateTime.MAX);
        return repository.getBetweenHalfOpen(start, end, userId);
    }
}
