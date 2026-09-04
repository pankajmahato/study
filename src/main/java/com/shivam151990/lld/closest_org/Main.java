package com.shivam151990.lld.closest_org;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        OrgHierarchy org = new OrgHierarchy("Atlassian");

        OrgHierarchy.Group engineering = new OrgHierarchy.Group("Engineering");
        OrgHierarchy.Group sales = new OrgHierarchy.Group("Sales");
        OrgHierarchy.Group backend = new OrgHierarchy.Group("Backend");
        OrgHierarchy.Group frontend = new OrgHierarchy.Group("Frontend");

        org.addGroup(org.getRoot(), "Engineering");
        org.getRoot().subGroups.add(engineering);
        engineering.subGroups.add(backend);
        engineering.subGroups.add(frontend);
        org.getRoot().subGroups.add(sales);

        org.addEmployee(backend, "E1");
        org.addEmployee(backend, "E2");
        org.addEmployee(frontend, "E3");
        org.addEmployee(sales, "E4");

        // Find closest common group for [E1, E2]
        OrgHierarchy.Group common = org.getClosestCommonGroup(Arrays.asList("E1", "E2"));
        System.out.println("Closest common group for E1 & E2: " + (common != null ? common.name : "None"));

        // Closest common group for [E1, E3]
        common = org.getClosestCommonGroup(Arrays.asList("E1", "E3"));
        System.out.println("Closest common group for E1 & E3: " + (common != null ? common.name : "None"));

        // Closest common group for [E1, E4]
        common = org.getClosestCommonGroup(Arrays.asList("E1", "E4"));
        System.out.println("Closest common group for E1 & E4: " + (common != null ? common.name : "None"));
    }
}

