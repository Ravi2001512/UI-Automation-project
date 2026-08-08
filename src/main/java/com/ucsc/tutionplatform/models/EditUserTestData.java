package com.ucsc.tutionplatform.models;

public class EditUserTestData {

    private String userId;
    private String currentDisplayName;
    private String updatedDisplayName;

    public EditUserTestData() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(
            String userId
    ) {
        this.userId = userId;
    }

    public String getCurrentDisplayName() {
        return currentDisplayName;
    }

    public void setCurrentDisplayName(
            String currentDisplayName
    ) {
        this.currentDisplayName =
                currentDisplayName;
    }

    public String getUpdatedDisplayName() {
        return updatedDisplayName;
    }

    public void setUpdatedDisplayName(
            String updatedDisplayName
    ) {
        this.updatedDisplayName =
                updatedDisplayName;
    }
}