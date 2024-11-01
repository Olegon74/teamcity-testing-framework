package com.example.teamcity.enums;

import com.example.teamcity.models.BaseModel;
import com.example.teamcity.models.BuildType;
import com.example.teamcity.models.Project;
import com.example.teamcity.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {
    BUILD_TYPES("/app/rest/buildTypes", BuildType.class),
    PROJECTS("/app/rest/projects",Project.class),
    USERS("/app/rest/users",User.class);


    private final String url;
    private final Class<? extends BaseModel> modelClass;
}
