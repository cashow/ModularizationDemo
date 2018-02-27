/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package com.cashow.modularizationdemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cashow.domain.User;
import com.cashow.domain.exception.DefaultErrorBundle;
import com.cashow.domain.exception.ErrorBundle;
import com.cashow.domain.interactor.DefaultObserver;
import com.cashow.domain.interactor.GetUserList;
import com.cashow.modularizationdemo.R;
import com.cashow.modularizationdemo.adapter.UsersAdapter;
import com.cashow.modularizationdemo.adapter.UsersLayoutManager;
import com.cashow.modularizationdemo.di.components.UserComponent;
import com.cashow.modularizationdemo.exception.ErrorMessageFactory;
import com.cashow.modularizationdemo.mapper.UserModelDataMapper;
import com.cashow.modularizationdemo.model.UserModel;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Fragment that shows a list of Users.
 */
public class UserListFragment extends BaseFragment {

    /**
     * Interface for listening user list events.
     */
    public interface UserListListener {
        void onUserClicked(final UserModel userModel);
    }

    @Inject
    GetUserList getUserListUseCase;
    @Inject
    UserModelDataMapper userModelDataMapper;

    @Inject
    UsersAdapter usersAdapter;

    @BindView(R.id.rv_users)
    RecyclerView rv_users;
    @BindView(R.id.rl_progress)
    RelativeLayout rl_progress;
    @BindView(R.id.rl_retry)
    RelativeLayout rl_retry;
    @BindView(R.id.bt_retry)
    Button bt_retry;

    private Unbinder unbinder;

    private UserListListener userListListener;

    public UserListFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof UserListListener) {
            this.userListListener = (UserListListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(UserComponent.class).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_user_list, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);
        setupRecyclerView();
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            this.loadUserList();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rv_users.setAdapter(null);
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.getUserListUseCase.dispose();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.userListListener = null;
    }

    public void showLoading() {
        this.rl_progress.setVisibility(View.VISIBLE);
        this.getActivity().setProgressBarIndeterminateVisibility(true);
    }

    public void hideLoading() {
        this.rl_progress.setVisibility(View.GONE);
        this.getActivity().setProgressBarIndeterminateVisibility(false);
    }

    public void showRetry() {
        this.rl_retry.setVisibility(View.VISIBLE);
    }

    public void hideRetry() {
        this.rl_retry.setVisibility(View.GONE);
    }

    public void renderUserList(Collection<UserModel> userModelCollection) {
        if (userModelCollection != null) {
            this.usersAdapter.setUsersCollection(userModelCollection);
        }
    }

    public void viewUser(UserModel userModel) {
        if (this.userListListener != null) {
            this.userListListener.onUserClicked(userModel);
        }
    }

    public void showError(String message) {
        this.showToastMessage(message);
    }

    public Context context() {
        return this.getActivity().getApplicationContext();
    }

    private void setupRecyclerView() {
        this.usersAdapter.setOnItemClickListener(onItemClickListener);
        this.rv_users.setLayoutManager(new UsersLayoutManager(context()));
        this.rv_users.setAdapter(usersAdapter);
    }

    private void loadUserList() {
        this.hideRetry();
        this.showLoading();
        this.getUserList();
    }

    @OnClick(R.id.bt_retry)
    void onButtonRetryClick() {
        UserListFragment.this.loadUserList();
    }

    private UsersAdapter.OnItemClickListener onItemClickListener =
            userModel -> {
                if (userModel != null) {
                    viewUser(userModel);
                }
            };

    private void showErrorMessage(ErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(context(),
                errorBundle.getException());
        showError(errorMessage);
    }

    private void showUsersCollectionInView(Collection<User> usersCollection) {
        final Collection<UserModel> userModelsCollection =
                this.userModelDataMapper.transform(usersCollection);
        renderUserList(userModelsCollection);
    }

    private void getUserList() {
        this.getUserListUseCase.execute(new UserListObserver(), null);
    }

    private final class UserListObserver extends DefaultObserver<List<User>> {

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
        public void onNext(List<User> users) {
            showUsersCollectionInView(users);
        }
    }
}
