package org.pager.cordova.huella;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;

class KeyguardAuth {

  private final KeyguardManager keyguardManager;

  static final int REQUEST_CODE = 1;

  public enum ERROR {
    AUTH_FAILED, NOT_AVAILABLE
  }

  KeyguardAuth(Activity activity) {
    this.keyguardManager = activity.getSystemService(KeyguardManager.class);
  }

  Intent createAuthIntent(String title, String description) {
    return keyguardManager.createConfirmDeviceCredentialIntent(title, description);
  }

  boolean isDeviceSecure() {
    return keyguardManager.isDeviceSecure();
  }
}
