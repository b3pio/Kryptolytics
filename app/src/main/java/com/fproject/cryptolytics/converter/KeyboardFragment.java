package com.fproject.cryptolytics.converter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fproject.cryptolytics.R;

/**
 * A custom {@link Fragment} used for editing numerical values.
 */
public class KeyboardFragment extends Fragment implements View.OnClickListener {

    // The text is being modified using the keyboard.
    private String text = null;

    // The listener to notify when the text changes.
    private OnTextChangedListener textChangedListener;

    /**
     * Required empty public constructor
     */
    public KeyboardFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        //
        //
        //
        setupListeners(view);
        //
        //
        //
        return view;
    }

    /**
     * Hook up the event listeners.
     */
    private void setupListeners(View view) {
        Button btnZero  = view.findViewById(R.id.btn_zero);
        btnZero.setOnClickListener(this);

        Button btnOne   = view.findViewById(R.id.btn_one);
        btnOne.setOnClickListener(this);

        Button btnTwo   = view.findViewById(R.id.btn_two);
        btnTwo.setOnClickListener(this);

        Button btnThree = view.findViewById(R.id.btn_three);
        btnThree.setOnClickListener(this);

        Button btnFour  = view.findViewById(R.id.btn_four);
        btnFour.setOnClickListener(this);

        Button btnFive  = view.findViewById(R.id.btn_five);
        btnFive.setOnClickListener(this);

        Button btnSix   = view.findViewById(R.id.btn_six);
        btnSix.setOnClickListener(this);

        Button btnSeven = view.findViewById(R.id.btn_seven);
        btnSeven.setOnClickListener(this);

        Button btnEight = view.findViewById(R.id.btn_eight);
        btnEight.setOnClickListener(this);

        Button btnNine  = view.findViewById(R.id.btn_nine);
        btnNine.setOnClickListener(this);

        Button btnDot = view.findViewById(R.id.btn_dot);
        btnDot.setOnClickListener(this);

        Button btnDel = view.findViewById(R.id.btn_del);
        btnDel.setOnClickListener(this);
    }

    /**
     * Set the text that will be modified using the keyboard.
     * @param text - the string that will be modified by the Fragment.
     */
    public void setText(String text){
        this.text = text;
        this.text = "0";
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnTextChangedListener) {
            textChangedListener = (OnTextChangedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTextChangedListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        textChangedListener = null;
    }

    @Override
    public void onClick(View view) {
        if ((text == null) || (view.getTag() == null))
            return;

        String charStr = String.valueOf(view.getTag());


        switch (charStr) {
            case "ZERO":
                onZeroClicked();
                break;
            case "DOT":
                onDotClicked();
                break;
            case "DEL":
                onDeleteClicked();
                break;
            default:
                onNumberClicked(charStr);
                break;
        }

        if (textChangedListener != null) {
            textChangedListener.onTextChanged(text);
        }
    }

    public void onDeleteClicked() {
        text = text.substring(0, text.length() - 1);
        text = text.isEmpty() ? "0" : text;
    }

    public void onDotClicked() {
        if (text.contains("."))
            return;

        text += ".";
    }

    public void onZeroClicked(){
        if (text.equals("0"))
            return;

        text +="0";
    }

    public void onNumberClicked(String number){
        if (!text.equals("0")){
            text += String.valueOf(number);
        }
        else {
            text = String.valueOf(number);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnTextChangedListener {

        /**
         * This method is executed when the text is modified using the {@link KeyboardFragment}
         * @param str - the text after modification.
         */
        void onTextChanged(String  str);
    }
}
