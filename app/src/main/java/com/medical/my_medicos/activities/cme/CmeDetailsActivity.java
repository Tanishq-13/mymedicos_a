package com.medical.my_medicos.activities.cme;

import static android.content.ContentValues.TAG;
import static android.net.Uri.encode;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medical.my_medicos.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.databinding.ActivityCmeDetailsBinding;
import com.medical.my_medicos.databinding.ActivityEnterOtpBinding;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CmeDetailsActivity extends AppCompatActivity {

    ActivityCmeDetailsBinding binding;
    private ExoPlayer player;
    private ProgressDialog progressDialog;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    Toolbar toolbar;
    String field3,field7,email,receivedDataCme,videoURL,field4,documentid;
    FirebaseFirestore dc = FirebaseFirestore.getInstance();
    Button reservedcmebtn,viewlocationcmebtn,reservecmebtn;
    String current = user.getPhoneNumber();
    private boolean isReserved;
    LinearLayout playerlayout;
    String pdf = null;
    ImageView circularImageView;
    String cmetitle,cmedescription;
    TextView typecmelink;
    String typefordeep;
    Button sharebtnforcmepast,sharebtnforcme;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCmeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.detailtoolbar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_bk);
        }
        StyledPlayerView playerView = findViewById(R.id.player_view);
        player = new ExoPlayer.Builder(CmeDetailsActivity.this).build();
        playerView.setPlayer(player);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TextView presenter3 = findViewById(R.id.presenters3);
        final TextView textView = findViewById(R.id.description);
        TextView date = findViewById(R.id.date);
        TextView time = findViewById(R.id.time1);
        TextView Name = findViewById(R.id.speakername);
        TextView Speciality = findViewById(R.id.speciality);
        TextView Type = findViewById(R.id.type);
        reservecmebtn = findViewById(R.id.reservecmebtn);
        reservedcmebtn = findViewById(R.id.reservedcmebtn);
        Button liveendpost = findViewById(R.id.liveendpost);
        TextView addpresent = findViewById(R.id.addpresenter);
        addpresent.setVisibility(View.GONE);
        String mode = getIntent().getExtras().getString("mode");
        Button attendCmeBtn = findViewById(R.id.attendcmebtn);
        Button viewLocationBtn = findViewById(R.id.viewlocationcmebtn);

        String field1 = getIntent().getExtras().getString("documentid");
        if (field1 == null || field1.isEmpty()){
            field1 = getIntent().getExtras().getString("cmeId");
            Log.e("coming id",field1);

        }

        String name = getIntent().getExtras().getString("name");
        String field6 = getIntent().getExtras().getString("time");
        String field5 = getIntent().getExtras().getString("type");
        Type.setText(field5);
        LinearLayout downloadPdfButton = findViewById(R.id.downloadPdfButton);
        TextView presenters2 = findViewById(R.id.presenters1);
        LinearLayout reservebtn = findViewById(R.id.reservbtn);
        LinearLayout playerlayout = findViewById(R.id.playerlayout);
        boolean isCreator = current.equals(name);
        SharedPreferences sharedPref = getSharedPreferences("CmeDetails", Context.MODE_PRIVATE);
        isReserved = sharedPref.getBoolean("isReserved", false);
        reservecmebtn.setVisibility(isReserved ? View.GONE : View.VISIBLE);
        reservedcmebtn.setVisibility(isReserved ? View.VISIBLE : View.GONE);
        presenter3.setVisibility(View.GONE);
        checkReservationStatusFirestore(current, field1);
        LinearLayout livebtn = findViewById(R.id.livebtn);
        LinearLayout pastbtn = findViewById(R.id.pastbtn);
        ImageView defaultimageofcme = findViewById(R.id.defaultimage);
        LinearLayout textpartforlive = findViewById(R.id.textpartforlive);
        LinearLayout textpartforlivecreator = findViewById(R.id.textpartforlivecreator);
        Button attendcmebtn = findViewById(R.id.attendcmebtn);
        Button livecmebtn = findViewById(R.id.livecmebtn);
        liveendpost.setVisibility(View.GONE);
        Button Schedule = findViewById(R.id.schedulemeet);
        circularImageView = findViewById(R.id.circularImageView);
        LinearLayout textpartforupcoming = findViewById(R.id.textpartforupcoming);
        RelativeLayout relativeboxnotforpast = findViewById(R.id.relativeboxnotforpast);
        Query query = db.collection("CME");
        String finalField = field1;
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    hideLoader();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        String User = ((String) dataMap.get("User"));
                        field3 = ((String) dataMap.get("documentId"));
                        Log.e("document Id -----------------",field3);
                        documentid = ((String) dataMap.get("documentId"));
                        if (field3 != null) {
                            int r = finalField.compareTo(field3);
                            Log.e("Final field ----------------",finalField);
                            if (r == 0) {
                                String speciality = ((String) dataMap.get("Speciality"));
                                String venue = ((String) dataMap.get("CME Venue"));
                                String date1 = ((String) dataMap.get("Selected Date"));
                                String time1 = ((String) dataMap.get("Selected Time"));
                                videoURL = document.getString("Video");
                                List<String> presenters = (List<String>) dataMap.get("CME Presenter");
                                int count = 0;
                                String pres = null;
                                if (presenters != null && !presenters.isEmpty()) {
                                    for (String presenter : presenters) {
                                        if (pres == null) {
                                            pres = presenter + "   ";
                                        } else {
                                            pres = pres + presenter + "   ";
                                        }
                                        Log.d("Presenter", presenter);
                                        count++;
                                    }
                                }
                                presenter3.setText(pres);

                                presenters2.setText(presenters.get(0));
                                if (count > 1) {
                                    addpresent.setVisibility(View.VISIBLE);
                                    addpresent.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            presenter3.setVisibility(View.VISIBLE);
                                            presenters2.setVisibility(View.GONE);
                                            addpresent.setVisibility(View.GONE);
                                        }
                                    });
                                }
                                pdf = ((String) dataMap.get("Cme pdf"));
                                if (pdf == null) {
                                    downloadPdfButton.setVisibility(View.GONE);
                                } else {
                                    downloadPdfButton.setVisibility(View.VISIBLE);
                                    downloadPdfButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            downloadPdf(pdf);
                                        }
                                    });
                                }
                                StyledPlayerView playerView = findViewById(R.id.player_view);
                                player = new ExoPlayer.Builder(CmeDetailsActivity.this).build();
                                playerView.setPlayer(player);

                                MediaItem mediaItem = null;
                                if (videoURL != null) {
                                    mediaItem = MediaItem.fromUri(videoURL);
                                }
                                if (mediaItem != null) {
                                    player.setMediaItem(mediaItem);
                                    player.prepare();
                                    player.setPlayWhenReady(true);
                                }
                                String title = ((String) dataMap.get("CME Title"));
                                String virtuallink = ((String) dataMap.get("Virtual Link"));
                                setSupportActionBar(toolbar);
                                getSupportActionBar().setTitle(title);
                                textView.setText(venue);
                                date.setText(date1);
                                time.setText(time1);
                                Speciality.setText(speciality);
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(CmeDetailsActivity.this, "Error fetching data from Firebase Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });

        long eventStartTime = convertTimeToMillis(field6);
        if ("UPCOMING".equals(field5)) {
            reservebtn.setVisibility(View.VISIBLE);
            livebtn.setVisibility(View.GONE);
            liveendpost.setVisibility(View.GONE);
            textpartforlivecreator.setVisibility(View.GONE);
            textpartforlive.setVisibility(View.GONE);
            pastbtn.setVisibility(View.GONE);
            reservecmebtn.setVisibility(View.GONE);
            reservedcmebtn.setVisibility(View.GONE);
            if ("Online".equals(mode)) {
                attendCmeBtn.setVisibility(View.VISIBLE);
                viewLocationBtn.setVisibility(View.GONE);
            } else if ("Offline".equals(mode)) {
                attendCmeBtn.setVisibility(View.GONE);
                viewLocationBtn.setVisibility(View.VISIBLE);
            }
            if (isCreator) {
                reservecmebtn.setVisibility(View.GONE);
                Schedule.setVisibility(View.VISIBLE);
                Button deleteButton = findViewById(R.id.deletemeet);
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteConfirmationDialog(documentid);
                    }
                });
            } else {
                reservecmebtn.setVisibility(View.VISIBLE);
                Schedule.setVisibility(View.GONE);
                attendCmeBtn.setVisibility(View.GONE);
                Button deleteButton = findViewById(R.id.deletemeet);
                deleteButton.setVisibility(View.GONE);
            }
            String finalField1 = field1;
            reservecmebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isReserved) {
                        Toast.makeText(CmeDetailsActivity.this, "You have already reserved a seat", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("isReserved", true);
                        editor.apply();

                        reservecmebtn.setVisibility(View.GONE);
                        reservedcmebtn.setVisibility(View.VISIBLE);
                        apply(finalField1);
                    }
                }
            });
            reservedcmebtn.setVisibility(View.GONE);
            playerlayout.setVisibility(View.GONE);
            defaultimageofcme.setVisibility(View.VISIBLE);
            textpartforlive.setVisibility(View.INVISIBLE);
            textpartforupcoming.setVisibility(View.VISIBLE);
            relativeboxnotforpast.setVisibility(View.VISIBLE);

        }
        else if ("LIVE".equals(field5)) {
            String currentTime = getCurrentTime();
            Log.d("CurrentTime", currentTime);
            long currenttimeformatted = convertTimeToMillis(currentTime);
            if (isCreator) {
                reservebtn.setVisibility(View.GONE);
                pastbtn.setVisibility(View.GONE);
                playerlayout.setVisibility(View.GONE);
                defaultimageofcme.setVisibility(View.VISIBLE);
                textpartforlive.setVisibility(View.GONE);
                textpartforlivecreator.setVisibility(View.VISIBLE);
                viewLocationBtn.setVisibility(View.GONE);
                attendcmebtn.setVisibility(View.GONE);
                textpartforupcoming.setVisibility(View.INVISIBLE);
                relativeboxnotforpast.setVisibility(View.VISIBLE);
                Log.d("timenew", String.valueOf(eventStartTime));
                livebtn.setVisibility(View.VISIBLE);
                Log.d("CmeDetailsActivity", "Time difference: " + (currenttimeformatted - eventStartTime));
                if ((currenttimeformatted - eventStartTime) >= 10 * 60 * 1000) {
                    liveendpost.setVisibility(View.VISIBLE);
                    livecmebtn.setVisibility(View.GONE);
                    String finalField2 = field1;
                    liveendpost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("CME").document(finalField2);
                            docRef.update("endtime", getCurrentTime(), "Video", null)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            Toast.makeText(CmeDetailsActivity.this, "Successfully Ended", Toast.LENGTH_SHORT).show();
                                            liveendpost.setVisibility(View.GONE);
                                            Log.d("Something went wrong", "Something went wrong");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);
                                        }
                                    });
                        }
                    });
                }
                else {
                    liveendpost.setVisibility(View.GONE);
                    livecmebtn.setVisibility(View.VISIBLE);
                }
            }
            else {
                reservebtn.setVisibility(View.GONE);
                livebtn.setVisibility(View.VISIBLE);
                livecmebtn.setVisibility(View.GONE);
                pastbtn.setVisibility(View.GONE);
                playerlayout.setVisibility(View.GONE);
                defaultimageofcme.setVisibility(View.VISIBLE);
                textpartforlive.setVisibility(View.VISIBLE);
                textpartforlivecreator.setVisibility(View.GONE);
                attendcmebtn.setVisibility(View.VISIBLE);
                textpartforupcoming.setVisibility(View.GONE);
                relativeboxnotforpast.setVisibility(View.VISIBLE);
                liveendpost.setVisibility(View.GONE);
                attendcmebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Query query = db.collection("CME").whereEqualTo("User", current);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String virtualLink = document.getString("Virtual Link");
                                        try {
                                            Uri uri = Uri.parse(virtualLink);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.e("VirtualLinkError", "Error opening virtual link", e);
                                        }
                                    }
                                } else {
                                    Toast.makeText(CmeDetailsActivity.this, "Error fetching data from Firebase Firestore", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                viewLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Query query = db.collection("CME").whereEqualTo("User", current);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String cmePlace = document.getString("CME Place");

                                        try {
                                            Uri uri = Uri.parse(cmePlace);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            if (intent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(CmeDetailsActivity.this, "No app available to handle the link", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.e("CmePlaceError", "Error opening cme place", e);
                                        }
                                    }
                                } else {
                                    Toast.makeText(CmeDetailsActivity.this, "Error fetching data from Firebase Firestore", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }
        else if ("PAST".equals(field5)) {
            reservebtn.setVisibility(View.GONE);
            livebtn.setVisibility(View.GONE);
            pastbtn.setVisibility(View.VISIBLE);
            pastbtn.setVisibility(View.VISIBLE);
            playerlayout.setVisibility(View.VISIBLE);
            defaultimageofcme.setVisibility(View.GONE);
            textpartforlive.setVisibility(View.INVISIBLE);
            textpartforupcoming.setVisibility(View.GONE);
            relativeboxnotforpast.setVisibility(View.GONE);

        }
        else {
            reservebtn.setVisibility(View.GONE);
            livebtn.setVisibility(View.GONE);
        }
        Query query1 = db.collection("users");
        query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        field3 = (String) dataMap.get("Phone Number");
                        boolean areEqualIgnoreCase = current.equalsIgnoreCase(field3);
                        Log.d("Something went wrong", String.valueOf(areEqualIgnoreCase));
                        if ((current != null) && (field3 != null)) {
                            int r = current.compareTo(field3);
                            if (r == 0) {
                                field4 = (String) dataMap.get("Name");
                                Name.setText(field4);

                                fetchOrganiserProfileImage(field3);
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(CmeDetailsActivity.this, "Error fetching data from Firebase Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sharebtnforcme = findViewById(R.id.cmesharebtn);
        sharebtnforcmepast = findViewById(R.id.cmepastshare);

        sharebtnforcme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createlink(current, documentid, cmetitle,cmedescription);
            }
        });
        sharebtnforcmepast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createlink(current, documentid, cmetitle,cmedescription);
            }
        });
        showLoader();
        handleDeepLink();
    }


    private void fetchOrganiserProfileImage(String phoneNumber) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("users").child(phoneNumber).child("profile_image.jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(circularImageView);
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Error fetching profile image: " + exception.getMessage());
        });
    }
    private void showLoader() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false); // Prevent dismiss on touch outside
        progressDialog.show();
    }
    private void hideLoader() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        player.setPlayWhenReady(false);
        player.release();
        player = null;
    }
    private void showDeleteConfirmationDialog(final String documentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCmeItem(documentId);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private void deleteCmeItem(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("CME").document(documentId);
        Log.d("document id 2",documentId);
        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CmeDetailsActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CmeDetailsActivity.this, CmeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CmeDetailsActivity.this, "Error deleting item", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkReservationStatusFirestore(String userId, String eventId) {
        dc.collection("CMEsReserved")
                .whereEqualTo("User", userId)
                .whereEqualTo("documentidapply", eventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                isReserved = true;
                                reservecmebtn.setVisibility(View.GONE);
                                reservedcmebtn.setVisibility(View.VISIBLE);
                                break;
                            }
                        } else {
                            Log.w(TAG, "Error checking reservation status", task.getException());
                        }
                    }
                });
    }
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }
    public static void main(String[] args) {
        String currentTime = getCurrentTime();
        System.out.println("Current Time: " + currentTime);
    }
    private long convertTimeToMillis(String time) {
        if (time == null) {
            return 0;
        }
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        Log.d("timenew",time);
        return hours * 60 * 60 * 1000 + minutes * 60 * 1000;
    }
    public void downloadPdf(String pdfUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfUrl));
        request.setTitle("Downloading PDF");
        request.setDescription("Please wait...");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "your_pdf_filename.pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
        }
        Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
    }
    public void apply(String name) {
        HashMap<String, Object> usermap = new HashMap<>();
        usermap.put("User", current);
        usermap.put("documentidapply",name);
        usermap.put("isReserved",true);
        Log.d(receivedDataCme,"Something went wrong...");
        dc.collection("CMEsReserved")
                .add(usermap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void createlink(String custid, String cmeId, String cmetitle, String cmedescription) {
        Log.e("main", "create link");

        // Attempt to URL encode cmetitle and cmedescription
        String encodedCmeTitle = encode(cmetitle);
        String encodedCmeDescription = encode(cmedescription);

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.mymedicos.in/cmedetails?custid=" + custid + "&cmeId=" + cmeId + "&st=" + encodedCmeTitle + "&sd=" + encodedCmeDescription))
                .setDomainUriPrefix("https://app.mymedicos.in")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.e("main", " Long refer " + dynamicLink.getUri());

        createreferlink(custid, cmeId);
    }
    public void createreferlink(String custid, String cmeId) {
        TextView Type = findViewById(R.id.type);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("CME").document(cmeId);
        Log.e("Error", cmeId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String cmeTitle = documentSnapshot.getString("CME Title");
                String cmeSpeciality = documentSnapshot.getString("Speciality");
                String cmeOrganiser = documentSnapshot.getString("CME Organiser");
                String cmeDate = documentSnapshot.getString("Selected Date");
                String cmeTime = documentSnapshot.getString("Selected Time");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date selectedDateTime = null;
                try {
                    selectedDateTime = sdf.parse(cmeDate + " " + cmeTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date currentDate = new Date();

                SharedPreferences sharedPref = getSharedPreferences("CmeDetails", Context.MODE_PRIVATE);
                String field1 = getIntent().getExtras().getString("documentid");
                if (field1 == null || field1.isEmpty()){
                    field1 = getIntent().getExtras().getString("cmeId");
                    Log.e("coming id",field1);
                }
                String name = getIntent().getExtras().getString("name");
                boolean isCreator = current.equals(name);

                if (selectedDateTime != null && selectedDateTime.after(currentDate)) {

                    Type.setText("UPCOMING");
                    // Handle UPCOMING CME here
                    binding.reservbtn.setVisibility(View.VISIBLE);
                    binding.livebtn.setVisibility(View.GONE);
                    binding.liveendpost.setVisibility(View.GONE);
                    binding.textpartforlivecreator.setVisibility(View.GONE);
                    binding.textpartforlive.setVisibility(View.GONE);
                    binding.pastbtn.setVisibility(View.GONE);
                    reservecmebtn.setVisibility(View.GONE);
                    reservedcmebtn.setVisibility(View.GONE);
                    if (isCreator) {
                        reservecmebtn.setVisibility(View.GONE);
                        binding.schedulemeet.setVisibility(View.VISIBLE);
                        Button deleteButton = findViewById(R.id.deletemeet);
                        deleteButton.setVisibility(View.VISIBLE);
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDeleteConfirmationDialog(documentid);
                            }
                        });
                    } else {
                        reservecmebtn.setVisibility(View.VISIBLE);
                        binding.schedulemeet.setVisibility(View.GONE);
                        binding.attendcmebtn.setVisibility(View.GONE);
                        Button deleteButton = findViewById(R.id.deletemeet);
                        deleteButton.setVisibility(View.GONE);
                    }
                    String finalField1 = field1;
                    reservecmebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isReserved) {
                                Toast.makeText(CmeDetailsActivity.this, "You have already reserved a seat", Toast.LENGTH_SHORT).show();
                            } else {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putBoolean("isReserved", true);
                                editor.apply();

                                reservecmebtn.setVisibility(View.GONE);
                                reservedcmebtn.setVisibility(View.VISIBLE);
                                apply(finalField1);
                            }
                        }
                    });
                    reservedcmebtn.setVisibility(View.GONE);
                    playerlayout.setVisibility(View.GONE);
                    binding.defaultimage.setVisibility(View.VISIBLE);
                    binding.textpartforlive.setVisibility(View.INVISIBLE);
                    binding.textpartforupcoming.setVisibility(View.VISIBLE);
                    binding.relativeboxnotforpast.setVisibility(View.VISIBLE);

                } else if (selectedDateTime != null && currentDate.after(selectedDateTime)) {
                    long diffInMillies = Math.abs(currentDate.getTime() - selectedDateTime.getTime());
                    long diffInHours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    if (diffInHours >= 24) {
                        Type.setText("PAST");
                        binding.reservbtn.setVisibility(View.GONE);
                        binding.livebtn.setVisibility(View.GONE);
                        binding.pastbtn.setVisibility(View.VISIBLE);
                        binding.pastbtn.setVisibility(View.VISIBLE);
                        playerlayout.setVisibility(View.VISIBLE);
                        binding.defaultimage.setVisibility(View.GONE);
                        binding.textpartforlive.setVisibility(View.INVISIBLE);
                        binding.textpartforupcoming.setVisibility(View.GONE);
                        binding.relativeboxnotforpast.setVisibility(View.GONE);

                    }
                    } else {
                    Type.setText("LIVE");
                    String currentTime = getCurrentTime();
                    Log.d("CurrentTime", currentTime);

                    String field6 = getIntent().getExtras().getString("time");
                    long eventStartTime = convertTimeToMillis(field6);

                    long currenttimeformatted = convertTimeToMillis(currentTime);
                    if (isCreator) {
                        binding.reservbtn.setVisibility(View.GONE);
                        binding.pastbtn.setVisibility(View.GONE);
                        playerlayout.setVisibility(View.GONE);
                        binding.defaultimage.setVisibility(View.VISIBLE);
                        binding.textpartforlive.setVisibility(View.GONE);
                        binding.textpartforlivecreator.setVisibility(View.VISIBLE);
                        binding.viewlocationcmebtn.setVisibility(View.VISIBLE);
                        binding.attendcmebtn.setVisibility(View.GONE);
                        binding.textpartforupcoming.setVisibility(View.INVISIBLE);
                        binding.relativeboxnotforpast.setVisibility(View.VISIBLE);
                        Log.d("timenew", String.valueOf(eventStartTime));
                        binding.livebtn.setVisibility(View.VISIBLE);
                        Log.d("CmeDetailsActivity", "Time difference: " + (currenttimeformatted - eventStartTime));
                        if ((currenttimeformatted - eventStartTime) >= 10 * 60 * 1000) {
                            binding.liveendpost.setVisibility(View.VISIBLE);
                            binding.livecmebtn.setVisibility(View.GONE);
                            String finalField2 = field1;
                            binding.liveendpost.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference docRef = db.collection("CME").document(finalField2);
                                    docRef.update("endtime", getCurrentTime(), "Video", null)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                    Toast.makeText(CmeDetailsActivity.this, "Successfully Ended", Toast.LENGTH_SHORT).show();
                                                    binding.liveendpost.setVisibility(View.GONE);
                                                    Log.d("Something went wrong", "Something went wrong");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error updating document", e);
                                                }
                                            });
                                }
                            });
                        } else {
                            binding.liveendpost.setVisibility(View.GONE);
                            binding.livecmebtn.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.reservbtn.setVisibility(View.GONE);
                        binding.livebtn.setVisibility(View.VISIBLE);
                        binding.livecmebtn.setVisibility(View.GONE);
                        binding.pastbtn.setVisibility(View.GONE);
                        playerlayout.setVisibility(View.GONE);
                        binding.defaultimage.setVisibility(View.VISIBLE);
                        binding.textpartforlive.setVisibility(View.VISIBLE);
                        binding.textpartforlive.setVisibility(View.VISIBLE);
                        binding.textpartforlivecreator.setVisibility(View.GONE);
                        binding.attendcmebtn.setVisibility(View.GONE);
                        binding.textpartforupcoming.setVisibility(View.INVISIBLE);
                        binding.relativeboxnotforpast.setVisibility(View.VISIBLE);
                        binding.liveendpost.setVisibility(View.GONE);
                        binding.attendcmebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Query query = db.collection("CME").whereEqualTo("User", current);
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String virtualLink = document.getString("Virtual Link");
                                                try {
                                                    Uri uri = Uri.parse(virtualLink);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                    startActivity(intent);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Log.e("VirtualLinkError", "Error opening virtual link", e);
                                                }
                                            }
                                        } else {
                                            Toast.makeText(CmeDetailsActivity.this, "Error fetching data from Firebase Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                        binding.viewlocationcmebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Query query = db.collection("CME").whereEqualTo("User", current);
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String cmePlace = document.getString("CME Place");

                                                try {
                                                    Uri uri = Uri.parse(cmePlace);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                    if (intent.resolveActivity(getPackageManager()) != null) {
                                                        startActivity(intent);
                                                    } else {
                                                        Toast.makeText(CmeDetailsActivity.this, "No app available to handle the link", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Log.e("CmePlaceError", "Error opening cme place", e);
                                                }
                                            }
                                        } else {
                                            Toast.makeText(CmeDetailsActivity.this, "Error fetching data from Firebase Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }

                String sharelinktext = "Hey there,\n\nJoin us for an insightful CME session on " + cmeTitle + " \uD83E\uDE7A" + " organized by " +
                        cmeOrganiser + " focusing on " + cmeSpeciality + ".\n\n" +
                        "Event Details:\n\n" +
                        "Title : " + cmeTitle + "\n" +
                        "Organizer : " + cmeOrganiser + "\n" +
                        "Specialty : " + cmeSpeciality + "\n" +
                        "Date : " + cmeDate + "\n" +
                        "Time : " + cmeTime + "\n" +
                        "Type : " + Type.getText().toString() + "\n\n" +
                        "Learn more and register: \n" +
                        "https://app.mymedicos.in/?" +
                        "link=http://www.mymedicos.in/cmedetails?cmeId=" + encode(cmeId) +
                        "&st=" + encode(cmeTitle) +
                        "&sd=" + encode(Type.getText().toString()) +
                        "&apn=" + encode(getPackageName()) +
                        "&si=" + encode("https://res.cloudinary.com/dmzp6notl/image/upload/v1709502435/wewewe_ixdzja.png");


                Log.e("Cme Detailed Activity", "Sharelink - " + sharelinktext);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, sharelinktext.toString());
                intent.setType("text/plain");
                startActivity(intent);

            } else {
                Log.e(TAG, "No such document with documentId: " + cmeId);
            }

        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching job details for documentId: " + cmeId, e);
        });
    }


    private void handleDeepLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String cmeId = deepLink.getQueryParameter("cmeId");
                            Intent intent = getIntent();
                            intent.putExtra("cmeId", cmeId);
                            setIntent(intent);
                        }
                    }
                })
                .addOnFailureListener(this, e -> Log.w("DeepLink", "getDynamicLink:onFailure", e));
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}