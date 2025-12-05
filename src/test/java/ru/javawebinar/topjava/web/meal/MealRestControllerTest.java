package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.user;
import static ru.javawebinar.topjava.util.MealsUtil.createTo;
import static ru.javawebinar.topjava.util.MealsUtil.getTos;

class MealRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = "/rest/profile/meals/";

    @Autowired
    private MealService mealService;

    @Test
    void create() throws Exception {
        Meal newMeal = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMeal)))
                .andExpect(status().isCreated());

        Meal created = JsonUtil.readValue(action.andReturn().getResponse().getContentAsString(), Meal.class);
        int newId = created.getId();
        newMeal.setId(newId);

        assertThat(created)
                .usingRecursiveComparison()
                .ignoringFields("user")
                .isEqualTo(newMeal);

        Meal dbMeal = mealService.get(newId, USER_ID);
        assertThat(dbMeal)
                .usingRecursiveComparison()
                .ignoringFields("user")
                .isEqualTo(newMeal);
    }

    @Test
    void get() throws Exception {
        String response = perform(MockMvcRequestBuilders.get(REST_URL + MEAL1_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Meal actual = JsonUtil.readValue(response, Meal.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("user")
                .isEqualTo(meal1);
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL1_ID, USER_ID));
    }

    @Test
    void update() throws Exception {
        Meal updated = getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        Meal dbMeal = mealService.get(MEAL1_ID, USER_ID);

        assertThat(dbMeal)
                .usingRecursiveComparison()
                .ignoringFields("user")
                .isEqualTo(updated);
    }

    @Test
    void getAll() throws Exception {
        String response = perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<MealTo> actual = JsonUtil.readValues(response, MealTo.class);
        List<MealTo> expected = getTos(meals, user.getCaloriesPerDay());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBetween() throws Exception {
        String url_part = "between?startDateTime=2020-01-30T07:00&endDateTime=2020-01-31T11:00:00";
        String response = perform(MockMvcRequestBuilders.get(REST_URL +  url_part))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<MealTo> actual = JsonUtil.readValues(response, MealTo.class);
        List<MealTo> expected = List.of(
                createTo(meal5, true),
                createTo(meal1, false)
        );
        assertThat(actual).isEqualTo(expected);
    }
}
