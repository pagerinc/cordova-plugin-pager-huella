package org.pager.cordova.huella;

import android.app.Activity;
import android.content.Intent;

import com.pager.example.R;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Huella extends CordovaPlugin {

  /**
   * Key for the title JSON arg.
   */
  private static final String TITLE_KEY = "title";

  /**
   * Key for the description JSON arg.
   */
  private static final String DESCRIPTION_KEY = "description";

  /**
   * Command key for asking is keyguard is available
   */
  private static final String IS_AVAILABLE_COMMAND = "isAvailable";

  /**
   * Command key to show the unlock screen
   */
  private static final String SHOW_UNLOCK_COMMAND = "showUnlock";

  private KeyguardAuth keyguardAuth;

  private CallbackContext keyguardCallback;

  /**
   * The commands with the plugin process.
   */
  private enum COMMANDS {
    IS_AVAILABLE(IS_AVAILABLE_COMMAND), SHOW_UNLOCK_SCREEN(SHOW_UNLOCK_COMMAND), NO_ACTION("");

    private final String name;

    COMMANDS(String name) {
      this.name = name;
    }

    public static COMMANDS from(String action) {
      if (IS_AVAILABLE.name.equals(action)) return IS_AVAILABLE;
      if (SHOW_UNLOCK_SCREEN.name.equals(action)) return SHOW_UNLOCK_SCREEN;
      return NO_ACTION;
    }
  }

  /**
   * Constructor.
   */
  public Huella() {
  }

  /**
   * Sets the context of the Command. This can then be used to do things like
   * get file paths associated with the Activity.
   *
   * @param cordova The context of the main Activity.
   * @param webView The CordovaWebView Cordova is running in.
   */
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    keyguardAuth = new KeyguardAuth(cordova.getActivity());
  }

  /**
   * Executes the request and returns PluginResult.
   *
   * @param action The action to execute.
   * @param args JSONArry of arguments for the plugin.
   * @param callbackContext The callback id used when calling back into JavaScript.
   * @return True if the action was valid, false if not.
   */
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
      throws JSONException {

    switch (COMMANDS.from(action)) {
      case IS_AVAILABLE:
        handleIsAvailable(callbackContext);
        return true;
      case SHOW_UNLOCK_SCREEN:
        handleShowUnlockScreen(callbackContext, args);
        return true;
      default:
        return false;
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == KeyguardAuth.REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        keyguardCallback.success(1);
      } else if (resultCode == Activity.RESULT_CANCELED) {
        keyguardCallback.error(KeyguardAuth.ERROR.AUTH_FAILED.name());
      }
    }
  }

  /**
   * Check if the device have an authentication method and call the js callback.
   *
   * @param callbackContext The callback id used when calling back into JavaScript.
   */
  private void handleIsAvailable(CallbackContext callbackContext) {
    if (keyguardAuth.isDeviceSecure()) {
      callbackContext.success();
    } else {
      callbackContext.error(KeyguardAuth.ERROR.NOT_AVAILABLE.name());
    }
  }

  /**
   * Resolve the intent for start the activity for verify the auth method.
   *
   * @param callbackContext The callback id used when calling back into JavaScript.
   * @param args JSONArray of arguments for the plugin.
   */
  private void handleShowUnlockScreen(CallbackContext callbackContext, JSONArray args)
      throws JSONException {
    keyguardCallback = callbackContext;
    cordova.startActivityForResult(this, intent(from(args)), KeyguardAuth.REQUEST_CODE);
  }

  /**
   * Get the JSON Object from the cordova args.
   *
   * @param args The JSON args from the plugin.
   * @return a JSONObject from the original args, if the value doesn't exist return a empty
   * JSONObject.
   */
  private JSONObject from(JSONArray args) {
    try {
      return args.getJSONObject(0);
    } catch (JSONException e) {
      return new JSONObject();
    }
  }

  /**
   * Creates a default intent to display keyguard.
   *
   * @param jsonArgs JSON args
   * @return a keyguard intent
   * @throws JSONException
   */
  private Intent intent(JSONObject jsonArgs) throws JSONException {
    String title = string(jsonArgs, TITLE_KEY, R.string.default_title);
    String description = string(jsonArgs, DESCRIPTION_KEY, R.string.default_description);
    return keyguardAuth.createAuthIntent(title, description);
  }

  /**
   * Retrieves a default string if the original in the args object does not exists
   *
   * @param args Arguments to ckeck
   * @param key key from the argument to retrieve in the args
   * @param resource resource of default string
   * @return non null string
   */
  private String string(JSONObject args, String key, int resource) {
    if (args != null && args.has(key)) {
      try {
        return args.getString(key);
      } catch (JSONException e) {
        return cordova.getActivity().getString(resource);
      }
    }
    return cordova.getActivity().getString(resource);
  }
}
