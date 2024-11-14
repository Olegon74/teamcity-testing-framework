package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;

public class CreateBuildTypePage extends CreateBasePage {
    private static final String BUILD_TYPE_SHOW_MODE = "createBuildTypeMenu";
    private SelenideElement errorMessage = $("#error_url");

    public static CreateBuildTypePage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, BUILD_TYPE_SHOW_MODE), CreateBuildTypePage.class);
    }

    public CreateBuildTypePage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public CreateBuildTypePage createForm1() {
        baseCreateForm1();
        return this;
    }


    public void setupBuildType(String buildTypeName) {
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
    }

    public CreateBuildTypePage verifyUrlMustNotBeEmptyMessage() {
        errorMessage.shouldBe(Condition.visible);
        errorMessage.shouldHave(Condition.text("URL must not be empty"));
        return this;
    }



}
