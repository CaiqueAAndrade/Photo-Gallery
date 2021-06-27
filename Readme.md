
# Photo Gallery app

Android application that shows a gallery of images where the user can see the latest images or search for specific images.

## Installation
Clone this repository and import into **Android Studio**
```bash
git clone https://github.com/CaiqueAAndrade/Photo-Gallery.git
```

## Development

### Architecture:

Project developed using MVVM architecture with DataBinding from Google Android JetPack: 

Create `app/build.gradle.app` with the following info:
```gradle
buildFeatures {
    dataBinding true
}

implementation "androidx.lifecycle:lifecycle-extensions:$rootProject.extensionsVersion"
implementation "androidx.lifecycle:lifecycle-common-java8:$rootProject.lifecycleVersion"
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$rootProject.lifecycleVersion"
```
Android Lifecycle and ViewModel version under `build.gradle` file:
- `lifecycleVersion = '2.3.1'`
- `extensionsVersion = '2.2.0'`

Dependency injection with Koin:
```gradle
implementation "org.koin:koin-android-viewmodel:$rootProject.koinVersion"
```

- `koinVersion = ‘2.0.1’`


### Image Loading
Using Glide to load image from Url and store in cache:

```gradle
// Glide
implementation "com.github.bumptech.glide:glide:$rootProject.glideVersion"
annotationProcessor "com.github.bumptech.glide:compiler:$rootProject.glideVersion"
```

- `glideVersion = '4.12.0'`

## Data Repository:
Using Retrofit2 with coroutines to fetch data from API:

```gradle
// Retrofit
implementation "com.squareup.retrofit2:retrofit:$rootProject.retrofit2Version"
implementation "com.squareup.retrofit2:converter-moshi:$rootProject.retrofit2Version"
implementation "com.squareup.okhttp3:logging-interceptor:$rootProject.okhttp3Version"
implementation "com.squareup.okhttp3:okhttp-urlconnection:$rootProject.okhttp3Version"


```

- `retrofit2Version = '2.9.0'`

## Coding style
The project using a variety of features to improve code quality and make it easy to maintain, as with:
 - Databinding to fetch layout behavior directly from ViewModel.
 - CustomView to encapsulate view behavior and reuse if necessary in other parts of the application.
 - Custom retrofit call that returns results as Success, Failure, and Network Failure to intercept API calls in an easy way.
 - Unit testing with mockito to test ViewModel values and behavior.


## Notice

My free plan in Unsplash can only do 50 API calls per hour, so if it runs out you can change to my mock API. 

 - Mock: `https://private-00cd68-caique1.apiary-mock.com/`

You only need to go to file `RetrofitConfig.kt` and change the commented line with the `.baseUrl("https://private-00cd68-caique1.apiary-mock.com/")`.

