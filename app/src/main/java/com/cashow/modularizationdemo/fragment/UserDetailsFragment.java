/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package com.cashow.modularizationdemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cashow.domain.User;
import com.cashow.domain.exception.DefaultErrorBundle;
import com.cashow.domain.exception.ErrorBundle;
import com.cashow.domain.interactor.DefaultObserver;
import com.cashow.domain.interactor.GetUserDetails;
import com.cashow.modularizationdemo.R;
import com.cashow.modularizationdemo.di.components.UserComponent;
import com.cashow.modularizationdemo.exception.ErrorMessageFactory;
import com.cashow.modularizationdemo.mapper.UserModelDataMapper;
import com.cashow.modularizationdemo.model.UserModel;
import com.cashow.modularizationdemo.widget.AutoLoadImageView;
import com.fernandocejas.arrow.checks.Preconditions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Fragment that shows details of a certain user.
 */
public class UserDetailsFragment extends BaseFragment {

    @BindView(R.id.iv_cover)
    AutoLoadImageView iv_cover;
    @BindView(R.id.tv_fullname)
    TextView tv_fullname;
    @BindView(R.id.tv_email)
    TextView tv_email;
    @BindView(R.id.tv_followers)
    TextView tv_followers;
    @BindView(R.id.tv_description)
    TextView tv_description;
    @BindView(R.id.rl_progress)
    RelativeLayout rl_progress;
    @BindView(R.id.rl_retry)
    RelativeLayout rl_retry;
    @BindView(R.id.bt_retry)
    Button bt_retry;

    @Inject
    GetUserDetails getUserDetailsUseCase;
    @Inject
    UserModelDataMapper userModelDataMapper;

    private Unbinder unbinder;

    private static final String PARAM_USER_ID = "param_user_id";

    public static UserDetailsFragment forUser(int userId) {
        final UserDetailsFragment userDetailsFragment = new UserDetailsFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt(PARAM_USER_ID, userId);
        userDetailsFragment.setArguments(arguments);
        return userDetailsFragment;
    }

    public UserDetailsFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(UserComponent.class).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_user_details, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            loadUserDetails();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getUserDetailsUseCase.dispose();
    }

    public void renderUser(UserModel user) {
        if (user != null) {
            iv_cover.setImageUrl(user.getCoverUrl());
            tv_fullname.setText(user.getFullName());
            tv_email.setText(user.getEmail());
            tv_followers.setText(String.valueOf(user.getFollowers()));
            tv_description.setText(user.getDescription());
        }
    }

    public void showLoading() {
        rl_progress.setVisibility(View.VISIBLE);
        getActivity().setProgressBarIndeterminateVisibility(true);
    }

    public void hideLoading() {
        rl_progress.setVisibility(View.GONE);
        getActivity().setProgressBarIndeterminateVisibility(false);
    }

    public void showRetry() {
        rl_retry.setVisibility(View.VISIBLE);
    }

    public void hideRetry() {
        this.rl_retry.setVisibility(View.GONE);
    }

    public Context context() {
        return getActivity().getApplicationContext();
    }

    /**
     * Load user details.
     */
    private void loadUserDetails() {
        this.hideRetry();
        this.showLoading();
        this.getUserDetails(currentUserId());
    }

    /**
     * Get current user id from fragments arguments.
     */
    private int currentUserId() {
        final Bundle arguments = getArguments();
        Preconditions.checkNotNull(arguments, "Fragment arguments cannot be null");
        return arguments.getInt(PARAM_USER_ID);
    }

    @OnClick(R.id.bt_retry)
    void onButtonRetryClick() {
        UserDetailsFragment.this.loadUserDetails();
    }

    private void getUserDetails(int userId) {
        this.getUserDetailsUseCase.execute(new UserDetailsObserver(), GetUserDetails.Params.forUser(userId));
    }

    private void showErrorMessage(ErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.context(),
                errorBundle.getException());
        this.showToastMessage(errorMessage);
    }

    private void showUserDetailsInView(User user) {
        final UserModel userModel = userModelDataMapper.transform(user);
        renderUser(userModel);
    }

    private final class UserDetailsObserver extends DefaultObserver<User> {

        @Override
        public void onComplete() {
            hideLoading();
        }

        @Override
        public void onError(Throwable e) {
            hideLoading();
            showErrorMessage(new DefaultErrorBundle((Exception) e));
            showRetry();
        }

        @Override
        public void onNext(User user) {
            showUserDetailsInView(user);
        }
    }
}
