package com.battor.fastwxcall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ContactFragment extends Fragment {

    private static final String TAG = "ContactFragment";
    private ContactAdapter mContactAdapter;
    private ContactDataBaseHelper dbHelper;

    private List<Contact> mContactList;

    public ContactFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.contact_recyclerview);
        initContactList();
        this.mContactAdapter = new ContactAdapter(this, mContactList);
        recyclerView.setAdapter(this.mContactAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // 根据滑动距离自动滑入下一个（上一个）item
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();  // 第 1 个可以看到的 item 是第几个（从 0 开始）
                    View firstVisibleChildView = linearLayoutManager.findViewByPosition(firstVisibleItemPosition);  // 获取这个 item
                    if(firstVisibleChildView.getHeight() / 2 >= Math.abs(firstVisibleChildView.getTop())){  // getTop 表示这个 item 距离父 View 顶端的距离（可以为负数）
                        recyclerView.scrollToPosition(firstVisibleItemPosition);
                    }else{
                        int totalCount = recyclerView.getAdapter().getItemCount();
                        recyclerView.smoothScrollToPosition(
                                Math.min(firstVisibleItemPosition + 1, totalCount - 1));
                    }
                }
            }
        });
        return view;
    }

    private void initContactList(){
        dbHelper = ContactDataBaseHelper.initAndObtain(getContext());
        mContactList = dbHelper.getContactList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ContactAdapter.CONTACT_FRAGMENT_EDIT_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    this.mContactAdapter.updateContactList(dbHelper.getContactList());
                }
                break;
        }
    }
}
