package com.example.h_mal.inventoryapp;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.h_mal.inventoryapp.data.ProductContract.ProductEntry;

public class AddProduct extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentProductUri;

    private EditText mNameEditText;

    private EditText mQuantityEditText;

    private EditText mPriceEditText;

    private ImageView mImageView;

    private Uri mImageURI;

    private static final int RESULT_LOAD_IMAGE = 1;

    private boolean mProductHasChanged = false;

    private static final int PICK_IMAGE_REQUEST = 0;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mNameEditText = (EditText) findViewById(R.id.productName);
        mQuantityEditText = (EditText) findViewById(R.id.productQuantity);
        mPriceEditText = (EditText) findViewById(R.id.productPrice);
        mImageView = (ImageView) findViewById(R.id.imageView2);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product_title));

            invalidateOptionsMenu();
        } else {

            setTitle(getString(R.string.edit_product_title));


            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        Button Browse = (Button) findViewById(R.id.BrowseImage);
        Browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryToOpenImageSelector();

            }
        });

        Button SubmitProduct = (Button) findViewById(R.id.submit);
        SubmitProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    saveProduct();
            }
        });

        ImageButton ContactSupplier = (ImageButton) findViewById(R.id.ContactSupplier);
        ContactSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, "Order Stock " + mNameEditText.getText().toString().trim());
                intent.putExtra(Intent.EXTRA_TEXT, "I would like to order more " + mNameEditText.getText().toString().trim());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        Button Incrament = (Button) findViewById(R.id.buttonAdd);
        Incrament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IncramentQuantity();
            }
        });

        Button decrament = (Button) findViewById(R.id.buttonMinus);
        decrament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DecramentQuantity();
            }
        });


        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard changes?")
                .setMessage("Are you sure you want to discard changes?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        AddProduct.super.onBackPressed();
                    }
                }).create().show();
    }

    private void saveProduct() {

        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        if (
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(priceString) || mImageURI == null) {
            Toast.makeText(AddProduct.this, "please insert all product data", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityString);
        int price = Integer.parseInt(priceString);

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, String.valueOf(mImageURI));

        if (mCurrentProductUri == null) {

            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        NavUtils.navigateUpFromSameTask(AddProduct.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();

                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_delete:
                DeleteDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void DeleteDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {

        if (mCurrentProductUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "error deleting product", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            mImageView.setImageURI(selectedImage);
            mImageURI = Uri.parse(selectedImage.toString());

        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE };

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int selectedImage = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String image = cursor.getString(selectedImage);

            mNameEditText.setText(name);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Integer.toString(price));
            mImageView.setImageURI(Uri.parse(image));
            mImageURI = Uri.parse(image);


        }
    }

    private void IncramentQuantity() {
        String quantityString = mQuantityEditText.getText().toString();
        int initialQuantity;
        if (quantityString.isEmpty()) {
            initialQuantity = 0;
        } else {
            initialQuantity = Integer.parseInt(quantityString);
        }
        mQuantityEditText.setText(String.valueOf(initialQuantity + 1));
    }

    private void DecramentQuantity() {
        String quantityString = mQuantityEditText.getText().toString();
        int initialQuantity;
        if (quantityString.isEmpty()) {
            return;
        } else if (quantityString.equals("0")) {
            return;
        } else {
            initialQuantity = Integer.parseInt(quantityString);
            mQuantityEditText.setText(String.valueOf(initialQuantity - 1));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
