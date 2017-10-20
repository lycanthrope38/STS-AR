package com.company.sts_ar.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by thuongle on 07/09/15.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationScope {
}
