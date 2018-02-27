/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cashow.userdetails;

import com.cashow.baselibrary.di.PerActivity;
import com.cashow.baselibrary.di.components.ActivityComponent;
import com.cashow.baselibrary.di.components.ApplicationComponent;
import com.cashow.baselibrary.di.modules.ActivityModule;
import com.cashow.baselibrary.di.modules.UserModule;

import dagger.Component;

/**
 * A scope {@link PerActivity} component.
 * Injects user specific Fragments.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, UserModule.class})
public interface UserDetailsComponent extends ActivityComponent {
    void inject(UserDetailsFragment userDetailsFragment);
}
