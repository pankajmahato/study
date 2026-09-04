package com.shivam151990.lld.closest_org;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class OrgHierarchy {

    // Represents a Group (can have subgroups + employees)
    static class Group {
        String name;
        Set<String> employees;
        List<Group> subGroups;

        Group(String name) {
            this.name = name;
            this.employees = new HashSet<>();
            this.subGroups = new ArrayList<>();
        }
    }

    @Getter
    private Group root;
    private final Map<String, Group> employeeToGroup;
    private final ReadWriteLock rwLock;

    public OrgHierarchy(String rootName) {
        this.root = new Group(rootName);
        this.employeeToGroup = new HashMap<>();
        this.rwLock = new ReentrantReadWriteLock();
    }

    // ----------- Update Methods (Thread-Safe) ------------

    public void addGroup(Group parent, String groupName) {
        rwLock.writeLock().lock();
        try {
            Group newGroup = new Group(groupName);
            parent.subGroups.add(newGroup);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void addEmployee(Group group, String empId) {
        rwLock.writeLock().lock();
        try {
            group.employees.add(empId);
            employeeToGroup.put(empId, group);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void removeEmployee(String empId) {
        rwLock.writeLock().lock();
        try {
            Group g = employeeToGroup.get(empId);
            if (g != null) {
                g.employees.remove(empId);
                employeeToGroup.remove(empId);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void moveEmployee(String empId, Group newGroup) {
        rwLock.writeLock().lock();
        try {
            removeEmployee(empId);
            addEmployee(newGroup, empId);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // ----------- Core Query: Closest Common Group ------------

    public Group getClosestCommonGroup(List<String> employees) {
        rwLock.readLock().lock();
        try {
            if (employees.isEmpty()) return null;
            List<List<Group>> paths = new ArrayList<>();

            for (String emp : employees) {
                Group g = employeeToGroup.get(emp);
                if (g == null) {
                    return null;
                }
                List<Group> path = getPathToRoot(g);
                paths.add(path);
            }

            // Find lowest common group in all paths
            Group lca = null;
            int i = 0;
            while (true) {
                Group candidate = null;
                for (List<Group> path : paths) {
                    if (i >= path.size()) {
                        return lca;
                    }
                    if (candidate == null) {
                        candidate = path.get(path.size() - 1 - i);
                    } else if (candidate != path.get(path.size() - 1 - i)) {
                        return lca;
                    }
                }
                lca = candidate;
                i++;
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    private List<Group> getPathToRoot(Group g) {
        List<Group> path = new ArrayList<>();
        dfsFindPath(root, g, new ArrayList<>(), path);
        return path;
    }

    private boolean dfsFindPath(Group curr, Group target, List<Group> currPath, List<Group> result) {
        currPath.add(curr);
        if (curr == target) {
            result.addAll(currPath);
            return true;
        }
        for (Group sub : curr.subGroups) {
            if (dfsFindPath(sub, target, currPath, result)) {
                return true;
            }
        }
        currPath.removeLast();
        return false;
    }

    // ----------- Flat Structure Simplification ------------

    public Group getClosestCommonGroupFlat(List<String> employees) {
        rwLock.readLock().lock();
        try {
            Set<Group> groups = new HashSet<>();
            for (String emp : employees) {
                Group g = employeeToGroup.get(emp);
                if (g != null) groups.add(g);
            }
            if (groups.size() == 1) return groups.iterator().next();
            return null; // no common group in flat structure
        } finally {
            rwLock.readLock().unlock();
        }
    }
}