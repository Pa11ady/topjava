<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://example.com/functions" prefix="f" %>

<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
 <table border=1>
        <thead>
            <tr>
                <th>Date</th>
                <th>Description</th>
                <th>Calories</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${mealsTo}" var="mealTo">
                 <tr style="color: ${mealTo.excess ? 'red' : 'green'};">
                    <td>${f:format(mealTo.dateTime)}</td>
                    <td>${mealTo.description}</td>
                    <td>${mealTo.calories}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
