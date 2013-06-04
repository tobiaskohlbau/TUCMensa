package de.kohlbau.tucmensa;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MealAdapter extends BaseAdapter {
    private ArrayList<Meal> mMeals;
    private LayoutInflater mInflater;
    private Context mContext;

    public MealAdapter(Context context) {
        mMeals = new ArrayList<Meal>();
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<Meal> meals) {
        synchronized (mMeals) {
            mMeals = meals;
            notifyDataSetChanged();
            Toast.makeText(mContext, mContext.getString(R.string.refreshed), Toast.LENGTH_SHORT).show();
        }
    }

    static class MealHolder {
        TextView vCategory;
        TextView vDescription;
        TextView vStudent;
        TextView vCoworker;
        TextView vGuest;
        TextView vRating;
        ImageView vImage;
    }

    @Override
    public int getCount() {
        synchronized (mMeals) {
            return mMeals.size();
        }

    }

    @Override
    public String getItem(int position) {
        synchronized (mMeals) {
            return mMeals.get(position).toString();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        View rowView = convertView;
        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.meal, viewGroup, false);
            MealHolder mealHolder = new MealHolder();
            mealHolder.vCategory = (TextView) rowView.findViewById(R.id.category);
            mealHolder.vDescription = (TextView) rowView.findViewById(R.id.description);
            mealHolder.vStudent = (TextView) rowView.findViewById(R.id.student);
            mealHolder.vCoworker = (TextView) rowView.findViewById(R.id.coworker);
            mealHolder.vGuest = (TextView) rowView.findViewById(R.id.guest);
            mealHolder.vRating = (TextView) rowView.findViewById(R.id.rating);
            mealHolder.vImage = (ImageView) rowView.findViewById(R.id.image);
            rowView.setTag(mealHolder);
        }

        String category;
        String description;
        double student;
        double coworker;
        double guest;
        int rating;
        Bitmap image;


        synchronized (mMeals) {
            category = mMeals.get(pos).getCategory();
            description = mMeals.get(pos).getDescription();
            student = mMeals.get(pos).getPrice(0);
            coworker = mMeals.get(pos).getPrice(1);
            guest = mMeals.get(pos).getPrice(2);
            rating = mMeals.get(pos).getRating();
            image = mMeals.get(pos).getImage();

        }

        MealHolder holder = (MealHolder) rowView.getTag();
        holder.vCategory.setText(category);
        holder.vDescription.setText(description);
        holder.vStudent.setText(String.format("%1$,.2f", student) + " \u20ac");
        holder.vCoworker.setText(String.format("%1$,.2f", coworker) + " \u20ac");
        holder.vGuest.setText(String.format("%1$,.2f", guest) + " \u20ac");
        holder.vRating.setText(String.valueOf(rating));
        holder.vImage.setImageBitmap(image);

        return rowView;
    }
}
