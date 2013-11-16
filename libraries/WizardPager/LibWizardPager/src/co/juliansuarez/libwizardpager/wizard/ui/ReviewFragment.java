/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.juliansuarez.libwizardpager.wizard.ui;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import co.juliansuarez.libwizardpager.R;
import co.juliansuarez.libwizardpager.wizard.LocationHelper;
import co.juliansuarez.libwizardpager.wizard.model.AbstractWizardModel;
import co.juliansuarez.libwizardpager.wizard.model.ModelCallbacks;
import co.juliansuarez.libwizardpager.wizard.model.Page;
import co.juliansuarez.libwizardpager.wizard.model.ReviewItem;

public class ReviewFragment extends ListFragment implements ModelCallbacks {
    private Callbacks mCallbacks;
    private AbstractWizardModel mWizardModel;
    private List<ReviewItem> mCurrentReviewItems;

    private ReviewAdapter mReviewAdapter;

    public ReviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReviewAdapter = new ReviewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);

        TextView titleView = (TextView) rootView.findViewById(android.R.id.title);
        titleView.setText(R.string.review);
        titleView.setTextColor(getResources().getColor(R.color.review_green));

        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        setListAdapter(mReviewAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks");
        }

        mCallbacks = (Callbacks) activity;

        mWizardModel = mCallbacks.onGetModel();
        mWizardModel.registerListener(this);
        onPageTreeChanged();
    }

    @Override
    public void onPageTreeChanged() {
        onPageDataChanged(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;

        mWizardModel.unregisterListener(this);
    }

    @Override
    public void onPageDataChanged(Page changedPage) {
        ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
        for (Page page : mWizardModel.getCurrentPageSequence()) {
            page.getReviewItems(reviewItems);
        }
        Collections.sort(reviewItems, new Comparator<ReviewItem>() {
            @Override
            public int compare(ReviewItem a, ReviewItem b) {
                return a.getWeight() > b.getWeight() ? +1 : a.getWeight() < b.getWeight() ? -1 : 0;
            }
        });
        mCurrentReviewItems = reviewItems;

        if (mReviewAdapter != null) {
            mReviewAdapter.notifyDataSetInvalidated();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 1) {
            if(mCurrentReviewItems.size()>=1)
                mCallbacks.onEditScreenAfterReview(mCurrentReviewItems.get(0).getPageKey());
        } else if (position == 0) {
            mReviewAdapter.notifyDataSetChanged();
        }
    }

    public interface Callbacks {
        AbstractWizardModel onGetModel();

        void onEditScreenAfterReview(String pageKey);
    }

    private class ReviewAdapter extends BaseAdapter {
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public Object getItem(int position) {
            return mCurrentReviewItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 1L;
        }

        @Override
        public View getView(int position, View view, ViewGroup container) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View rootView = inflater.inflate(R.layout.list_item_review, container, false);
            switch (position) {
                case 0:
                    ((TextView) rootView.findViewById(android.R.id.text1)).setText("Wo");
                    TextView text = (TextView) rootView.findViewById(android.R.id.text2);
                    text.setText("Position wird ermittelt");
                    new LocationWorker().execute(text);
                    break;
                case 1:
                    ReviewItem reviewItem = mCurrentReviewItems.get(0);
                    String value = reviewItem.getDisplayValue();
                    if (TextUtils.isEmpty(value) || value.equals("Notruf tätigen")) {
                        value = "ist passiert";
                    }
                    ((TextView) rootView.findViewById(android.R.id.text1)).setText("Was");
                    ((TextView) rootView.findViewById(android.R.id.text2)).setText(value);
                    break;
                case 2:
                    ((TextView) rootView.findViewById(android.R.id.text1)).setText("Wieviele");
                    ((TextView) rootView.findViewById(android.R.id.text2)).setText("verletze Personen");
                    break;
                case 3:
                    ((TextView) rootView.findViewById(android.R.id.text1)).setText("Welche");
                    ((TextView) rootView.findViewById(android.R.id.text2)).setText("Verletzungen haben die Personen");
                    break;
                case 4:
                    ((TextView) rootView.findViewById(android.R.id.text1)).setText("Warten");
                    ((TextView) rootView.findViewById(android.R.id.text2)).setText("auf Rückfragen - Nicht auflegen!");
                    break;

            }
            return rootView;
        }

        @Override
        public int getCount() {
            return 5;
        }


    }

    private class LocationWorker extends AsyncTask<TextView, Void, TextView> {

        LocationHelper myLocationHelper = new LocationHelper(getActivity().getApplicationContext());

        @Override
        protected TextView doInBackground(TextView... params) {
            while (!myLocationHelper.gotLocation()) {
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(TextView text) {
            try {
                Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());

                List<Address> addresses;
                if (myLocationHelper.getAccuracy() < 500) {
                    addresses = geo.getFromLocation(myLocationHelper.getLat(), myLocationHelper.getLong(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address returnedAddress = addresses.get(0);

                        StringBuilder strReturnedAddress = new StringBuilder();
                        for (int i = 0; i < returnedAddress.getMaxAddressLineIndex() - 1; i++) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                        }
                        strReturnedAddress.append(returnedAddress.getAddressLine(returnedAddress.getMaxAddressLineIndex() - 1));
                        text.setText(strReturnedAddress.toString());
                    } else {
                        text.setText("Kurze Beschreibung");
                    }
                } else
                    text.setText("Kurze Beschreibung");

            } catch (Exception e) {
                e.printStackTrace();
                text.setText("Fehler");
            }
        }
    }

}
