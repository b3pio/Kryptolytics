package com.fproject.cryptolytics.utility;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextWatcher;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom TextView component.
 *  Needed because the standard component does not allow removal of the text change listeners.
 */
public class ExtTextView extends AppCompatTextView {
    private List<TextWatcher> mListeners = new ArrayList<>();

    public ExtTextView(Context context)  {
        super(context);
    }

    public ExtTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ExtTextView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    /**
     * Removes all the {@link TextWatcher} object form the the {@link ExtTextView}
     */
    public void clearTextChangedListeners()  {
        for(TextWatcher watcher : mListeners)  {
            super.removeTextChangedListener(watcher);
        }
        mListeners.clear();
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        mListeners.add(watcher);
        super.addTextChangedListener(watcher);
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher)  {
        mListeners.remove(watcher);
        super.removeTextChangedListener(watcher);
    }

}