package com.shalil.shop;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;

public class DB extends SQLiteOpenHelper {
    public DB(Context c) { super(c, "shalil.db", null, 1); }
    @Override public void onCreate(SQLiteDatabase d) {
        d.execSQL("CREATE TABLE products(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,barcode TEXT UNIQUE,price REAL NOT NULL,image TEXT)");
        d.execSQL("CREATE TABLE invoices(id INTEGER PRIMARY KEY AUTOINCREMENT,created TEXT NOT NULL,total REAL NOT NULL)");
        d.execSQL("CREATE TABLE invoice_items(id INTEGER PRIMARY KEY AUTOINCREMENT,invoice_id INTEGER,product_id INTEGER,name TEXT,price REAL,qty INTEGER)");
    }
    @Override public void onUpgrade(SQLiteDatabase d,int oldV,int newV) {}
    public Cursor products(){ return getReadableDatabase().rawQuery("SELECT * FROM products ORDER BY name",null); }
    public long saveProduct(long id,String name,String barcode,double price,String image){
        ContentValues v=new ContentValues(); v.put("name",name);v.put("barcode",barcode);v.put("price",price);v.put("image",image);
        if(id==0)return getWritableDatabase().insertOrThrow("products",null,v);
        getWritableDatabase().update("products",v,"id=?",new String[]{""+id});return id;
    }
    public void deleteProduct(long id){getWritableDatabase().delete("products","id=?",new String[]{""+id});}
    public Cursor byBarcode(String code){return getReadableDatabase().rawQuery("SELECT * FROM products WHERE barcode=?",new String[]{code});}
    public long saveInvoice(String date,double total,java.util.ArrayList<CartItem> items){
        SQLiteDatabase d=getWritableDatabase(); d.beginTransaction();
        try { ContentValues v=new ContentValues();v.put("created",date);v.put("total",total);long id=d.insertOrThrow("invoices",null,v);
            for(CartItem x:items){ContentValues q=new ContentValues();q.put("invoice_id",id);q.put("product_id",x.id);q.put("name",x.name);q.put("price",x.price);q.put("qty",x.qty);d.insertOrThrow("invoice_items",null,q);} d.setTransactionSuccessful();return id;
        } finally {d.endTransaction();}
    }
    public Cursor invoices(){return getReadableDatabase().rawQuery("SELECT * FROM invoices ORDER BY id DESC",null);}
    public Cursor invoiceItems(long id){return getReadableDatabase().rawQuery("SELECT * FROM invoice_items WHERE invoice_id=?",new String[]{""+id});}
    public void deleteInvoice(long id){SQLiteDatabase d=getWritableDatabase();d.delete("invoice_items","invoice_id=?",new String[]{""+id});d.delete("invoices","id=?",new String[]{""+id});}
}
