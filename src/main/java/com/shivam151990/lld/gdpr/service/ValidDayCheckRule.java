package com.shivam151990.lld.gdpr.service;

import com.shivam151990.lld.gdpr.model.Activity;
import com.shivam151990.lld.gdpr.model.UserActivity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidDayCheckRule implements Rule {

    private int maxDays;

    public ValidDayCheckRule(int maxDays) {
        this.maxDays = maxDays;
    }

    public boolean canDelete(List<UserActivity> userActivity) {
        Map<String, List<UserActivity>> activities = new HashMap<>();
        for (UserActivity activity: userActivity) {
            activities.computeIfAbsent(activity.getUserId(), l -> new ArrayList<>()).add(activity);
        }
        LocalDate curr = LocalDate.now();
        for (Map.Entry<String, List<UserActivity>> entry: activities.entrySet()) {
            String curUser = entry.getKey();
            if (entry.getValue().size() == 1 && userActivity.getFirst().getActivity() == Activity.MARKET_EMAIL) {
                return false;
            }
            for (UserActivity activity: entry.getValue()) {
                long difference = ChronoUnit.DAYS.between(activity.getActivityDate(), curr);
                if (difference <= maxDays) {
                    return false;
                }
            }
        }
        return true;
    }
}
