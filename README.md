# MoreApps
Showcase your other apps in a beautiful way. This library also includes force updater.

**Current version:**  <a href='https://bintray.com/raghavsatyadev/Maven/MoreApps/_latestVersion'><img src='https://api.bintray.com/packages/raghavsatyadev/Maven/MoreApps/images/download.svg'></a>

# Setup
To use this library your **minSdkVersion** must be >= 19. This library supports **AndroidX**.

In the build.gradle of your app module add:

```gradle
dependencies {
    implementation 'com.rocky.moreapps:moreapps:x.x.x'
}
```

# Example (More Apps Dialog)

**Basic**

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
    .customOkHttpClient(okHttpClient) // provide custom OkHttpClient
    .removeApplicationFromList("com.appdroidtechnologies.whatscut") // to remove an application from the list, give package name here
    .dialogTitle(R.string.more_apps) // custom dialog title
    .dialogLayout(R.layout.more_apps_view) // custom dialog layout, read more instructions in it's javadoc
    .dialogRowLayout(R.layout.row_more_apps) // custom list item layout, read more instructions in it's javadoc
    .openAppsInPlayStore(true) // on clicking the item, should it open in the play store
    .font(R.font.sans_bold) // custom font
    .themeColor(Color.parseColor("#AAF44336")) // custom theme color, read more in javadoc default primary color
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

# Example (Force Updater)

**Application Class**

```java
MoreAppsDialog moreAppsDialog = new MoreAppsDialog.Builder(this, JSON_FILE_URL).build(); //calling this method in application class would be recommended
```

**Launcher Activity**

```java
if (ForceUpdater.shouldShowUpdateDialogs(getContext()))
    ForceUpdater.showUpdateDialogs(getContext(), new UpdateDialogListener() {
        @Override
        public void onClose() {
        }
    });
```

**Json File Format**

***NOTE*** : redirect_details, soft_update_details, hard_update_details are only required for Force Updater.

```json
[
  {
    "image_link": "https://lh3.googleusercontent.com/e9P4W-uQW8kdMw6yMX6NSHiy9KL7gvMPUugh4r2lOSYECTFuvZhl_rqKAlma43qA4uM8=s180-rw",
    "name": "WhatsCut - Best Video Cut & Share App for WhatsApp",
    "rating": 4.2,
    "app_link": "https://play.google.com/store/apps/details?id=com.appdroidtechnologies.whatscut",
    "package_name": "com.appdroidtechnologies.whatscut",
    "description": "Easily cut and share large videos on WhatsApp.",
    "min_version": 5,
    "current_version": 6,
    "redirect_details": {
      "enable": true,
      "hard_redirect": false,
      "dialog_title": "Redirect Notice",
      "dialog_message": "We have uploaded our app to new location",
      "positive_button": "Redirect",
      "negative_button": "Cancel",
      "app_link": "https://play.google.com/store/apps/details?id=com.appdroidtechnologies.whatscut"
    },
    "soft_update_details": {
      "enable": true,
      "dialog_title": "Update Required!",
      "dialog_message": "Soft Update is required",
      "positive_button": "Update",
      "negative_button": "Cancel"
    },
    "hard_update_details": {
      "enable": true,
      "dialog_title": "Update Required!",
      "dialog_message": "Hard Update is required",
      "positive_button": "Update"
    }
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
