package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping("/meals")
public class JspMealController extends AbstractMealController {

    @GetMapping("/create")
    public String create(Model model) {
        Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping("/update")
    public String update(@RequestParam int id, Model model) {
        model.addAttribute("meal", super.get(id));
        return "mealForm";
    }

    @GetMapping("/delete")
    public String deleteJSP(@RequestParam int id) {
        super.delete(id);
        return "redirect:/meals";
    }

    @PostMapping
    public String createOrUpdate(
            @RequestParam(required = false) Integer id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestParam String description,
            @RequestParam int calories) {

        Meal meal = new Meal(dateTime, description, calories);
        if (id == null) {
            super.create(meal);
        } else {
            super.update(meal, id);
        }
        return "redirect:/meals";
    }

    @GetMapping("/filter")
    public String getBetween(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            Model model) {

        LocalDate startD = parseLocalDate(startDate);
        LocalDate endD = parseLocalDate(endDate);
        LocalTime startT = parseLocalTime(startTime);
        LocalTime endT = parseLocalTime(endTime);
        model.addAttribute("meals", super.getBetween(startD, startT, endD, endT));
        return "meals";
    }
}
