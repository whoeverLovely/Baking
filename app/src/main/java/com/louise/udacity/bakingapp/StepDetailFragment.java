package com.louise.udacity.bakingapp;

import android.app.Dialog;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.louise.udacity.bakingapp.data.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StepDetailFragment extends Fragment {

    @BindView(R.id.playerView_step_video)
    PlayerView stepVideoPlayerView;

    @BindView(R.id.textView_step_description)
    TextView descriptionTextView;

    @BindView(R.id.button_next_step)
    Button nextStepButton;

    @BindView(R.id.textView_no_video)
    TextView noVideoTextView;

    @BindView(R.id.playerView_frame)
            FrameLayout playerFrame;

    FrameLayout mFullScreenButton;

    private Dialog mFullScreenDialog;
    private boolean mExoPlayerFullscreen;

    SimpleExoPlayer player;
    ExtractorMediaSource.Factory mediaSourceFactory;
    private ImageView mFullScreenIcon;
    private int mResumeWindow;
    private long mResumePosition;

    private List<Step> steps;
    private Step currentStep;
    private int index;
    private boolean hasVideo;

    private final static String BUNDLE_INDEX = "currentIndex";

    public StepDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.d("here is fragment onCreate");
        steps = getArguments().getParcelableArrayList(RecipeDetailFragment.BUNDLE_STEPS);
        if (savedInstanceState == null) {
            index = getArguments().getInt(RecipeDetailFragment.EXTRA_INDEX);
        } else {
            index = savedInstanceState.getInt(BUNDLE_INDEX);
        }

        Timber.d("the index at the end of fragment onCreate is %s", index);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("Here is fragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, rootView);

        currentStep = steps.get(index);

        initializePlayer();
        setStepViews(currentStep);

        if (index == steps.size() - 1) {
            nextStepButton.setEnabled(false);
            nextStepButton.setText(getResources().getString(R.string.the_last_step));
        }

        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                player.stop();
                Timber.d("index after click: %s", index);
                if (index == steps.size() - 1) {

                    nextStepButton.setEnabled(false);
                    nextStepButton.setText(getResources().getString(R.string.the_last_step));
                }

                currentStep = steps.get(index);
                setStepViews(currentStep);

            }
        });
        Timber.d("index in onCreateVIew is %s", index);

        initFullscreenDialog();
        initFullscreenButton();

        return rootView;
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            index = savedInstanceState.getInt(BUNDLE_INDEX);
        }
        Timber.d("index in onActivityCreated is %s", index);
    }*/

    private void initializePlayer() {
        // Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // Create the player
        player =
                ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

        // Bind the player to the view.
        stepVideoPlayerView.setPlayer(player);

        // Produces DataSource instances through which media data is loaded.
        ApplicationInfo applicationInfo = getActivity().getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        String appName = stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : getActivity().getString(stringId);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), appName));

        mediaSourceFactory = new ExtractorMediaSource.Factory(dataSourceFactory);

        /*boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            player.seekTo(mResumeWindow, mResumePosition);
        }*/
    }


    private void setStepViews(Step step) {
        String url = step.getVideoURL();
        Timber.d("video url in step detail fragment is %s", url);
        if ("".equals(url)) {
            Timber.d("url is empty");
            hasVideo = false;
            noVideoTextView.setVisibility(View.VISIBLE);
            stepVideoPlayerView.setVisibility(View.GONE);
        } else {
            hasVideo = true;
            noVideoTextView.setVisibility(View.GONE);
            stepVideoPlayerView.setVisibility(View.VISIBLE);
            MediaSource mediaSource = mediaSourceFactory.createMediaSource(Uri.parse(url));
            player.prepare(mediaSource);
        }

        // Set description
        descriptionTextView.setText(step.getDescription());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Timber.d("the index saved is %s", index);
        outState.putInt(BUNDLE_INDEX, index);
    }

    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {

        ((ViewGroup) stepVideoPlayerView.getParent()).removeView(stepVideoPlayerView);
        mFullScreenDialog.addContentView(stepVideoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {

        ((ViewGroup) stepVideoPlayerView.getParent()).removeView(stepVideoPlayerView);
        playerFrame.addView(stepVideoPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fullscreen_expand));
    }

    private void initFullscreenButton() {

        PlayerControlView controlView = stepVideoPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }
}