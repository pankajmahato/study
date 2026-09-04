package com.shivam151990.questions;

import java.util.*;

public class LoyalCustomerFinderPractice {

    static class LogEntry {

        String ts;
        String pageId;
        String customerId;

        public LogEntry(String entry) {
            String[] entries = entry.split(",");
            this.ts = entries[0].trim();
            this.pageId = entries[1].trim();
            this.customerId = entries[2].trim();
        }

    }

    public static Map<String, Set<String>> processLog(List<String> dayLogs) {
        Map<String, Set<String>> dayLogMap = new HashMap<>();
        for (String dayLog: dayLogs) {
            LogEntry entry = new LogEntry(dayLog);
            dayLogMap.computeIfAbsent(entry.customerId, s -> new HashSet<>()).add(entry.pageId);
        }
        return dayLogMap;
    }


    public static List<String> findLoyalCustomers(List<String> day1Logs, List<String> day2Logs) {
        Map<String, Set<String>> day1 = processLog(day1Logs);
        Map<String, Set<String>> day2 = processLog(day2Logs);

        List<String> loyalCustomers = new ArrayList<>();
        for (Map.Entry<String, Set<String>> day1Data: day1.entrySet()) {
            String day1CustomerId = day1Data.getKey();
            // Customer should have visited both Days
            Set<String> uniquePages = new HashSet<>(day1Data.getValue());
            if (day2.containsKey(day1CustomerId)) {
                uniquePages.addAll(day2.get(day1CustomerId));
            }
            // Unique page visits across 2 days should be >= 2
            if (uniquePages.size() >= 2) {
                loyalCustomers.add(day1CustomerId);
            }
        }
        return loyalCustomers;
    }

    public static void main(String[] args) {
        List<String> day1Logs = Arrays.asList(
                "2023-07-17 10:00:00, page1, customer1",
                "2023-07-17 10:05:00, page2, customer1",
                "2023-07-17 10:10:00, page1, customer2",
                "2023-07-17 10:15:00, page3, customer3"
        );

        List<String> day2Logs = Arrays.asList(
                "2023-07-18 09:00:00, page2, customer1",
                "2023-07-18 09:05:00, page1, customer2",
                "2023-07-18 09:10:00, page3, customer2",
                "2023-07-18 09:15:00, page1, customer4"
        );

        List<String> loyalCustomers = findLoyalCustomers(day1Logs, day2Logs);
        System.out.println("Loyal Customers: " + loyalCustomers);
    }
}
