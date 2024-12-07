package com.final_project_ticket_box.Settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.final_project_ticket_box.LoginRegister.Login;
import com.final_project_ticket_box.ModelsSingleton.UserSession;
import com.final_project_ticket_box.MyTicketFragment;
import com.final_project_ticket_box.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class SettingFragment extends Fragment {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView imageView;
    TextView textViewName;
    TextView editProfile,editPassword,editMyTicket;
    ProfileFragment profileFragment = new ProfileFragment();
    ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
    MyTicketFragment myTicketFragment = new MyTicketFragment();
//    MyOrderFragment myOrderFragment = new MyOrderFragment();
//    LanguageFragment languageFragment = new LanguageFragment();
//    SoldFragment soldFragment = new SoldFragment();
    TextView btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        imageView = view.findViewById(R.id.profileCircleImageView);
        textViewName = view.findViewById(R.id.usernameTextView);
        editProfile = view.findViewById(R.id.profile);
        editPassword = view.findViewById(R.id.editPassword);
        editMyTicket = view.findViewById(R.id.myTicket);
        btnLogout = view.findViewById(R.id.btnLogout);

        if (!user.isAnonymous()) {
            String username = UserSession.getInstance().getName();
            if (username == null || username.trim().isEmpty()) {
                String email = user.getEmail(); // Lấy email từ FirebaseUser
                if (email != null && email.contains("@")) {
                    username = email.split("@")[0]; // Lấy phần trước của @
                } else {
                    username = "Guest"; // Giá trị mặc định nếu email không khả dụng
                }
            }
            textViewName.setText("Hello! " + username);
            Bitmap userImage = UserSession.getInstance().getImage();
            if (userImage != null) {
                imageView.setImageBitmap(userImage);
            } else {
                imageView.setImageResource(R.drawable.avatar); // Thay bằng ảnh mặc định
            }
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.isAnonymous()) {
                    Toast.makeText(getContext(),"User need to login!",Toast.LENGTH_SHORT).show();
                    return;
                }
                FragmentManager fragmentManager = getFragmentManager();

                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.container, profileFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.isAnonymous()) {
                    Toast.makeText(getContext(),"User need to login!",Toast.LENGTH_SHORT).show();
                    return;
                }
                FragmentManager fragmentManager = getFragmentManager();

                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.container, changePasswordFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        editMyTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.isAnonymous()) {
                    Toast.makeText(getContext(),"User need to login!",Toast.LENGTH_SHORT).show();
                    return;
                }
                FragmentManager fragmentManager = getFragmentManager();

                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.container, myTicketFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(),"Log out successful",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
