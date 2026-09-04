package com.shivam151990.lld.gdpr;

import com.shivam151990.lld.gdpr.model.Activity;
import com.shivam151990.lld.gdpr.model.UserActivity;
import com.shivam151990.lld.gdpr.service.Rule;
import com.shivam151990.lld.gdpr.service.UserDeletionService;
import com.shivam151990.lld.gdpr.service.ValidDayCheckRule;

import java.time.LocalDate;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        List<UserActivity> userActivities = List.of(
            new UserActivity("u1", Activity.MARKET_EMAIL, LocalDate.of(2025, 8, 1)),
            new UserActivity("u2", Activity.MARKET_EMAIL, LocalDate.of(2025, 2, 1)),
            new UserActivity("u1", Activity.MOBILE_APP_LOGIN, LocalDate.of(2025, 3, 1)),
            new UserActivity("u1", Activity.WEBSITE_LOGIN, LocalDate.of(2025, 7, 1))
        );
        Rule rule1 = new ValidDayCheckRule(90);
        UserDeletionService userDeletionService = new UserDeletionService(List.of(rule1));
        System.out.println(userDeletionService.findUsersReadyForDeletion(userActivities));
    }
}