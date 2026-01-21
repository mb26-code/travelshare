# TravelShare

**TravelShare** is a mobile social platform for Android dedicated to sharing travel experiences through photography, centered on visual and geographic discovery. Unlike generalist social networks, TravelShare structures content around the concept of **"Frames"**. A Frame is not just a single image but a collection of photographs, descriptions, and contextual metadata (location, date) displayed on various **"Walls"** (feeds).

This repository contains the source code for the native Android client application.

## 📱 Project Overview

The application is designed to offer two distinct modes of interaction:

1.
**Guest Mode (Anonymous):** Allows users to browse public frames, search for content, and discover travel locations without creating an account.


2.
**Connected Mode (Authenticated):** Unlocks full features including posting "Frames", managing a profile, and interacting with the community.



## ✨ Features

The current version (v1) of the application includes the following features:

### Authentication & Security

*
**Sign Up & Sign In:** Secure user registration and login via the backend API.


* **Secure Storage:** Authentication tokens and user credentials are encrypted using **Jetpack Security (EncryptedSharedPreferences)**.
* **Guest Access:** "Signed Out Mode" for restricted, read-only access.

### Feed & Discovery

*
**Feed Wall:** A vertical scrollable feed displaying Frames from the community.


* **Photo Carousel:** Each Frame features a horizontal carousel to swipe through multiple travel photos.
* **Search:** Dedicated screen to search for Frames by keywords.

### "Hang a Frame" (Posting)

* **Multi-Photo Upload:** Users can select up to 8 photos from their device gallery.
* **Smart Location Extraction:** The app automatically extracts GPS coordinates from image EXIF data.
* **Manual Map Picker:** Integrated **Google Maps** interface to manually pin locations for photos lacking GPS data.
* **Image Processing:** Automatic background compression, resizing, and rotation handling (using Glide) to ensure fast uploads and correct orientation.

### Profile Management

* **User Profile:** Display of user information (Name, Email).
* **Session Management:** Secure logout functionality.

## 🛠️ Tech Stack

The application is built using native Android technologies and follows modern development standards.

* **Language:** Java
* **Minimum SDK:** 24 (Android 7.0)
* **Architecture:** MVC / MVVM pattern separating Activities, Fragments, and Data Models.

### Key Libraries & Dependencies

* **Networking:** [Retrofit 2](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) for REST API communication.
* **Image Loading:** [Glide](https://github.com/bumptech/glide) for efficient image caching, display, and background processing.
* **Maps:** [Google Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk) for the location picker interface.
* **Security:** `androidx.security:security-crypto` for secure local data storage.
* **UI Components:** Material Design components, RecyclerView, ViewPager2.

## 🔗 Backend API

This Android client relies on a dedicated backend service for data persistence, authentication, and media storage. The backend is built with **Node.js**, **Express**, and **MySQL**.

You can find the backend source code and documentation here:
👉 **[TravelShare API Repository](https://github.com/mb26-code/travelshare-api)**

The API documentation is also available locally in `TravelShare_API_documentation.md`.

## ⚙️ Setup & Configuration

To build and run this project locally, follow these steps:

### 1. Prerequisites

* Android Studio Ladybug (or newer).
* JDK 17 or higher.
* A Google Cloud Project with the **Maps SDK for Android** enabled.

### 2. Clone the Repository

```bash
git clone https://github.com/mb26-code/travelshare.git

```

### 3. API Key Configuration

This project requires a Google Maps API Key to function correctly. For security reasons, the key is not committed to version control.

Create a file named `local.properties` in the root directory of the project (if it does not exist) and add your API key:

```properties
sdk.dir=/path/to/your/android/sdk
MAPS_API_KEY=AIzaSy_YourSecretAPIKeyHere

```

The `build.gradle.kts` file is configured to inject this key into the Android Manifest during the build process.

### 4. Backend Connection

By default, the app tries to connect to the production API URL. To test with a local server, modify `BASE_URL` in `dev.mb_labs.travelshare.api.APIClient`:

```java
// For Android Emulator access to localhost
private static final String BASE_URL = "http://10.0.2.2:3001/";

```

## 👤 Author

**Mehdi BAKHTAR**
*Developer and Master 2 Computer Science Student*
*University of Montpellier, France*

---

*This project was developed as part of the Mobile Programming (HAI926I) course.*
