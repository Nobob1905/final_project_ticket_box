package com.final_project_ticket_box;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.final_project_ticket_box.Adapter.CategoryEachEventAdapter;
import com.final_project_ticket_box.Models.Event;
import com.final_project_ticket_box.databinding.FrameDetailBinding;

import eightbitlab.com.blurview.RenderScriptBlur;

public class EventDetailActivity extends AppCompatActivity {

    private FrameDetailBinding binding;
    private ExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FrameDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setVariable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (exoPlayer != null) {
            exoPlayer.play();  // Tiếp tục phát video nếu đang tạm dừng
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
        super.onBackPressed();
    }

    private void setVariable() {
        Event item = getIntent().getParcelableExtra("object");
        if (item != null) {
            RequestOptions requestOptions = new RequestOptions()
                    .transform(new CenterCrop(), new GranularRoundedCorners(0f, 0f, 50f, 50f));

            Glide.with(this)
                    .load(item.getPoster())
                    .apply(requestOptions)
                    .into(binding.filmPic);

            binding.titleTxt.setText(item.getTitle());
            binding.imdbTxt.setText("IMDB " + item.getImdb());
            binding.movieTimeTxt.setText(item.getDate() + " - " + item.getTime());
            binding.eventSummeryTxt.setText(item.getDescription());

            binding.ShareBtn.setOnClickListener(v -> {
                shareEvent(item);  // Gọi phương thức share khi nhấn nút share
            });


            binding.backBtn.setOnClickListener(v -> finish());
            binding.buyTicketBtn.setOnClickListener(v -> {
                // Tạm dừng video khi mua vé
                if (exoPlayer != null) {
                    exoPlayer.pause();  // Tạm dừng video
                }
                Intent intent = new Intent(EventDetailActivity.this, SeatListActivity.class);
                intent.putExtra("event", item);
                startActivity(intent);
            });

            float radius = 10f;
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            ViewGroup rootView = decorView.findViewById(android.R.id.content);
            android.graphics.drawable.Drawable windowsBackground = decorView.getBackground();

            binding.blurView.setupWith(rootView, new RenderScriptBlur(this))
                    .setFrameClearDrawable(windowsBackground)
                    .setBlurRadius(radius);
            binding.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            binding.blurView.setClipToOutline(true);

            if (item.getGenre() != null) {
                binding.genreView.setAdapter(new CategoryEachEventAdapter(item.getGenre()));
                binding.genreView.setLayoutManager(
                        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                );
            }


            if (item.getTrailer() != null) {

                binding.trailerView.setVisibility(VideoView.VISIBLE);
                PlayerView playerView = binding.trailerView;
                exoPlayer = new ExoPlayer.Builder(this).build();
                playerView.setPlayer(exoPlayer);

                MediaItem mediaItem = MediaItem.fromUri(item.getTrailer());
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.play();
            } else {
                binding.trailerView.setVisibility(VideoView.GONE);
            }
        }
    }

    private void shareEvent(Event item) {
        if (item != null) {
//            // Tạo đối tượng Intent để chia sẻ
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("text/plain");
//
//            // Dữ liệu chia sẻ (Title, Description, Poster)
//            String title = item.getTitle();
//            String description = item.getDescription();
//            String posterUrl = item.getPoster();  // URL của poster
//
//            // Dữ liệu cần chia sẻ (có thể thêm poster vào dưới dạng URL)
//            String shareText = "Check out this event!\n" + title + "\n" + description + "\n" + posterUrl;
//
//            // Truyền dữ liệu vào Intent
//            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
//
//            // Nếu bạn muốn chia sẻ poster (ảnh) thì có thể thêm:
//            if (posterUrl != null) {
//                Uri posterUri = Uri.parse(posterUrl);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, posterUri);
//                shareIntent.setType("image/jpeg");  // hoặc loại hình ảnh phù hợp nếu chia sẻ ảnh
//            }
//
//            // Hiển thị hộp thoại chia sẻ với các ứng dụng như Facebook, Messenger, v.v.
//            Intent chooser = Intent.createChooser(shareIntent, "Share via");
//            startActivity(chooser);

            // Tạo đối tượng Intent để chia sẻ
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            // Dữ liệu chia sẻ (Title, Description, Poster)
            String title = item.getTitle();
            String description = item.getDescription();
            String posterUrl = item.getPoster();  // URL của poster

            // Dữ liệu cần chia sẻ
            String shareText = "Check out this event!\n" + title + "\n" + description + "\n" + posterUrl;

            // Truyền dữ liệu vào Intent
//            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            // Truyền dữ liệu vào Intent
            shareIntent.putExtra(Intent.EXTRA_TEXT, title); // Tiêu đề bài viết
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText); // Mô tả bài viết

            // Hiển thị hộp thoại chia sẻ với các ứng dụng như Facebook, Messenger, v.v.
            Intent chooser = Intent.createChooser(shareIntent, "Share via");
            startActivity(chooser);
        }
    }
}
