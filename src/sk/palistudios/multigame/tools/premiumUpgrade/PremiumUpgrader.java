package sk.palistudios.multigame.tools.premiumUpgrade;

// @author Pali

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import sk.palistudios.multigame.tools.Toaster;

public class PremiumUpgrader {

    static final String SKU_PREMIUM = "premium";
    static final int RC_REQUEST = 10001;
    static IabHelper mHelper;
    static boolean mIsPremium = false;
    //    @Override
//    protected static void onActivityResult(int requestCode, int resultCode, Intent data) {
////        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
//
//        // Pass on the activity result to the helper for handling
//        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
//            // not handled, so handle it ourselves (here's where you'd
//            // perform any handling of activity results not related to in-app
//            // billing...
//            super.onActivityResult(requestCode, resultCode, data);
//        } else {
////            Log.d(TAG, "onActivityResult handled by IABUtil.");
//        }
//    }
    // Callback for when a purchase is finished
    static IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
//            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }
//            if (!verifyDeveloperPayload(purchase)) {
//                complain("Error purchasing. Authenticity verification failed.");
//                setWaitScreen(false);
//                return;
//            }

//            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
//                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                mIsPremium = true;
                updateUi();
                setWaitScreen(false);
            }

        }
    };
    private static Activity mAct = null;
    private static IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
//            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Toaster.toastLong("Failed to query inventory: " + result, mAct);
                return;
            }

//            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null);
//                        && verifyDeveloperPayload(premiumPurchase));
//            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));


            updateUi();
            setWaitScreen(false);
//            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    public static void upgrade(final Activity act) {
        String string3 = "GD3OkFInTRfMUHBvZOgNAX/dEqX5Zb3iLSkPqXSkeVXDtzOaDEw9SiwYWOtFay/ZZbcfhna3YucSF1v79TW6TvV22AhbkeM/2fUTlwf1zB9KX23/42tg++/PJuBxEBWNbiuCog";
        String string1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzO8s+2c6t6D2k/Od";
        String string2 = "L3LxwfYGrKN+OcOq1pYUnqaQHQesYuxRxC+AvIF32g3s5Oxl51TGQc93CqH3yJjB";
        String string4 = "csKFHQqhMXDt4H/5ibuXpyz0CntrUHwHcaC2JsCqJrK8o5PoOb08wM4yeoBxI8LLrF9zS03OCtAVy8n6Nw+hI0syCbGJlbbCYcEU0wk8466oPhRca4vyyK95pWk2+mwwIDAQAB";

        String base64EncodedPublicKey = string1 + string2 + string3 + string4;

        mAct = act;

        mHelper = new IabHelper(act, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);//2DO M change na false pri publishovaní

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
//                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
//                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
//                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });


    }

    // User clicked the "Upgrade to Premium" button.
    public static void onUpgradeAppButtonClicked(View arg0) {
//        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");
        setWaitScreen(true);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        mHelper.launchPurchaseFlow(mAct, SKU_PREMIUM, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }
//    // Called when consumption is complete
//    static IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
//        public void onConsumeFinished(Purchase purchase, IabResult result) {
////            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
//
//            // We know this is the "gas" sku because it's the only one we consume,
//            // so we don't check which sku was consumed. If you have more than one
//            // sku, you probably should check...
//            if (result.isSuccess()) {
//                // successfully consumed, so we apply the effects of the item in our
//                // game world's logic, which in our case means filling the gas tank a bit
////                Log.d(TAG, "Consumption successful. Provisioning.");
//                alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");
//            } else {
//                complain("Error while consuming: " + result);
//            }
//            updateUi();
//            setWaitScreen(false);
////            Log.d(TAG, "End consumption flow.");
//        }
//    };

    public static void finish() {

        if (mHelper != null) {
            mHelper.dispose();
        }
        mHelper = null;
    }

    public static void updateUi() {
    }

    public static void setWaitScreen(boolean set) {
    }

    private static void complain(String message) {
        alert("Error: " + message);
    }

    private static void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(mAct);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }
}
