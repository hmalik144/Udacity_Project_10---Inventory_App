package com.example.h_mal.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.h_mal.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by h_mal on 04/04/2017.
 */

public class ProductCursorAdapter extends CursorAdapter {

    private final MainActivity activity;

    private Context mContext;

    public ProductCursorAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.activity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mContext = context;

        ImageView picImageView = (ImageView) view.findViewById(R.id.imageView);
        TextView nameTextView = (TextView) view.findViewById(R.id.Product);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.Qn);
        Button sellButton = (Button) view.findViewById(R.id.button);

        final String nameColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
        final Integer priceColumnIndex = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));
        final Integer quantityColumnIndex = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        final String imageColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_IMAGE));

        nameTextView.setText(nameColumnIndex);
        priceTextView.setText(Integer.toString(priceColumnIndex));
        quantityTextView.setText(String.valueOf(quantityColumnIndex));

//        picImageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE))));

        if (imageColumnIndex == null) {
            picImageView.setVisibility(View.GONE);
        }else {
            picImageView.setVisibility(View.VISIBLE);
            picImageView.setImageURI(Uri.parse(imageColumnIndex));
        }

        final long id = cursor.getLong(cursor.getColumnIndexOrThrow(ProductEntry._ID));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnViewItem(id);
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    Object obj = view.getTag();
                    String st = obj.toString();
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameColumnIndex);
                    values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageColumnIndex);
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityColumnIndex >= 1? quantityColumnIndex-1: 0);
                    values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceColumnIndex);

                    Uri currentPetUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, Integer.parseInt(st));

                    int rowsAffected = mContext.getContentResolver().update(currentPetUri, values, null, null);

                }
            }
        });
        Object obj = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        sellButton.setTag(obj);

    }
}
