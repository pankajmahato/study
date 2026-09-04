package com.shivam151990.lld.gdpr.service;
import com.shivam151990.lld.gdpr.model.UserActivity;

import java.util.*;

public class UserDeletionService {

    private final List<Rule> rules;

    public UserDeletionService(List<Rule> rules) {
        this.rules = rules;
    }

    public Set<String> findUsersReadyForDeletion(List<UserActivity> activities) {
        Set<String> users = new HashSet<>();
        Map<String, List<UserActivity>> activitiesMap = new HashMap<>();
        for (UserActivity activity: activities) {
            activitiesMap.computeIfAbsent(activity.getUserId(), l -> new ArrayList<>()).add(activity);
        }
        for (Map.Entry<String, List<UserActivity>> entry: activitiesMap.entrySet()) {
            for (Rule currentRule: rules) {
                if (currentRule.canDelete(entry.getValue())) {
                    users.add(entry.getKey());
                    break;
                }
            }
        }
        return users;
    }
}
