package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.MealTo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.MealsUtil.filteredByStreams;
import static ru.javawebinar.topjava.util.MealsUtil.getMeals;

public class MealServlet extends HttpServlet {
    private  static final Logger log = getLogger(MealServlet.class);
    private static final int CALORIES_PER_DAY = 2000;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("redirect to meal");
        List<MealTo> mealsTo = filteredByStreams(getMeals(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
        req.setAttribute("mealsTo", mealsTo);
        req.getRequestDispatcher("/meals.jsp").forward(req, resp);
    }
}
