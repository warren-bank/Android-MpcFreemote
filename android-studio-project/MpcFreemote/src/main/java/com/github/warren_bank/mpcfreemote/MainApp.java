package com.github.warren_bank.mpcfreemote;

import android.app.Application;

public class MainApp extends Application {
  private static MainApp instance;

  public static MainApp getInstance() {
    return instance;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
  }
}
