package com.example.teamcity.models;

import lombok.Data;


@Data
public class TestData {
    private Project project;
    private User user;
    private BuildType buildType;

}
