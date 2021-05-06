package com.battor.fastwxcall;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityServiceFastCall extends AccessibilityService {

    private static final String TAG = "ServiceFastCall";

    private List<IAccessibilityConditionAction> conditionActions = new ArrayList<IAccessibilityConditionAction>();

    private static int flagIndex = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        String packageName = accessibilityEvent.getPackageName().toString();
        int eventType = accessibilityEvent.getEventType();
        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        //Log.d(TAG,  "flagIndex:" + flagIndex + " PackageName: " + packageName + " AccessibilityEventType: " + eventType + " AccessibilityEventSource: " + source);


        boolean isAccessibilityServiceRunning = AccessibilityServiceFlagUtil
                                                    .getAccessibilityIsRunning(AccessibilityServiceFastCall.this);
        if(isAccessibilityServiceRunning){
            judegeConditionAndDoAction(accessibilityEvent, getRootInActiveWindow());
        }
    }

    private void initFastCallConditionActions(){
        // 0.刚进入微信页面时判断，并点击 搜索 按钮
        conditionActions.add(new IAccessibilityConditionAction() {
            @Override
            public boolean TestCondition(AccessibilityEvent event, AccessibilityNodeInfo rootNode) {
                if(event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                    return false;
                }
                if(rootNode == null){
                    return false;
                }

                if(rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/fdi").size() == 0){
                    return false;
                }

                return true;
            }

            @Override
            public void DoAction(AccessibilityNodeInfo rootNode) {
                rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/fdi").get(0)
                        .performAction(AccessibilityNodeInfo.ACTION_CLICK);

                try{
                    Thread.sleep(1000);
                }catch (Exception ex){

                }
            }
        });

        // 1.输入搜索内容
        conditionActions.add(new IAccessibilityConditionAction() {
            @Override
            public boolean TestCondition(AccessibilityEvent event, AccessibilityNodeInfo rootNode) {
                if(event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
                    return false;
                }
                if(rootNode == null){
                    return false;
                }

                if(rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bxz").size() == 0){
                    return false;
                }

                return true;
            }

            @Override
            public void DoAction(AccessibilityNodeInfo rootNode) {
                String targetContactName = AccessibilityServiceFlagUtil.getTargetContactName(AccessibilityServiceFastCall.this);
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, targetContactName);
                rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bxz").get(0)
                        .performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            }
        });

        // 2.点击搜索结果
        conditionActions.add(new IAccessibilityConditionAction() {
            @Override
            public boolean TestCondition(AccessibilityEvent event, AccessibilityNodeInfo rootNode) {
                if(event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
                    return false;
                }
                if(rootNode == null){
                    return false;
                }

                if(rootNode.findAccessibilityNodeInfosByText("联系人").size() == 0
                        && rootNode.findAccessibilityNodeInfosByText("最常使用").size() == 0){
                    return false;
                }
                // 如果有 查找微信号，表示人员不存在，返回 false
                if(rootNode.findAccessibilityNodeInfosByText("查找微信号").size() != 0){
                    return false;
                }

                return true;
            }

            @Override
            public void DoAction(AccessibilityNodeInfo rootNode) {
                AccessibilityNodeInfo ani = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/hf1").get(0)
                        .getChild(2);
                ani.performAction(AccessibilityNodeInfo.ACTION_SELECT);
                ani.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        });

        // 3.点击聊天页面 更多（加号） 按钮
        conditionActions.add(new IAccessibilityConditionAction() {
            @Override
            public boolean TestCondition(AccessibilityEvent event, AccessibilityNodeInfo rootNode) {
                if(event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                    return false;
                }
                if(rootNode == null){
                    return false;
                }

                if(rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/au0").size() == 0){
                    return false;
                }

                return true;
            }

            @Override
            public void DoAction(AccessibilityNodeInfo rootNode) {
                rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/au0").get(0)
                        .performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        });

        // 4.点击 视频通话 按钮
        conditionActions.add(new IAccessibilityConditionAction() {
            @Override
            public boolean TestCondition(AccessibilityEvent event, AccessibilityNodeInfo rootNode) {
                if(event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
                    return false;
                }
                if(rootNode == null){
                    return false;
                }

                if(rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/se").size() == 0){
                    return false;
                }

                return true;
            }

            @Override
            public void DoAction(AccessibilityNodeInfo rootNode) {
                rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/se").get(0)
                        .getChild(2).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        });

        // 5.点击 语音通话 按钮
        conditionActions.add(new IAccessibilityConditionAction() {
            @Override
            public boolean TestCondition(AccessibilityEvent event, AccessibilityNodeInfo rootNode) {
                if(event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                    return false;
                }
                if(rootNode == null){
                    return false;
                }

                if(rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ab7").size() == 0){
                    return false;
                }

                return true;
            }

            @Override
            public void DoAction(AccessibilityNodeInfo rootNode) {
                rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ab7").get(0)
                        .getChild(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        });

        // 6.聊天页面，点击返回
        conditionActions.add(new IAccessibilityConditionAction() {
            @Override
            public boolean TestCondition(AccessibilityEvent event, AccessibilityNodeInfo rootNode) {
                if(event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                    return false;
                }
                if(rootNode == null){
                    return false;
                }

                if(rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/uo").size() == 0){
                    return false;
                }

                return true;
            }

            @Override
            public void DoAction(AccessibilityNodeInfo rootNode) {
                rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eh").get(0)
                        .performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        });

        // 7.搜索页面，点击“取消”
        conditionActions.add(new IAccessibilityConditionAction() {
            @Override
            public boolean TestCondition(AccessibilityEvent event, AccessibilityNodeInfo rootNode) {
                if(event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                    return false;
                }
                if(rootNode == null){
                    return false;
                }

                if(rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aib").size() == 0){
                    return false;
                }

                return true;
            }

            @Override
            public void DoAction(AccessibilityNodeInfo rootNode) {
                rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aib").get(0)
                        .performAction(AccessibilityNodeInfo.ACTION_CLICK);

                AccessibilityServiceFlagUtil.setAccessibilityIsRunning(AccessibilityServiceFastCall.this,false);

                flagIndex = 0;

                try{
                    Thread.sleep(1000);
                }catch (Exception ex){

                }

                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        });
    }

    private void judegeConditionAndDoAction(AccessibilityEvent event, AccessibilityNodeInfo rootNodeInfo){
        IAccessibilityConditionAction aca = conditionActions.get(flagIndex);
        if(aca.TestCondition(event,rootNodeInfo)){
            flagIndex++;
            aca.DoAction(rootNodeInfo);
        }
    }

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "service connected");
        initFastCallConditionActions();
        super.onServiceConnected();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "service unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "service interrupted");
    }

    public static void resetFlagIndex(){
        flagIndex = 0;
    }
}