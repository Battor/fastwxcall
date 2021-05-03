package com.battor.fastwxcall;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ContactFragment extends Fragment {

    private static final String TAG = "ContactFragment";

    private List<Contact> mContactList;

    public ContactFragment(){

    }

    public void setConatctData(List<Contact> contactList){
        this.mContactList = contactList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.contact_recyclerview);
        recyclerView.setAdapter(new ContactAdapter(this.mContactList));
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
}
