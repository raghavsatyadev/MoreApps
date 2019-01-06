# MoreApps
With More Apps library you can showcase your other apps in a beautiful way.

**Current version:**  <a href='https://bintray.com/raghavsatyadev/Maven/MoreApps/_latestVersion'><img src='https://api.bintray.com/packages/raghavsatyadev/Maven/MoreApps/images/download.svg'></a>

# Setup
To use this library your minSdkVersion must be >= 19. Library is made of AndroidX components,so you have to upgrade your project to AndroidX currently.

In the build.gradle of your app module add:

```gradle
dependencies {
    implementation 'com.rocky.moreapps:moreapps:x.x.x'
}
```

You also have to give Java 8 support for this library

```gradle
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

# Example

**Java**

```java
new MoreAppsDialog.Builder(this, CoreApp.JSON_FILE_URL)
        .buildAndShow(new MoreAppsDialogListener() {
            @Override
            public void onClose() {
                // on dialog close
            }

            @Override
            public void onAppClicked(MoreAppsModel appsModel) {
                // on item click
            }
        });
```

**Customization**

```java

new MoreAppsDialog.Builder(this, CoreApp.JSON_FILE_URL)
       .removeCurrentApplication("com.appdroidtechnologies.whatscut") // to remove current application from the list give package name here
       .dialogTitle("More Apps") // custom dialog title
       .dialogLayout(R.layout.more_apps_view) // custom dialog layout, read more instructions in it's javadoc
       .dialogRowLayout(R.layout.row_more_apps) // custom list item layout, read more instructions in it's javadoc
       .openAppsInPlayStore(true) // on clicking the item, should it open in the play store
       .font(R.font.sans_bold) // custom font
       .themeColor(Color.parseColor("#AAF44336")) // custom theme color, read more in javadoc
       .rowTitleColor(Color.parseColor("#000000")) // custom list item title color
       .rowDescriptionColor(Color.parseColor("#888888")) // custom list item description color
       .buildAndShow(new MoreAppsDialogListener() {
           @Override
           public void onClose() {
               // on dialog close
           }

           @Override
           public void onAppClicked(MoreAppsModel appsModel) {
               // on item click
           }
       });

```

**Json File Format**
```json
[
  {
    "image_link": "image link",
    "name": "app name",
    "rating": 4.2,
    "play_store_link": "play store link",
    "package_name": "package name",
    "description": "description"
  },
  {
    "image_link": "https://lh3.googleusercontent.com/EpDjP8egmkfhnGHoo4kII_-GInJRUE11kBg8iWAzvz5NNa_1p0VALeQbh307wFalZaDl=s180-rw",
    "name": "Useful Tools For TikTok",
    "rating": 5,
    "play_store_link": "https://play.google.com/store/apps/details?id=com.appdroidtechno.tools.tictoc",
    "package_name": "com.appdroidtechno.tools.tictoc",
    "description": "Every tool/feature you need to manage TikTok (formally Known as Musically) app."
  }
]
```

File is available at : https://github.com/raghavsatyadev/MoreApps/blob/master/Resources/more_apps.json

# Preview

[![screen](https://raw.githubusercontent.com/raghavsatyadev/MoreApps/master/Resources/Option-1.png)](https://github.com/raghavsatyadev/MoreApps)

This library uses 

- **SimpleRatingBar** :  https://github.com/FlyingPumba/SimpleRatingBar
- **Retrofit** : https://github.com/square/retrofit
- **Glide** : https://github.com/bumptech/glide

<div>Icons made by <a href="https://www.flaticon.com/authors/smashicons" title="Smashicons">Smashicons</a> from <a href="https://www.flaticon.com/" 			    title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" 			    title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>


# Licence
Copyright 2018 Raghav Satyadev

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
