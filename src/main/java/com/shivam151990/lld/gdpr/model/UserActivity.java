package com.shivam151990.lld.gdpr.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserActivity {

    private String userId;
    private Activity activity;
    private LocalDate activityDate;

    public UserActivity(String userId, Activity activity, LocalDate activityDate) {
        this.userId = userId;
        this.activity = activity;
        this.activityDate = activityDate;
    }
}
