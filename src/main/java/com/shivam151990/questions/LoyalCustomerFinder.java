package com.shivam151990.questions;

import java.util.*;

/**
 Let’s say we have a website and we keep track of what pages customers are viewing, for things like business metrics.

 Every time somebody comes to the website, we write a record to a log file consisting of
 Timestamp, PageId, CustomerId. At the end of the day we have a big log file with many entries in that format.
 And for every day we have a new file.

 Now, given two log files (log file from day 1 and log file from day 2) we want to generate a list of ‘loyal customers’
 that meet the criteria of: (a) they came on both days, and (b) they visited at least two unique pages.

 */

public class LoyalCustomerFinder {

    public static class LogEntry {
        String timestamp;
        String pageId;
        String customerId;

        LogEntry(String entry) {
            String[] parts = entry.split(",");
            this.timestamp = parts[0].trim();
            this.pageId = parts[1].trim();
            this.customerId = parts[2].trim();
        }
    }

    public static List<String> findLoyalCustomers(List<String> day1Logs, List<String> day2Logs) {
        Map<String, Set<String>> day1Customers = processLogs(day1Logs);
        Map<String, Set<String>> day2Customers = processLogs(day2Logs);

        List<String> loyalCustomers = new ArrayList<>();

        for (Map.Entry<String, Set<String>> entry : day1Customers.entrySet()) {
            String customerId = entry.getKey();
            Set<String> day1Pages = entry.getValue();

            if (day2Customers.containsKey(customerId)) {
                Set<String> day2Pages = day2Customers.get(customerId);
                Set<String> combinedPages = new HashSet<>(day1Pages);
                combinedPages.addAll(day2Pages);

                if (combinedPages.size() >= 2) {
                    loyalCustomers.add(customerId);
                }
            }
        }

        return loyalCustomers;
    }

    private static Map<String, Set<String>> processLogs(List<String> logs) {
        Map<String, Set<String>> customerPages = new HashMap<>();

        for (String log : logs) {
            LogEntry entry = new LogEntry(log);
            customerPages.computeIfAbsent(entry.customerId, k -> new HashSet<>()).add(entry.pageId);
        }

        return customerPages;
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
