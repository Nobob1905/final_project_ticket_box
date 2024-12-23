package com.final_project_ticket_box;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.final_project_ticket_box.LoginRegister.MainActivity;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    private LottieAnimationView lottie;
    private TextView textView;
    private String notification;

    public LoadingDialog(Activity activity, String notification) {
        this.activity = activity;
        this.notification = notification;
    }

    public void loadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_dialog, null);
        builder.setView(dialogView);

        lottie = dialogView.findViewById(R.id.lottieAnimationView);
        lottie.playAnimation();

        textView = dialogView.findViewById(R.id.textViewDialog);
        textView.setText(notification);

        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog() {
        // Set animation to success
        lottie.setAnimation(R.raw.successful);
        lottie.playAnimation();
        textView.setText("Done!");

        // Wait for the success animation to complete before dismissing
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();  // Dismiss the loading dialog after success animation
                showSuccessDialog();  // Show success message after dismissing loading
            }
        }, 1500);  // Delay to match the animation duration
    }

    // Show success dialog after loading is finished
    private void showSuccessDialog() {
        new AlertDialog.Builder(activity)
                .setTitle("Payment Successful")
                .setMessage("Your payment was successful. Returning to the home page.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Navigate back to the MainActivity
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the current activity stack
                    activity.startActivity(intent);  // Start the MainActivity
                    activity.finish();  // Finish the PaymentActivity
                })
                .show();
    }
}
