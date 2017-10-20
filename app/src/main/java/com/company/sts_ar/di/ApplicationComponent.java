package com.company.sts_ar.di;

import android.app.Application;
import android.content.Context;

import com.company.sts_ar.data.SharedVariables;
import com.company.sts_ar.view.MainActivity;
import com.company.sts_ar.view.menu.DetailActivity;
import com.company.sts_ar.view.menu.MenuActivity;

import javax.inject.Singleton;

import dagger.Component;


/**
 * Created by thuongle on 07/09/15.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    Application application();

    @ApplicationScope
    Context context();

    SharedVariables sharedVariables();

    void inject(MenuActivity __);

    void inject(MainActivity __);

    void inject(DetailActivity __);
}
