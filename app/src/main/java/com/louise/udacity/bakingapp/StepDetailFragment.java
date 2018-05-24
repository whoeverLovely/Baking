package com.louise.udacity.bakingapp;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
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
import com.squareup.picasso.Picasso;

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

    @BindView(R.id.imageView_step_thumbnail)
    ImageView stepThumbnail;

    FrameLayout mFullScreenButton;

    private Dialog mFullScreenDialog;
    private boolean mExoPlayerFullscreen;

    SimpleExoPlayer player;
    ExtractorMediaSource.Factory mediaSourceFactory;
    private ImageView mFullScreenIcon;

    private List<Step> steps;
    private Step currentStep;
    private int index;
    private boolean hasVideo;
    private long currentPosition;
    private boolean playState;

    public final static String BUNDLE_INDEX = "currentIndex";
    public final static String BUNDLE_CURRENT_POSITION = "currentPosition";
    public final static String BUNDLE_PLAY_STATE = "playState";

    public StepDetailFragment() {
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("Here is fragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, rootView);

        initFullscreenDialog();
        initFullscreenButton();

        if (savedInstanceState != null) {
            index = savedInstanceState.getInt(BUNDLE_INDEX);
            currentPosition = savedInstanceState.getLong(BUNDLE_CURRENT_POSITION);
            playState = savedInstanceState.getBoolean(BUNDLE_PLAY_STATE, true);

            Timber.d("currentPosition retrieved from savedInstanceState is " + currentPosition);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

        if (mExoPlayerFullscreen && hasVideo) {
            openFullscreenDialog();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Timber.d("orientation changed");
        Timber.d("newConfig.orientation: " + newConfig.orientation);
        Timber.d("mExoPlayerFullscreen: " + mExoPlayerFullscreen);
        Timber.d("hasVideo " + hasVideo);
        // Checks the orientation of the screen
        // When the video is not full screen and new Congfin is landscape and the video source is available, update to full screen mode
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && !mExoPlayerFullscreen && hasVideo) {
            openFullscreenDialog();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_INDEX, index);
        outState.putLong(BUNDLE_CURRENT_POSITION, player.getCurrentPosition());
        outState.putBoolean(BUNDLE_PLAY_STATE, player.getPlayWhenReady());

        Timber.d("the index saved is %s", index);
        Timber.d("the video position saved %s", player.getCurrentPosition());

    }

    @Override
    public void onStop() {
        super.onStop();
        player.release();
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
        player.setPlayWhenReady(true);

        // Produces DataSource instances through which media data is loaded.
        ApplicationInfo applicationInfo = getActivity().getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        String appName = stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : getActivity().getString(stringId);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), appName));

        mediaSourceFactory = new ExtractorMediaSource.Factory(dataSourceFactory);
    }


    private void setStepViews(Step step) {
        String videoURL = step.getVideoURL();
        String thumbnailUrl = step.getThumbnailURL();
        Timber.d("video url in step detail fragment is %s", videoURL);
        if ("".equals(videoURL)) {
            Timber.d("url is empty");
            hasVideo = false;

            stepThumbnail.setVisibility(View.VISIBLE);
            if (!"".equals(thumbnailUrl))
                Picasso.get().load(thumbnailUrl).into(stepThumbnail);

            noVideoTextView.setVisibility(View.VISIBLE);
            stepVideoPlayerView.setVisibility(View.GONE);
        } else {
            hasVideo = true;
            stepThumbnail.setVisibility(View.GONE);
            noVideoTextView.setVisibility(View.GONE);
            stepVideoPlayerView.setVisibility(View.VISIBLE);
            MediaSource mediaSource = mediaSourceFactory.createMediaSource(Uri.parse(videoURL));
            player.prepare(mediaSource);

            player.setPlayWhenReady(playState);
            Timber.d("the current postion in setStepViews is: " + currentPosition);
            if (currentPosition > 0)
                player.seekTo(currentPosition);
        }

        // Set description
        descriptionTextView.setText(step.getDescription());
    }

    public void setNextStepButtonVisibility(int visibility) {
        if (nextStepButton == null)
            nextStepButton = getActivity().findViewById(R.id.button_next_step);
        nextStepButton.setVisibility(visibility);
    }
}