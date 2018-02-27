/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package com.cashow.userdetails;

import android.os.Bundle;

import com.cashow.baselibrary.activity.BaseActivity;

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator.navigateToUserDetails(this, 3);
        finish();
    }
}
