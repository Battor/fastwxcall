package com.battor.fastwxcall;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context mContext;
    private List<Contact> mContactList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView mHeadImageView;
        ImageView mImageView;

        public  ViewHolder(View view){
            super(view);
            mHeadImageView = (CircleImageView)view.findViewById(R.id.contact_item_headimg);
            mImageView = (ImageView)view.findViewById(R.id.contact_item_photo);
        }
    }

    public ContactAdapter(List<Contact> contactList){
        this.mContactList = contactList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = mContactList.get(position);
        holder.mHeadImageView.setImageResource(contact.getHeadImgId());
        holder.mImageView.setImageResource(contact.getPhotoId());
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }
}
