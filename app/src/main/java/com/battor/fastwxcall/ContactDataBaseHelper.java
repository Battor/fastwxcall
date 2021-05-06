package com.battor.fastwxcall;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ContactDataBaseHelper extends SQLiteOpenHelper {

    // 建 Contact 表 SQL语句
    private static final String CREATE_CONTACT_TABLE =
            "CREATE TABLE Contact(" +
                "id text PRIMARY KEY," +
                "name text," +
                "headImgId integer," +
                "photoImgPath text," +
                "createTimestamp integer," +
                "updateTimestamp integer" +
            ")";

    // 插入 Contact SQL语句
    private static final String INSERT_SQL =
            "INSERT INTO Contact(id, name, headImgId, photoImgPath, createTimestamp, updateTimestamp) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";
    // 根据 id 查找 Contact SQL语句
    private static final String SELECT_SQL_BY_ID =
            "SELECT id, name, headImgId, photoImgPath, createTimestamp, updateTimestamp FROM Contact " +
                    "WHERE id = ?";
    // 查找所有的 Contact SQL语句
    private static final String SELECT_SQL =
            "SELECT id, name, headImgId, photoImgPath, createTimestamp, updateTimestamp FROM Contact ";
    // 更新 Contact SQL语句
    private static final String UPDATE_SQL =
            "UPDATE Contact SET name = ?, headImgId = ?, photoImgPath = ?, updateTimestamp = ?" +
                    "WHERE id = ?";
    // 删除 Contact SQL语句
    private static final String DELETE_SQL =
            "DELETE FROM Contact WHERE id = ?";

    private static ContactDataBaseHelper dataBaseHelper;

    public ContactDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version){
        super(context, name, cursorFactory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static ContactDataBaseHelper initAndObtain(Context context){
        if(dataBaseHelper == null){
            synchronized (ContactDataBaseHelper.class){
                if(dataBaseHelper == null){
                    dataBaseHelper = new ContactDataBaseHelper(context, "Contact.db", null, 1);
                }
            }
        }
        return dataBaseHelper;
    }

    // 新增
    public void InsertContact(Contact contact){
        if(contact.getId() == null){
            contact.setId(UUID.randomUUID().toString());
        }
        getWritableDatabase().execSQL(INSERT_SQL, new Object[] {
                contact.getId(), contact.getName(), contact.getHeadImgId(), contact.getPhotoImgPath(),
                new Date().getTime(), null
        });
    }

    // 查询单个
    public Contact getContactById(String id){
        Cursor cursor = getReadableDatabase().rawQuery(SELECT_SQL_BY_ID, new String[] { id });
        if(cursor.moveToFirst()){
            String idStr = cursor.getString(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int headImgId = cursor.getInt(cursor.getColumnIndex("headImgId"));
            String photoImgPath = cursor.getString(cursor.getColumnIndex("photoImgPath"));
            long createTimestamp = cursor.getLong(cursor.getColumnIndex("createTimestamp"));
            long updateTimestamp = cursor.getLong(cursor.getColumnIndex("updateTimestamp"));

            cursor.close();

            return new Contact(
                    idStr, name, headImgId, photoImgPath,
                    new Date(createTimestamp), updateTimestamp == 0 ? null : new Date(updateTimestamp)
            );
        }else{
            return null;
        }
    }

    // 查询全部
    public List<Contact> getContactList(){
        List<Contact> resultList = new ArrayList<Contact>();
        Cursor cursor = getReadableDatabase().rawQuery(SELECT_SQL, null);
        if(cursor.moveToFirst()){
            do{
                String idStr = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int headImgId = cursor.getInt(cursor.getColumnIndex("headImgId"));
                String photoImgPath = cursor.getString(cursor.getColumnIndex("photoImgPath"));
                long createTimestamp = cursor.getLong(cursor.getColumnIndex("createTimestamp"));
                long updateTimestamp = cursor.getLong(cursor.getColumnIndex("updateTimestamp"));

                resultList.add(new Contact(
                        idStr, name, headImgId, photoImgPath,
                        new Date(createTimestamp), updateTimestamp == 0 ? null : new Date(updateTimestamp)
                ));
            }while(cursor.moveToNext());
            cursor.close();
        }
        return resultList;
    }

    // 更新
    public void UpdateContact(Contact contact){
        getWritableDatabase().execSQL(UPDATE_SQL, new Object[] {
                contact.getName(), contact.getHeadImgId(), contact.getPhotoImgPath(),
                new Date().getTime(), contact.getId()
        });
    }

    // 删除
    public void DeleteContact(String id){
        getWritableDatabase().execSQL(DELETE_SQL, new Object[] { id } );
    }
}
