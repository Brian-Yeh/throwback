Throwback (WIP)
===========================================
An Android app written in Kotlin that utilizes Spotify’s Android SDK to stream popular songs from a user-defined era

<h2>Tech Stack</h2>

- Architecture
  - MVVM - Using ViewModels and LiveData
  - Repository pattern
- [Spotify Android SDK](https://github.com/spotify/android-sdk)
- [Jetpack Navigation](https://developer.android.com/guide/navigation) - Used to switch between fragments in this single-activity app
- [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) - Asynchronous code
- [Hilt](https://dagger.dev/hilt/) - Dependency Injection
- [Retrofit](https://square.github.io/retrofit/) + [OKHttp](https://square.github.io/okhttp/) + [Glide](https://bumptech.github.io/glide/) - Fetching metadata and album artwork for the Queue with the RESTful [Spotify Web API](https://developer.spotify.com/documentation/web-api/)
- [Room](https://developer.android.com/jetpack/androidx/releases/room) + [Flow](https://developer.android.com/kotlin/flow) - Loading a prepackaged database consisting of the top Billboard tracks for each year
- [Timber](https://github.com/JakeWharton/timber) - Logging

