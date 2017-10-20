package com.company.sts_ar.di;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 * Created by thuongle on 07/09/15.
 */
@Module
public final class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return this.application;
    }

    @Provides
    @ApplicationScope
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    public RequestManager provideRequestManager() {
        return Glide.with(application);
    }
}
