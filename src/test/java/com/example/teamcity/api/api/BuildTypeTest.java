package com.example.teamcity.api.api;

//import com.example.teamcity.api.models.*;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import jdk.jfr.Description;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {
    @Description("Пользователь создает сборку")
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Description("Создание двух типов сборки с одинаковыми id")
    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {

        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())));

    }

    @Description("Администратор проекта, создает типы сборки для своего проекта")
    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {

        step("Create project");
        superUserCheckRequests.getRequest(PROJECTS).create((testData.getProject()));//суперюзером создаем проект

        step("Create user");
        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));//суперюзером создаем юзера с ролью PROJECT_ADMIN, для ранее созданного проекта
        superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());//берем id созданного юзера, для следующего запроса

        step("Create buildType for project by user (PROJECT_ADMIN)");
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());//юзером с ролью PROJECT_ADMIN, создаем BuildType для ранее созданного проекта

        step("Create buildType for project by user (PROJECT_ADMIN)");
        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());//get запросом по id созданного BuildType проверяем, что возвращается необходимый id и статус код 200
        step("Check buildType was created successfully");

        softy.assertEquals(testData.getBuildType().getName(),createdBuildType.getName(), "Build type name is not correct");

    }

    @Description("Администратор проекта, не может создавать тип сборки для чужого проекта")
    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        step("Create project1 and assign user1 as PROJECT_ADMIN");
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject()); // Создаем проект1 и назначаем user1 администратором этого проекта
        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        var user1 = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());

        step("Create project2 and assign user2 as PROJECT_ADMIN");
        Project project2 = generate(Project.class);// Создаем проект2 и назначаем user2 администратором этого проекта
        superUserCheckRequests.getRequest(PROJECTS).create(project2);
        var user2 = generate(User.class);
        user2.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + project2.getId()));
        superUserCheckRequests.<User>getRequest(USERS).create(user2);

        step("Create buildType for project1 by user2");
        var userCheckRequests = new UncheckedRequests(Specifications.authSpec(user2));// Пытаемся создать buildType для проекта1 пользователем user2, у которого нет прав на проект1

        step("We check that we cannot create an assembly type for project 1 by user 2");
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("You do not have enough permissions to edit project with id: %s".formatted(testData.getProject().getId())));

    }

}
