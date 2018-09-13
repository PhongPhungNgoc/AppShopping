package com.example.phongphung.appshopping.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.phongphung.appshopping.model.Favorite;
import com.example.phongphung.appshopping.model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;


public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "OrderFood.db";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public boolean checkIfFoodExists(String productId, String userPhone) {
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM OrderDetail WHERE UserPhone ='%s' AND ProductId='%s'", userPhone, productId);
        cursor = db.rawQuery(SQLQuery, null);

        if (cursor.getCount() > 0) {
            flag = true;
        } else {
            flag = false;
        }
        cursor.close();
        return flag;
    }

    public List<Order> getCarts(String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone", "ProductName", "ProductId", "Quantity", "Price", "Discount", "Image"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, "UserPhone=?", new String[]{userPhone}, null, null, null);

        final List<Order> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                //Son los nombres en la tabla en la base de datos local.
                result.add(new Order(
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("ProductID")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image")))
                );
            } while (c.moveToNext());
        }

        return result;
    }

    public void addToCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail(UserPhone, ProductId,ProductName, Quantity, Price, Discount, Image) VALUES ('%s','%s','%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        db.execSQL(query);
    }

    public void cleanCart(String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'", userPhone);
        db.execSQL(query);
    }

    public int getCountCart(String userPhone) {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail WHERE UserPhone='%s'", userPhone);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                count = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return count;
    }

    public void removeFromCart(String productId, String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s' and ProductId='%s'", userPhone, productId);
        db.execSQL(query);
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = '%s' WHERE UserPhone = '%s' AND ProductId  ='%s'", order.getQuantity(), order.getUserPhone(), order.getProductId());
        db.execSQL(query);
    }

    public void increaseCart(String userPhone, String productId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = Quantity+1 WHERE UserPhone = '%s' AND ProductId  ='%s'", userPhone, productId);
        db.execSQL(query);
    }

    //Favorites
    public void addToFavorites(Favorite product) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorite(" +
                        "ProductId,ProductName,ProductPrice,ProductCategoryId,ProductImage,ProductDiscount,ProductDescription,UserPhone)" +
                        "VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
                product.getProductID(),
                product.getProductName(),
                product.getProductPrice(),
                product.getProductCategoryID(),
                product.getProductImage(),
                product.getProductDiscount(),
                product.getProductDescription(),
                product.getUserPhone());
        db.execSQL(query);
    }

    public void removeFromFavorites(String productID, String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorite WHERE ProductId='%s' and UserPhone='%s';", productID, userPhone);
        db.execSQL(query);
    }

    public boolean isFavorite(String productID, String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorite WHERE ProductId='%s' and UserPhone='%s';", productID, userPhone);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<Favorite> getAllFavorites(String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone", "ProductId", "ProductName", "ProductPrice", "ProductCategoryId", "ProductImage", "ProductDiscount", "ProductDescription"};
        String sqlTable = "Favorite";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, "UserPhone=?", new String[]{userPhone}, null, null, null);

        final List<Favorite> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new Favorite(
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("ProductPrice")),
                        c.getString(c.getColumnIndex("ProductCategoryId")),
                        c.getString(c.getColumnIndex("ProductImage")),
                        c.getString(c.getColumnIndex("ProductDiscount")),
                        c.getString(c.getColumnIndex("ProductDescription")),
                        c.getString(c.getColumnIndex("UserPhone")))
                );
            } while (c.moveToNext());
        }
        return result;
    }

}
