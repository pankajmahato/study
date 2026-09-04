package com.shivam151990.lld.gdpr.service;

import com.shivam151990.lld.gdpr.model.UserActivity;

import java.util.List;

public interface Rule {
    public boolean canDelete(List<UserActivity> userActivity);
}
