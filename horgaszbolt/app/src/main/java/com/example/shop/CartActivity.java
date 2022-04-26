package com.example.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String LOG_TAG = CartActivity.class.getName();

    private FrameLayout redCircle;
    private TextView countTextView;

    private NotificationHandler mNotificationHandler;

    private Button button;

    Spinner spinner;
    RadioGroup accountTypeGroup;
    private RecyclerView mRecyclerView;
    private int gridNumber = 1;
    private int item_size = 0;

    private SharedPreferences preferences;

    private FirebaseFirestore mFirestore;
    private String uId;

    private ArrayList<ShoppingItem> mItemsData;
    private CollectionReference mItems;
    private CartItemAdapter mAdapter;

    private boolean viewRow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "cart onCreate");
        setContentView(R.layout.activity_cart);

        uId = getUserId();

        Bundle bundle = getIntent().getExtras();
        item_size = bundle.getInt("data_size");
        mItemsData = new ArrayList<>();

        // recycle view
        mRecyclerView = findViewById(R.id.recyclerView);
        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, gridNumber));

        mFirestore = FirebaseFirestore.getInstance();
        //mItems = mFirestore.collection("Items");
        mItems = mFirestore.collection(uId + "Items");
        Log.i(LOG_TAG, String.valueOf(mItemsData) + " data");
        mAdapter = new CartItemAdapter(this, mItemsData);
        mRecyclerView.setAdapter(mAdapter);
        mNotificationHandler = new NotificationHandler(this);

        Log.d(LOG_TAG, getUserEmail() + " email");

        // Gomb animáció
        button = (Button)findViewById(R.id.checkout);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(CartActivity.this, R.anim.bounce);
                button.startAnimation(animation);
            }
        });

        queryData();
    }

    public String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String name = user.getDisplayName();
        return uid;
    }

    public String getUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getEmail();
        return name;
    }

    private void initializeData() {
        // Get the resources from the XML file.
        String[] itemsList = getResources()
                .getStringArray(R.array.shopping_item_names);
        String[] itemsInfo = getResources()
                .getStringArray(R.array.shopping_item_desc);
        String[] itemsPrice = getResources()
                .getStringArray(R.array.shopping_item_price);
        TypedArray itemsImageResources =
                getResources().obtainTypedArray(R.array.shopping_item_images);
        TypedArray itemRate = getResources().obtainTypedArray(R.array.shopping_item_rates);

        // Create the ArrayList of Sports objects with the titles and
        // information about each sport.
        for (int i = 0; i < itemsList.length; i++) {
            mItems.add(new ShoppingItem(
                            itemsList[i],
                            itemsInfo[i],
                            itemsPrice[i],
                            itemRate.getFloat(i, 0),
                            itemsImageResources.getResourceId(i, 0),
                            0
                    )
            );
        }

        // Recycle the typed array.
        itemsImageResources.recycle();
    }

    private void queryData() {
        mItemsData.clear();

        //mItems.whereEqualTo()
        //mItems.whereGreaterThan("cartedCount", 0).orderBy("cartedCount").limit(10).get()
        mItems.whereGreaterThan("cartedCount", 0).orderBy("cartedCount").limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                ShoppingItem item = document.toObject(ShoppingItem.class);
                item.setId(document.getId());
                mItemsData.add(item);
            }
            if (item_size == 0){
                initializeData();
                queryData();
            }

            // letoltom az adatokat
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.cart_activity_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_button:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                Intent intent_logout = new Intent(this, MainActivity.class);
                startActivity(intent_logout);
                finish();
                return true;
            case R.id.settings_button:
                Log.d(LOG_TAG, "Setting clicked!");
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("SECRET_KEY", 99);
                startActivity(intent);
                finish();
                return true;
            case R.id.go_back_button:
                Log.d(LOG_TAG, "Go back clicked!");
                Intent intent_shop = new Intent(this, ShopListActivity.class);
                startActivity(intent_shop);
                finish();
                return true;
            case R.id.view_selector:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        final MenuItem alertMenuItem = menu.findItem(R.id.sum);
//        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
//        countTextView = (TextView) rootView.findViewById(R.id.sum);

        return super.onPrepareOptionsMenu(menu);
    }

    public void deleteItem(ShoppingItem item) {
        mItems.document(item._getId()).update("cartedCount", 0)
                .addOnFailureListener(failure -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be deleted from cart.", Toast.LENGTH_LONG).show();
                });

        mNotificationHandler.send(item.getName());
        queryData();
    }

    public void cancel(View view) {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "settings onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "settings onStop");
        Intent intent = new Intent(this, ShopListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "settings onDestroy");
        Intent intent = new Intent(this, ShopListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "settings onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "settings onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "settings onRestart");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        Log.i(LOG_TAG, selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
