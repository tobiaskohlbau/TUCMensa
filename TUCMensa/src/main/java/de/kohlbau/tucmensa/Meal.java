package de.kohlbau.tucmensa;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Meal implements Parcelable {
    private int id;
    private String category;
    private int rating;
    private boolean imageAvailable;
    private boolean[] ingredients;
    private String description;
    private double[] price;
    private Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isImageAvailable() {
        return imageAvailable;
    }

    public void setImageAvailable(boolean imageAvailable) {
        this.imageAvailable = imageAvailable;
    }

    public boolean[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(boolean[] ingredients) {
        this.ingredients = ingredients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double[] getPrice() {
        return price;
    }

    public double getPrice(int pos) {
        return price[pos];
    }

    public void setPrice(double[] price) {
        this.price = price;
    }


    public static final Creator<Meal> CREATOR = new Creator<Meal>() {
        public Meal createFromParcel(Parcel in) {
            return new Meal(in);
        }

        public Meal[] newArray(int size) {
            return new Meal[size];
        }
    };

    public Meal() {

    }

    public Meal(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(category);
        out.writeInt(rating);
        out.writeByte((byte) (imageAvailable ? 1 : 0));
        out.writeBooleanArray(ingredients);
        out.writeString(description);
        out.writeDoubleArray(price);
        out.writeParcelable(image, 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        category = in.readString();
        rating = in.readInt();
        imageAvailable = in.readByte() == 1;
        in.readBooleanArray(ingredients);
        description = in.readString();
        in.readDoubleArray(price);
        in.readParcelable(getClass().getClassLoader());
    }
}
