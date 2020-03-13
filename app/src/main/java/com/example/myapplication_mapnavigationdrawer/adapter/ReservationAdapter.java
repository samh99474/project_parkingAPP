
package com.example.myapplication_mapnavigationdrawer.adapter;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Query;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class ReservationAdapter extends FirestoreAdapter<RestaurantAdapter.ViewHolder>{

    public ReservationAdapter(Query query) {
        super(query);
    }

    private ReservationAdapter mListener;

    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.ViewHolder holder, int position) {

    }
}
