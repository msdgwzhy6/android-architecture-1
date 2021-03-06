package com.thepacific.presentation.core;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import dagger.android.support.DaggerFragment;
import io.reactivex.internal.functions.ObjectHelper;
import javax.annotation.CheckForNull;
import javax.inject.Inject;

public abstract class Fragment extends DaggerFragment implements LifecycleRegistryOwner {

  private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
  private ViewModel realViewModel;

  @Inject
  protected ViewModelFactory modelFactory;

  @Override
  public LifecycleRegistry getLifecycle() {
    return lifecycleRegistry;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    switch (modelProvider()) {
      case ACTIVITY:
        realViewModel = ViewModelProviders.of(getActivity(), modelFactory)
            .get(modelClass());
        break;
      case PARENT_FRAGMENT:
        realViewModel = ViewModelProviders.of(getParentFragment(), modelFactory)
            .get(modelClass());
        break;
      case NONE:
        realViewModel = ViewModelProviders.of(this, modelFactory)
            .get(modelClass());
        break;
      default:
        throw new UnsupportedOperationException();
    }
    lifecycleRegistry.addObserver(realViewModel);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (!view.isClickable()) {
      view.setClickable(true);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    lifecycleRegistry.removeObserver(realViewModel);
  }

  protected ViewModel.Provider modelProvider() {
    return ViewModel.Provider.ACTIVITY;
  }

  @CheckForNull
  protected final <T extends ViewModel> T fetchViewModel() {
    return (T) ObjectHelper.requireNonNull(realViewModel, "is null");
  }

  protected abstract Class<? extends ViewModel> modelClass();
}
