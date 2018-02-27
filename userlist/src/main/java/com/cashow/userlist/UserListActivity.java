/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package com.cashow.userlist;

import android.os.Bundle;
import android.view.Window;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cashow.baselibrary.activity.BaseActivity;
import com.cashow.baselibrary.di.HasComponent;
import com.cashow.baselibrary.model.UserModel;

/**
 * Activity that shows a list of Users.
 */
@Route(path = "/userlist/activity")
public class UserListActivity extends BaseActivity implements HasComponent<UserListComponent>,
        UserListFragment.UserListListener {

    private UserListComponent userComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_layout);

        this.initializeInjector();
        if (savedInstanceState == null) {
            addFragment(R.id.fragmentContainer, new UserListFragment());
        }
    }

    private void initializeInjector() {
        this.userComponent = DaggerUserListComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    @Override
    public UserListComponent getComponent() {
        return userComponent;
    }

    @Override
    public void onUserClicked(UserModel userModel) {
        this.navigator.navigateToUserDetails(this, userModel.getUserId());
    }
}
