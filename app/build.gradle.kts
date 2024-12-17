plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.medical.my_medicos"
    compileSdk = 34

    buildFeatures {
        viewBinding
    }
repositories{
    mavenCentral()
}
    defaultConfig {
        applicationId = "com.medical.my_medicos"
        minSdk = 24
        targetSdk = 34
        // Enhanced design and content viewing in version 89
        //Built new interface in home screen so that techer can sell their courses in version 90
        //implemented news feature in version 91
        versionCode = 93
        versionName = "4.32.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
    }
}



dependencies {
    implementation ("io.github.h07000223:flycoTabLayout:3.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.android.gms:play-services-auth:21.1.0")
    implementation ("com.google.android.gms:play-services-auth-api-phone:18.0.2")
    implementation ("com.google.firebase:firebase-auth:22.3.1")
   // implementation ("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")

//    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.firebase:firebase-sessions:1.2.4")
    implementation("androidx.media3:media3-common:1.3.1")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("androidx.activity:activity:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.hbb20:ccp:2.7.3")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    //....Image Zoom
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")
    ///........
    implementation("com.intuit.ssp:ssp-android:1.1.0")
//    implementation("com.google.firebase:firebase-database")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.20")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.firebaseui:firebase-ui-database:8.0.2")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")
    // Additional dependencies from the first code block
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-firestore:24.11.1")
    implementation ("com.squareup.picasso:picasso:2.71828")
    //....
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")
    /* For Slider - End */
// Chart and graph library
    implementation ("com.github.blackfizz:eazegraph:1.2.2@aar")
    implementation ("com.nineoldandroids:library:2.4.0")
    /* For Rounded Image View */
    implementation ("com.makeramen:roundedimageview:2.3.0")
    /* For Material Search Bar */
    implementation ("com.github.mancj:MaterialSearchBar:0.8.5")
    implementation ("me.relex:circleindicator:2.1.6")
    implementation ("org.imaginativeworld.whynotimagecarousel:whynotimagecarousel:2.1.0")
    /* For Slider - End */
    implementation ("com.android.volley:volley:1.2.1")
    //..
    implementation ("com.airbnb.android:lottie:6.2.0")
    //..
    implementation ("com.github.hishd:TinyCart:1.0.1")
    implementation ("com.github.delight-im:Android-AdvancedWebView:v3.2.1")
    //...Bottom navigation bar
    implementation("com.exyte:animated-navigation-bar:1.0.0")
    //...Shimmer....
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    //.....
    implementation ("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1") // Check for the latest version

    implementation ("com.google.android.exoplayer:exoplayer-ui:2.19.1")
    //.. AdMobs....
    implementation ("com.google.android.gms:play-services-ads:23.0.0")
    //...Swipe in....
    implementation ("com.github.esafirm:android-stubutton:1.1.0")
    implementation ("com.google.android.recaptcha:recaptcha:18.4.0")
    //.. Tap Target...
    implementation ("uk.co.samuelwall:material-tap-target-prompt:3.3.2")
    //.....Download Activity
    implementation ("com.itextpdf:itext7-core:7.1.16")
    //....In app Update
    implementation ("com.google.android.play:app-update:2.1.0")
    implementation("com.razorpay:checkout:1.6.40")
    //.......SMS Retriver.......
    implementation ("com.google.android.gms:play-services-auth:21.1.0")
    implementation ("com.google.android.gms:play-services-auth-api-phone:18.0.2")
    //......Deep Links...........
    implementation("com.google.firebase:firebase-dynamic-links-ktx:21.2.0")
    //.....Salary Range provider..............
    implementation ("com.google.android.material:material:1.11.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.ome450901:SimpleRatingBar:1.5.1")


    //.....App Check.........................
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    //......
    implementation ("jp.wasabeef:glide-transformations:4.3.0")
    //.....Reader............
  //  implementation ("com.github.barteksc:android-pdf-viewer:2.8.2")
    //....Retrofit............
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    //...Shimmer.............
    implementation ("com.facebook.shimmer:shimmer:0.1.0@aar")
    //...Pie.................
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //...CountryCode........
    implementation("com.github.barteksc:PdfiumAndroid:pdfium-android-1.9.0")
    implementation("com.hbb20:ccp:2.7.1")
}
