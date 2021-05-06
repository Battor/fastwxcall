package com.battor.fastwxcall;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class EditContactActivity extends AppCompatActivity {

    private TextView nameTextView;
    private ImageView photoImageView;
    private Button saveButton;

    private ContactDataBaseHelper dbHelper;

    private String contactId;
    private String imagePath;

    private Contact mContact = null;

    private Uri imageUri;

    private final int TAKE_PHOTO = 1;
    private final int CHOOSE_PHOTO = 2;

    private String photoFileName;

    private boolean isCreate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        dbHelper = ContactDataBaseHelper.initAndObtain(this);

        Bundle tmpBundle = getIntent().getExtras();
        if(tmpBundle != null){
            contactId = tmpBundle.getString("contact_id");
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        nameTextView = (TextView) findViewById(R.id.edit_name);
        photoImageView = (ImageView) findViewById(R.id.edit_photo);
        saveButton = (Button) findViewById(R.id.edit_save);

        if(contactId != null && !"".equals(contactId)){
            mContact = dbHelper.getContactById(contactId);
            imagePath = mContact.getPhotoImgPath();
            nameTextView.setText(mContact.getName());
            photoImageView.setImageBitmap(BitmapFactory.decodeFile(mContact.getPhotoImgPath()));
        }else{
            isCreate = true;
            contactId = UUID.randomUUID().toString();
        }

        photoImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                photoFileName = contactId + "_" + new Date().getTime() + ".jpg";
                File outputImage = new File(getExternalCacheDir(), photoFileName);
                try{
                    outputImage.createNewFile();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                if(Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(EditContactActivity.this, "com.battor.fastwxcall.fileprovider", outputImage);
                }else{
                    imageUri = Uri.fromFile(outputImage);
                }

                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });

        photoImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(ContextCompat.checkSelfPermission(EditContactActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            EditContactActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE} ,1);
                }else{
                    openAlbum();
                }
                return true;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmpName = nameTextView.getText().toString();
                if("".equals(tmpName)){
                    Toast.makeText(EditContactActivity.this, "名称不可以为空", Toast.LENGTH_SHORT).show();
                }else if(imagePath == null || "".equals(imagePath)){
                    Toast.makeText(EditContactActivity.this, "请上传图片", Toast.LENGTH_SHORT).show();
                }else{
                    if(isCreate){   // 新增
                        dbHelper.InsertContact(new Contact(contactId.toString(),tmpName,
                                        new Random().nextInt(5) % 5 + 1, imagePath, null, null));
                    }else{
                        mContact.setName(tmpName);
                        mContact.setPhotoImgPath(imagePath);
                        dbHelper.UpdateContact(mContact);
                    }
                    deleteOldPhotos(contactId);
                    Toast.makeText(EditContactActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this, "未获取到权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    imagePath = getExternalCacheDir() + "/" + photoFileName;
                    Bitmap bitmap = Utils.rotateBitmap(BitmapFactory.decodeFile(imagePath),90);
                    File imageFile = new File(imagePath);
                    imageFile.delete();
                    try {
                        imageFile.createNewFile();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(imageFile));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    displayImage(imagePath);
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    if(Build.VERSION.SDK_INT >= 19){
                        displayImage(handleImage(data.getData()));
                    }else{
                        displayImage(handleImageBeforeKitKat(data.getData()));
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String handleImage(Uri uri){
        if(DocumentsContract.isDocumentUri(this, uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                return getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                return getImagePath(contentUri, null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            return getImagePath(uri, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            return uri.getPath();
        }
        return null;
    }

    private String handleImageBeforeKitKat(Uri uri){
        return getImagePath(uri, null);
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        this.imagePath = imagePath;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        photoImageView.setImageBitmap(bitmap);
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void deleteOldPhotos(final String contactId){
        File[] oldPhotos = getExternalCacheDir().listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().startsWith(contactId) && !file.getName().equals(photoFileName);
            }
        });
        for (File oldPhoto:oldPhotos){
            oldPhoto.delete();
        }
    }
}
