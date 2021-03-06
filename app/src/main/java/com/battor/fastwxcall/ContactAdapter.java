package com.battor.fastwxcall;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements View.OnClickListener{

    private static final String TAG = "ContactAdapter";

    private Context mContext;
    private List<Contact> mContactList;
    private ContactFragment mContactFragment;
    private ContactDataBaseHelper dbHelper;

    private final int TYPE_CONTENT = 1;
    private final int TYPE_BOTTOM = 2;

    public static final int CONTACT_FRAGMENT_EDIT_REQUEST_CODE = 0;

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;

        CircleImageView mHeadImageView;
        ImageView mImageView;
        ImageView mEditImageView;

        ImageView mAddImageView;

        ViewHolder(View view, Context context) {
            super(view);
            mContext = context;
            mHeadImageView = (CircleImageView) view.findViewById(R.id.contact_item_headimg);
            mImageView = (ImageView) view.findViewById(R.id.contact_item_photo);
            mEditImageView = (ImageView) view.findViewById(R.id.contact_item_editimg);

            mAddImageView = (ImageView) view.findViewById(R.id.contact_item_add);
        }
    }

    ContactAdapter(ContactFragment contactFragment, List<Contact> contactList){
        this.mContactFragment = contactFragment;
        this.mContactList = contactList;
        this.dbHelper = ContactDataBaseHelper.initAndObtain(mContactFragment.getContext());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }

        switch (viewType){
            case TYPE_CONTENT:
                View view0 = LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false);
                return new ViewHolder(view0, mContext);
            case TYPE_BOTTOM:
                View view1 = LayoutInflater.from(mContext).inflate(R.layout.item_contact_add, parent, false);
                return new ViewHolder(view1, mContext);
        }

        return new ViewHolder(null, mContext);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position <= mContactList.size() - 1){
            final Contact nowContact = mContactList.get(position);
            holder.mImageView.setImageBitmap(BitmapFactory.decodeFile(nowContact.getPhotoImgPath()));
            holder.mHeadImageView.setImageResource(
                    mContext.getResources().getIdentifier(
                            "head_img_" + nowContact.getHeadImgId(), "mipmap", mContext.getPackageName()));

            holder.mHeadImageView.setOnClickListener(this);
            holder.mImageView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    AccessibilityServiceFlagUtil.setTargetContactName(mContext, nowContact.getName());
                    AccessibilityServiceFlagUtil.setAccessibilityIsRunning(mContext, true);
                    if(!checkWXInstalled(mContext)){
                        Toast.makeText(mContext, "WX is not installed", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    Toast.makeText(mContext, "photo long clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            holder.mEditImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EditContactActivity.class);    // ???????????? startActivityForResult ???????????????????????????????????????????????????
                    intent.putExtra("contact_id", nowContact.getId());
                    mContactFragment.startActivityForResult(intent, CONTACT_FRAGMENT_EDIT_REQUEST_CODE);
                }
            });
            holder.mEditImageView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("??????")
                            .setMessage("????????????????????????")
                            .setPositiveButton("???", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dbHelper.DeleteContact(mContactList.get(position).getId());
                                    mContactList = dbHelper.getContactList();
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("???", null)
                            .show();
                    return true;
                }
            });
        }else{
            holder.mAddImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EditContactActivity.class);
                    mContactFragment.startActivityForResult(intent, CONTACT_FRAGMENT_EDIT_REQUEST_CODE);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == mContactList.size()){
            return TYPE_BOTTOM;
        }else{
            return TYPE_CONTENT;
        }
    }

    @Override
    public int getItemCount() {
        return mContactList.size() + 1;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.contact_item_headimg:
                //Toast.makeText(mContext, "headimg clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.contact_item_photo:
                //Toast.makeText(mContext, "photo clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void updateContactList(List<Contact> contactList){
        this.mContactList = contactList;
        notifyDataSetChanged();
    }

    private boolean checkWXInstalled(Context context) {
        String wxPackageName = "com.tencent.mm";
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(wxPackageName, 0);
        } catch (Exception e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if(packageInfo == null) {
            return false;
        } else {
            PackageManager packageManager = mContext.getPackageManager();
            Intent it = packageManager.getLaunchIntentForPackage(wxPackageName);
            mContext.startActivity(it);
            return true;
        }
    }
}
