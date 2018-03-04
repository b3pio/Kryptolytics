package com.fproject.cryptolytics.converter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fproject.cryptolytics.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link KeyboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link KeyboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeyboardFragment extends Fragment implements View.OnClickListener {
    private final static String MODULE_TAG = "[KeyboardFragment]";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView textView = null;
    private OnFragmentInteractionListener mListener;

    public KeyboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KeyboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KeyboardFragment newInstance(String param1, String param2) {
        KeyboardFragment fragment = new KeyboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);

        setupListeners(view);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public  void setTextView(TextView textView){
        this.textView = textView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      /*
        if (context instanceof OnConvertItemClickListener) {
            mListener = (OnConvertItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnConvertItemClickListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setupListeners(View view) {
        Button btnOne = view.findViewById(R.id.btn_one);
        btnOne.setOnClickListener(this);

        Button btnSeven = view.findViewById(R.id.btn_seven);
        btnSeven.setOnClickListener(this);

        Button btnEight = view.findViewById(R.id.btn_eight);
        btnEight.setOnClickListener(this);

        Button btnNine  = view.findViewById(R.id.btn_nine);
        btnNine.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (textView == null)
            return;

        if (view.getTag().equals("7")) {
            textView.setText("7");
        }

        if (view.getTag().equals("8")) {
            textView.setText("8");
        }

        if (view.getTag().equals("9")) {
            textView.setText("9");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
