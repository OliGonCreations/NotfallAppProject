package co.juliansuarez.libwizardpager.wizard.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import co.juliansuarez.libwizardpager.R;
import co.juliansuarez.libwizardpager.wizard.model.Page;

public class ContentFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, SurfaceHolder.Callback {
    public static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private Page mPage;
    private String mKey;

    private final static int[] drawablesGHSHQ = {R.drawable.ic_ghs01_hq, R.drawable.ic_ghs02_hq, R.drawable.ic_ghs03_hq, R.drawable.ic_ghs04_hq, R.drawable.ic_ghs05_hq, R.drawable.ic_ghs06_hq, R.drawable.ic_ghs07_hq, R.drawable.ic_ghs08_hq, R.drawable.ic_ghs09_hq};
    private final static int[] drawablesCLASSHQ = {R.drawable.ic_class1_hq, R.drawable.ic_class21_hq, R.drawable.ic_class22_hq, R.drawable.ic_class23_hq, R.drawable.ic_class3_hq, R.drawable.ic_class41_hq, R.drawable.ic_class42_hq, R.drawable.ic_class43_hq, R.drawable.ic_class51_hq, R.drawable.ic_class52_hq, R.drawable.ic_class61_hq, R.drawable.ic_class1_hq, R.drawable.ic_class7_hq, R.drawable.ic_class8_hq, R.drawable.ic_class9_hq};
    private final static int[] drawablesSYMBOLSHQ = {R.drawable.ic_symbols1_hq, R.drawable.ic_symbols2_hq, R.drawable.ic_symbols2_hq, R.drawable.ic_symbols3_hq, R.drawable.ic_symbols4_hq, R.drawable.ic_symbols5_hq, R.drawable.ic_symbols6_hq, R.drawable.ic_symbols7_hq};

    private ListView list;
    private Spinner spinner, spNumbers;
    private TextView tvDescription1, tvDescription2;

    private int page;

    private Camera mCamera;
    private SurfaceView mCameraPreviewSurface;
    private boolean mActive;
    private SurfaceHolder mCameraSurfaceHolder;

