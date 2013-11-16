package co.juliansuarez.libwizardpager.wizard.model;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

import co.juliansuarez.libwizardpager.wizard.ui.ContentFragment;

public class ContentPage extends Page {


    public ContentPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return ContentFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY),
                getKey()));

    }
}
