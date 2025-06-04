package com.ozan.kotlintodoproject

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ProjectApplication : Application()

// AndroidManifest.xml'e bu sınıfı eklemeyi unutmayın:
// <application
//     android:name=".HobbyProjectApplication"
//     ...
// >
