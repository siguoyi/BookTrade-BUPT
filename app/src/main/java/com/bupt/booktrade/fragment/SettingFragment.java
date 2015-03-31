package com.bupt.booktrade.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.bupt.booktrade.R;

import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;


public class SettingFragment extends BaseFragment {

    private ProgressBar mProgressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressbar_circular);
        mProgressBar.setIndeterminateDrawable(new CircularProgressDrawable
                .Builder(getActivity())
                .colors(getResources().getIntArray(R.array.google_colors))
                .sweepSpeed(1f)
                .strokeWidth(5)
                .style(CircularProgressDrawable.Style.ROUNDED)
                .build());
        return rootView;
    }
}
