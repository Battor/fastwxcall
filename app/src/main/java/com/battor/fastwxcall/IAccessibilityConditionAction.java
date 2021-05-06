package com.battor.fastwxcall;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public interface IAccessibilityConditionAction {
    boolean TestCondition(AccessibilityEvent event, AccessibilityNodeInfo rootNode);
    void DoAction(AccessibilityNodeInfo rootNode);
}
