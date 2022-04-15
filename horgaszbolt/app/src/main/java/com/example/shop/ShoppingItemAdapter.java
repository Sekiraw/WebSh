package com.example.shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.EventListener;

public class ShoppingItemAdapter
        extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder>
        implements Filterable {
    // Member variables.
    private ArrayList<ShoppingItem> mShoppingData;
    private ArrayList<ShoppingItem> mSoppingDataAll;
    private Context mContext;
    private int lastPosition = -1;
    private String uId = getUserId();
    private String sellerId = "0NMI0d7Qyrf7TLFIsLoOeyEsSrg2";

    public ShoppingItemAdapter() {
    }

    public ArrayList<ShoppingItem> getmShoppingData() {
        return mShoppingData;
    }
    public ArrayList<ShoppingItem> getmmSoppingDataAll() {
        return mSoppingDataAll;
    }

    ShoppingItemAdapter(Context context, ArrayList<ShoppingItem> itemsData) {
        this.mShoppingData = itemsData;
        this.mSoppingDataAll = itemsData;
        this.mContext = context;
    }

    public String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String name = user.getDisplayName();
        return uid;
    }

    @Override
    public ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        // Seller's uid
        if (uId.equals(sellerId)){
            return new ViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item_seller, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get current sport.
        ShoppingItem currentItem = mShoppingData.get(position);

        // Populate the textviews with data.
        holder.bindTo(currentItem);

        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mShoppingData.size();
    }


    /**
     * RecycleView filter
     * **/
    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ShoppingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = mSoppingDataAll.size();
                results.values = mSoppingDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(ShoppingItem item : mSoppingDataAll) {
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mShoppingData = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        // Member Variables for the TextViews
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;
        private RatingBar mRatingBar;

        ViewHolder(View itemView) {
            super(itemView);

            // Initialize the views.
            mTitleText = itemView.findViewById(R.id.itemTitle);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mItemImage = itemView.findViewById(R.id.itemImage);
            mRatingBar = itemView.findViewById(R.id.ratingBar);
            mPriceText = itemView.findViewById(R.id.price);
        }

        void bindTo(ShoppingItem currentItem){
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getPrice());
            mRatingBar.setRating(currentItem.getRatedInfo());

            // Load the images into the ImageView using the Glide library.
            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
            itemView.findViewById(R.id.add_to_cart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ShopListActivity)mContext).updateAlertIcon(currentItem);
                }
            });
            if(uId.equals(sellerId)) {
                itemView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ShopListActivity) mContext).deleteItem(currentItem);
                    }
                });
            }
        }
    }
}
