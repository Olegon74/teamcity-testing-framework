package com.example.teamcity.api.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.pages.ProjectPage;
import com.example.teamcity.ui.pages.ProjectsPage;
import com.example.teamcity.ui.pages.admin.CreateBuildTypePage;
import com.example.teamcity.ui.pages.admin.CreateProjectPage;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.*;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest{
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";

    @Test(description = "User should be able to create project", groups = {"Positive"})
    public void userCreatesProject() {
        // подготовка окружения
        loginAs(testData.getUser());

        // взаимодействие с UI
        step("Create Project");
        CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(testData.getProject().getName(),testData.getBuildType().getName());


        // проверка состояния API
        // (корректность отправки данных с UI на API)
        step("Check that all entities (project, build type) was successfully created with correct data on API level");
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).read("name:" + testData.getProject().getName());
        softy.assertNotNull(createdProject);

        // проверка состояния UI
        // (корректность считывания данных и отображение данных на UI)
        step("Check that project is visible on Projects Page (http://localhost:8111/favorite/projects)");
        ProjectPage.open(createdProject.getId())
                .title.shouldHave(Condition.exactText(testData.getProject().getName()));

        var foundProjects = ProjectsPage.open()
                .getProjects().stream()
                .anyMatch(project -> project.getName().text().equals(testData.getProject().getName()));

        softy.assertTrue(foundProjects);
    }

    @Test(description = "User should be able to build type,through the ui", groups = {"Positive"})
    public void userCreatesBuildType() {
        step("Create user");
        loginAs(testData.getUser());

        step("Create project");
        superUserCheckRequests.getRequest(PROJECTS).create((testData.getProject()));

        step("Check that all entities project, was successfully created with correct data on API level");
        Project createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).read("name:" + testData.getProject().getName());
        var projectId = createdProject.getId();

       step("Create Build Type");
       CreateBuildTypePage.open(projectId)
                .createForm(REPO_URL)
                .setupBuildType(testData.getBuildType().getName());

       step(" checking the API status, correctness of sending data from UI to API");
       var CreatedBuildType = superUserCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("name:" + testData.getBuildType().getName());
        softy.assertNotNull(CreatedBuildType);

    }

    @Test(description = "The user cannot create a build configuration without entering the address in the Repository URL input", groups = {"Negative"})
    public void creatingaBuildTypeWithAnEmptyInputRepositoryURL() {
        step("Create user");
        loginAs(testData.getUser());

        step("Create project");
        superUserCheckRequests.getRequest(PROJECTS).create((testData.getProject()));

        step("Check that all entities project, was successfully created with correct data on API level");
        Project createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).read("name:" + testData.getProject().getName());
        var projectId = createdProject.getId();

        step("Creating a Build Type without a completed Repository URL input");
        CreateBuildTypePage.open(projectId)
                .createForm1()
                .verifyUrlMustNotBeEmptyMessage();

    }
}
