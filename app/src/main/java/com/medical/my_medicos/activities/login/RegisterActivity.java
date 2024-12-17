package com.medical.my_medicos.activities.login;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;
import com.medical.my_medicos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    TextView register;
    private Spinner prefixSpinner, locationSpinner, interestSpinner,interestSpinner2;
    private ArrayAdapter<CharSequence> locationAdapter;
    private ArrayAdapter<CharSequence> interestAdapter;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    HashMap<String, Object> userMap = new HashMap<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String selectedCountryCode ;
    EditText email, fullName, phoneNumber, password;
    TextView loginreg;
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        CountryCodePicker countryCodePicker = findViewById(R.id.countryCodePicker);

        // Optionally set a default country code if needed
        countryCodePicker.setDefaultCountryUsingNameCode("IN");
        selectedCountryCode ="+"+ countryCodePicker.getSelectedCountryCode();

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

        ImageView backArrow = findViewById(R.id.backbtn);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        TextView loginreg = findViewById(R.id.loginreg);
        loginreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        register = findViewById(R.id.register);
        prefixSpinner = findViewById(R.id.prefixSpinner);
        locationSpinner = findViewById(R.id.city);
        interestSpinner = findViewById(R.id.interest1);
        interestSpinner2 = findViewById(R.id.interest2);
        email = findViewById(R.id.emailedit);
        fullName = findViewById(R.id.fullnameedit);
        phoneNumber = findViewById(R.id.phonenumberededit);
        password = findViewById(R.id.passwordedit);

        // Retrieve the phone number from the intent and set it to the phoneNumber EditText
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("phone")) {
            String phone = intent.getStringExtra("phone");
            phoneNumber.setText(phone);
            phoneNumber.setEnabled(false); // Disable the field programmatically
        }

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        userMap.put("MedCoins", 50);
        userMap.put("Streak", 0);
        userMap.put("QuizToday", "default");

        setupSpinners();
        register();

        ImageView lockIcon = findViewById(R.id.lock_icon);
        lockIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TooltipCompat.setTooltipText(lockIcon, "The number is locked");
                Toast.makeText(RegisterActivity.this, "The number is locked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinners() {
        // Prefix Spinner
        ArrayAdapter<CharSequence> prefixAdapter = ArrayAdapter.createFromResource(this,
                R.array.prefix_options, android.R.layout.simple_spinner_item);
        prefixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefixSpinner.setAdapter(prefixAdapter);

        prefixSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedPrefix = adapterView.getItemAtPosition(position).toString();
                userMap.put("prefix", selectedPrefix);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        // Location Spinner
        locationAdapter = ArrayAdapter.createFromResource(this,
                R.array.indian_cities, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedLocation = adapterView.getItemAtPosition(position).toString();
                userMap.put("location", selectedLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
// Initialize Interest Spinners
        interestAdapter = ArrayAdapter.createFromResource(this,
                R.array.speciality, android.R.layout.simple_spinner_item);
        interestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interestSpinner.setAdapter(interestAdapter);
        interestSpinner2.setAdapter(interestAdapter);

// List of all items (to modify later for Spinner2)
        final String[] allItems = getResources().getStringArray(R.array.speciality);

// Handle the first interest spinner selection
        interestSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedInterest = adapterView.getItemAtPosition(position).toString();

                // Skip filtering if "Select Speciality" is selected
                if (!selectedInterest.equals("Select Speciality")) {
                    userMap.put("interest", selectedInterest); // Store in userMap

                    // Create a filtered list of items excluding the selected item
                    List<String> filteredItems = new ArrayList<>(Arrays.asList(allItems));
                    filteredItems.remove(selectedInterest); // Remove selected item from Spinner1

                    // Update Spinner2 with the filtered list
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_spinner_item, filteredItems);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    interestSpinner2.setAdapter(adapter2);
                } else {
                    // If "Select Speciality" is selected, reset Spinner2 to show all items
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_spinner_item, allItems);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    interestSpinner2.setAdapter(adapter2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

// Handle the second interest spinner selection
        interestSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedInterest2 = adapterView.getItemAtPosition(position).toString();
                userMap.put("interest2", selectedInterest2); // Store in userMap
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

    }

    private void register() {
        register.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                String mail = Objects.requireNonNull(email.getText()).toString().trim();
                String name = Objects.requireNonNull(fullName.getText()).toString().trim();
                String enteredPhone = Objects.requireNonNull(phoneNumber.getText()).toString().trim();
                String pass = Objects.requireNonNull(password.getText()).toString().trim();
                String phoneNo;

                if (prefixSpinner.getSelectedItemPosition() == 0){
                    Toast.makeText(RegisterActivity.this, "Please select a Designation", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (locationSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(RegisterActivity.this, "Please select a location", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if interest spinner 1 is empty
                if (interestSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(RegisterActivity.this, "Please select your first interest", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if interest spinner 2 is empty
                if (interestSpinner2.getSelectedItemPosition() == 0) {
                    Toast.makeText(RegisterActivity.this, "Please select your second interest", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!enteredPhone.startsWith("+91")) {
                    phoneNo = selectedCountryCode + enteredPhone;
                } else {
                    phoneNo = selectedCountryCode+enteredPhone;
                }

                if (TextUtils.isEmpty(mail)) {
                    email.setError("Email Required");
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    fullName.setError("Full Name Required");
                    return;
                }
                if (TextUtils.isEmpty(phoneNo)) {
                    phoneNumber.setError("Phone Number Required");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    password.setError("Password Required");
                    return;
                }

                String emailValidity = isValidEmail(mail);
                if (emailValidity != null) {
                    email.setError(emailValidity);
                    Toast.makeText(RegisterActivity.this, emailValidity, Toast.LENGTH_SHORT).show();
                    return;
                }

                String phoneValidity = isValidPhoneNumber(phoneNo);
                if (phoneValidity != null) {
                    phoneNumber.setError(phoneValidity);
                    Toast.makeText(RegisterActivity.this, phoneValidity, Toast.LENGTH_SHORT).show();
                    return;
                }

                String passwordValidity = isPasswordValid(pass);
                if (passwordValidity != null) {
                    password.setError(passwordValidity);
                    Toast.makeText(RegisterActivity.this, passwordValidity, Toast.LENGTH_LONG).show();
                    return;
                }

                progressDialog.setMessage("Registering");
                progressDialog.show();

                // Get FCM token
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String fcmToken = task.getResult();
                                mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        createUserInFirestore(mail, name, phoneNo,
                                                                Objects.requireNonNull(userMap.get("location")).toString(),
                                                                Objects.requireNonNull(userMap.get("interest")).toString(),
                                                                Objects.requireNonNull(userMap.get("QuizToday")).toString(),
                                                                Integer.parseInt(Objects.requireNonNull(userMap.get("MedCoins")).toString()),
                                                                Integer.parseInt(Objects.requireNonNull(userMap.get("Streak")).toString()), fcmToken);

                                                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                                        startActivity(i);
                                                        finish();

                                                        Toast.makeText(getApplicationContext(), "Registration Successful, please verify your Email ID", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    } else {
                                                        // Here you handle the specific error scenarios
                                                        String errorMessage = task.getException().getMessage();
                                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                            // Check if it's an email or phone number collision
                                                            FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) task.getException();
                                                            if (exception.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                                                Toast.makeText(getApplicationContext(), "This email is already registered. Please use a different email.", Toast.LENGTH_SHORT).show();
                                                            } else if (exception.getErrorCode().equals("ERROR_PHONE_NUMBER_ALREADY_EXISTS")) {
                                                                Toast.makeText(getApplicationContext(), "This phone number is already registered. Please use a different phone number.", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            // Generic error handling
                                                            Toast.makeText(getApplicationContext(), "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                                        }
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });
                                        } else {
                                            // Handle registration task failure
                                            String errorMessage = task.getException().getMessage();
                                            Toast.makeText(getApplicationContext(), "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }




                                    }
                                });
                            } else {
                                Log.e(TAG, "Failed to get FCM token: ", task.getException());
                                Toast.makeText(RegisterActivity.this, "Failed to get FCM token", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }


    private void createUserInFirestore(String mail, String name, String phoneNo, String location, String interest, String quiztoday,int medcoins,int streak,String fcmtoken) {
        Map<String, Object> user = new HashMap<>();
        user.put("Email ID", mail);
        user.put("Name", name);
        user.put("Phone Number", phoneNo);
        user.put("Location", location);
        user.put("QuizToday", quiztoday);
        user.put("MedCoins", medcoins);
        user.put("Streak", streak);
        user.put("Interest", interest);
        user.put("Interest2", interest);
        user.put("Prefix", userMap.get("prefix").toString());
        user.put("MCN verified", false);
        user.put("FCM Token",fcmtoken);
        user.put("Updatedprofile",true);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String documentId = documentReference.getId();
                        documentReference.update("DocID", documentId)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Log success if needed
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Log failure if needed
                                    }
                                });
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Registration Successful, please verify your Email ID", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else {
//                                    Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Error 404", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static String isPasswordValid(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        } else if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            return "Password must contain at least one uppercase letter";
        } else if (!Pattern.compile("[a-z]").matcher(password).find()) {
            return "Password must contain at least one lowercase letter";
        } else if (!Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
            return "Password must contain at least one special character";
        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            return "Password must contain at least one digit";
        } else {
            return null; // Password is valid
        }
    }


    @Nullable
    @Contract("null -> !null")
    private String isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "Email is required";
        } else if (!email.toLowerCase().endsWith("@gmail.com")) {
            return "Email must end with @gmail.com";
        } else {
            return null; // Email is valid
        }
    }

    @Nullable
    private String isValidPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return "Phone number is required";
        } else if (!phoneNumber.matches("\\+?[0-9]+")) {
            return "Phone number must contain only digits";
        } else {
            return null; // Phone number is valid
        }
    }
}