    public static ContentFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ContentFragment f = new ContentFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);
        mActive = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mPage.getTitle().equals("Warnhinweise")) {
            page = 0;
            View view = inflater.inflate(R.layout.fragment_page_ghs, container, false);
            ((TextView) view.findViewById(android.R.id.title)).setText(mPage.getTitle());
            final TextView ghs_definition = (TextView) view.findViewById(R.id.ghs_definition);
            tvDescription1 = (TextView) view.findViewById(R.id.ghs_text1);
            tvDescription2 = (TextView) view.findViewById(R.id.ghs_text2);
            final Spinner ghs_statement = (Spinner) view.findViewById(R.id.ghs_statement);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.statements, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ghs_statement.setAdapter(adapter);
            ghs_statement.setOnItemSelectedListener(this);
            spNumbers = (Spinner) view.findViewById(R.id.ghs_numbers);
            ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(),
                    R.array.s_values, android.R.layout.simple_spinner_item);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spNumbers.setAdapter(adapter1);
            spNumbers.setSelection(0);
            spNumbers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (ghs_statement.getSelectedItemPosition()) {
                        case 0:
                            ghs_definition.setText(getResources().getStringArray(R.array.h)[i]);
                            break;
                        case 1:
                            ghs_definition.setText(getResources().getStringArray(R.array.p)[i]);
                            break;
                        case 2:
                            ghs_definition.setText(getResources().getStringArray(R.array.euh)[i]);
                            break;
                        case 3:
                            ghs_definition.setText(getResources().getStringArray(R.array.s)[i]);
                            break;
                        case 4:
                            ghs_definition.setText(getResources().getStringArray(R.array.r)[i]);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            return view;
        } else if (mPage.getTitle().equals("Gefahrensymbole")) {
            page = 1;
            View view = inflater.inflate(R.layout.fragment_page_list, container, false);
            ((TextView) view.findViewById(android.R.id.title)).setText(mPage.getTitle());
            spinner = (Spinner) view.findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.pictogram, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
            list = (ListView) view.findViewById(R.id.list);
            list.setSelection(0);
            list.setOnItemClickListener(this);
            return view;
        }
        View rootView = inflater.inflate(R.layout.fragment_page_content, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());
        ImageView image = (ImageView) rootView.findViewById(R.id.image);
        TextView text = (TextView) rootView.findViewById(R.id.text);
        Button action = (Button) rootView.findViewById(R.id.action);
        if (mPage.getTitle().equals("Entdecken")) {
            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_behavior_fire_call));
            text.setText(getString(R.string.fire_discover));
            action.setVisibility(View.VISIBLE);
            action.setText(getString(R.string.action_call_firefighters));
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Rufe Feuerwehr an.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:112"));
                    startActivity(intent);
                }
            });
        } else if (mPage.getTitle().equals("Retten vor löschen!")) {
            text.setText(getString(R.string.fire_hero));
            image.setVisibility(View.GONE);
        } else if (mPage.getTitle().equals("Flucht unmöglich?")) {
            text.setText(getString(R.string.fire_noescape));
            image.setVisibility(View.GONE);
        } else if (mPage.getTitle().equals("Fenster schließen")) {
            text.setText(getString(R.string.fire_windows));
            image.setVisibility(View.GONE);
        } else if (mPage.getTitle().equals("Vermisste?")) {
            text.setText(getString(R.string.fire_missing));
            image.setVisibility(View.GONE);
        } else if (mPage.getTitle().equals("Erster Blick")) {
            text.setText(getString(R.string.aide_firstview));
            image.setVisibility(View.GONE);
        } else if (mPage.getTitle().equals("Sicherheit")) {
            text.setText(getString(R.string.aide_safety));
            image.setVisibility(View.GONE);
        } else if (mPage.getTitle().equals("Erste Hilfe")) {
            text.setText(getString(R.string.aide_lsm));
            image.setVisibility(View.GONE);
        } else if (mPage.getTitle().equals("Fluchtweg")) {
            text.setText(getString(R.string.fire_exit));
            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_behavior_fire_exit));
            action.setVisibility(View.VISIBLE);
            action.setText(getString(R.string.fire_light));
            mCameraPreviewSurface = (SurfaceView) rootView.findViewById(R.id.surfaceView);
            mCameraPreviewSurface.getHolder().addCallback(this);
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleLEDFlash();
                    if(mActive)
                        ((Button)v).setText(getString(R.string.fire_light_off));
                    else
                        ((Button)v).setText(getString(R.string.fire_light));
                }
            });
        } else if (mPage.getTitle().equals("Notruf")) {
            text.setText(getString(R.string.amok_emergency));
            image.setVisibility(View.GONE);
            action.setVisibility(View.VISIBLE);
            action.setText(getString(R.string.action_call_police));
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Bestätigen.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:110"));
                    startActivity(intent);
                }
            });
        } else if (mPage.getTitle().equals("Verhalten")) {
            text.setText(getString(R.string.amok_behavior));
            image.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof PageFragmentCallbacks))
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setLEDFlashOff();
        //releaseResources();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, final int i, long l) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_ghs, null);
        ImageView image = (ImageView) view.findViewById(R.id.dialog_image);
        switch (spinner.getSelectedItemPosition()) {
            case 0:
                ((TextView) view.findViewById(R.id.dialog_title)).setText(getResources().getStringArray(R.array.ghs_titles)[i]);
                ((TextView) view.findViewById(R.id.dialog_info)).setText(getResources().getStringArray(R.array.ghs_effect)[i]);
                ((TextView) view.findViewById(R.id.dialog_instructions)).setText(getResources().getStringArray(R.array.ghs_instructions)[i]);
                image.setImageDrawable(getResources().getDrawable(drawablesGHSHQ[i]));
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_transparent_image, null);
                        ImageView image = (ImageView) v.findViewById(R.id.image);
                        image.setImageDrawable(getResources().getDrawable(drawablesGHSHQ[i]));
                        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).create();
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                new AlertDialog.Builder(getActivity()).setView(view).setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
                break;
            case 2:
                ((TextView) view.findViewById(R.id.dialog_title)).setText(getResources().getStringArray(R.array.symbols_titles)[i]);
                ((TextView) view.findViewById(R.id.dialog_info)).setText(getResources().getStringArray(R.array.symbols_effect)[i]);
                ((TextView) view.findViewById(R.id.dialog_instructions)).setText(getResources().getStringArray(R.array.symbols_instructions)[i]);
                image.setImageDrawable(getResources().getDrawable(drawablesSYMBOLSHQ[i]));
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_transparent_image, null);
                        ImageView image = (ImageView) v.findViewById(R.id.image);
                        image.setImageDrawable(getResources().getDrawable(drawablesSYMBOLSHQ[i]));
                        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(v).create();
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                new AlertDialog.Builder(getActivity()).setView(view).setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
                break;
            default:

                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (page == 1) {
            switch (i) {
                case 0:
                    list.setAdapter(new CustomListImageAdapter(getActivity(), drawablesGHSHQ, getResources().getStringArray(R.array.ghs_titles)));
                    list.setClickable(true);
                    break;
                case 1:
                    list.setAdapter(new CustomListImageAdapter(getActivity(), drawablesCLASSHQ, getResources().getStringArray(R.array.class_titles)));
                    list.setClickable(false);
                    break;
                case 2:
                    list.setAdapter(new CustomListImageAdapter(getActivity(), drawablesSYMBOLSHQ, getResources().getStringArray(R.array.symbols_titles)));
                    list.setClickable(true);
                    break;
                default:
                    list.setAdapter(new CustomListImageAdapter(getActivity(), drawablesCLASSHQ, getResources().getStringArray(R.array.class_titles)));
                    list.setClickable(false);
                    break;
            }
        } else if (page == 0) {
            ArrayAdapter<CharSequence> adapter = null;
            switch (i) {
                case 0:
                    tvDescription1.setText(getString(R.string.ghs_h));
                    tvDescription2.setText(getString(R.string.ghs_h_statements));
                    adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.h_values, android.R.layout.simple_spinner_item);
                    break;
                case 1:
                    tvDescription1.setText(getString(R.string.ghs_p));
                    tvDescription2.setText(getString(R.string.ghs_p_statements));
                    adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.p_values, android.R.layout.simple_spinner_item);
                    break;
                case 2:
                    adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.euh_values, android.R.layout.simple_spinner_item);
                    tvDescription1.setText(getString(R.string.ghs_euh));
                    tvDescription2.setText("");
                    break;
                case 3:
                    adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.s_values, android.R.layout.simple_spinner_item);
                    tvDescription1.setText(getString(R.string.ghs_s));
                    tvDescription2.setText("");
                    break;
                case 4:
                    adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.r_values, android.R.layout.simple_spinner_item);
                    tvDescription1.setText(getString(R.string.ghs_r));
                    tvDescription2.setText("");
                    break;

                default:
                    break;
            }
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spNumbers.setAdapter(adapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCameraSurfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void toggleLEDFlash() {
        if (mActive) {
            setLEDFlashOff();
        } else {
            setLEDFlashOn();
        }
    }

    public void setLEDFlashOn() {
        if (!mActive) {
            mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
            try {
                mCamera.setPreviewDisplay(mCameraSurfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            mCamera.startPreview();
            mActive = true;
        }
    }

    public void setLEDFlashOff() {
        if (mActive) {
            mCamera.release();
            mCamera = null;
            mActive = false;
        }
    }

    public void releaseResources() {
        if (mCamera != null) {
            mActive = false;
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mCameraPreviewSurface.getHolder().removeCallback(this);
    }

    private class CustomListImageAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private int[] drawables;
        private String[] titles;

        public CustomListImageAdapter(Context context, int[] drawables, String[] titles) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.drawables = drawables;
            this.titles = titles;
        }

        @Override
        public int getCount() {
            return drawables.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View v = convertView;
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                v = inflater.inflate(R.layout.list_item_image, null);
                TextView tv = (TextView) v.findViewById(R.id.list_title);
                ImageView img = (ImageView) v.findViewById(R.id.list_image);
                holder.text = tv;
                holder.image = img;
                v.setTag(holder);
            } else
                holder = (ViewHolder) v.getTag();
            holder.text.setText(titles[i]);
            //holder.image.setImageResource(drawables[i]);
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
            holder.image.setImageBitmap(decodeSampledBitmapFromResource(getResources(), drawables[i], size, size));
            return v;
        }

        private class ViewHolder {
            public ImageView image;
            public TextView text;
        }


    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

}
