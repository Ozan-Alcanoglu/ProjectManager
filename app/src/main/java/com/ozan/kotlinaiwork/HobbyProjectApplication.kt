package com.ozan.kotlinaiwork

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HobbyProjectApplication : Application()

// AndroidManifest.xml'e bu sınıfı eklemeyi unutmayın:
// <application
//     android:name=".HobbyProjectApplication"
//     ...
// >
