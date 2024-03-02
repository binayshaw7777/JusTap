# Android Project Model Documentation

Welcome to the documentation for the model classes used in our Android project. This documentation aims to provide a comprehensive overview of each model class, including their purpose, structure, and usage within the project.

## Table of Contents

1. [User](#user)
2. [SocialAccount](#socialaccount)
3. [ScannedUser](#scanneduser)

---

## User

Represents a firebase user in the application.

### Properties

| Property            | Type      | Nullable | Description                                            |
|---------------------|-----------|----------|--------------------------------------------------------|
| `userID`            | `String`  | No       | Unique identifier for the user.                        |
| `name`              | `String`  | No       | Name of the user.                                      |
| `email`             | `String`  | No       | Email address of the user.                             |
| `bio`               | `String?` | Yes      | Biography of the user. Default value is an empty string.|
| `profilePictureURI` | `String?` | Yes      | URI for the profile picture of the user. Default value is an empty string. |
| `profileBannerURI`  | `String?` | Yes      | URI for the profile banner of the user. Default value is an empty string.  |


### Usage

```kotlin
data class User(
    val userID: String,
    val name: String,
    val email: String,
    val bio: String? = "",
    val profilePictureURI: String? = "",
    val profileBannerURI: String? = ""
)
```

### Example

```kotlin
val user = User(
    userID = "123456789",
    name = "John Doe",
    email = "john.doe@example.com",
    bio = "Software Engineer",
    profilePictureURI = "https://example.com/profile_picture.jpg",
    profileBannerURI = "https://example.com/profile_banner.jpg"
)
```

---


# SocialAccount

Represents a social media or contact account.

## Properties

| Property        | Type      | Nullable | Description                                            |
|-----------------|-----------|----------|--------------------------------------------------------|
| `accountID`     | `Int`     | No       | Identifier for the account.                            |
| `accountName`   | `String`  | No       | Name or username associated with the account.          |
| `isVisible`     | `Boolean` | No       | Flag indicating whether the account is visible.        |
| `accountType`   | `String`  | No       | Type of the account (e.g., Facebook, Twitter, Email). |
| `creationDate`  | `Long`    | No       | Timestamp representing the creation date of the account.|
| `lastUpdated`   | `Long`    | No       | Timestamp representing the last update date of the account.|

## Usage

```kotlin
data class SocialAccount(
    var accountID: Int,
    val accountName: String,
    var isVisible: Boolean,
    var accountType: String,
    var creationDate: Long = System.currentTimeMillis(),
    var lastUpdated: Long = System.currentTimeMillis()
)
```


### Example

```kotlin
val socialAccount = SocialAccount(
    accountID = 123456,
    accountName = "example_username",
    isVisible = true,
    accountType = "Twitter",
    creationDate = System.currentTimeMillis(), // Example timestamp for creation date
    lastUpdated = System.currentTimeMillis()   // Example timestamp for last update
)
```

---

## ScannedUser

Represents a scanned user entry in the local history.

### Properties

| Property     | Type      | Nullable | Description                                            |
|--------------|-----------|----------|--------------------------------------------------------|
| `scanID`     | `String`  | No       | Unique identifier for each scan entry.                 |
| `userID`     | `String`  | No       | User ID of the user who performed the scan.            |
| `username`   | `String`  | No       | Username associated with the scanned account.          |
| `updatedAt`  | `Long?`   | Yes      | Timestamp of when the scan entry was last updated.     |
| `profileImage`| `Bitmap?`| Yes      | Profile image associated with the scanned account.     |

### Usage

```kotlin
data class ScannedUser(
    var scanID: String,
    var userID: String,
    var username: String,
    var updatedAt: Long?,
    var profileImage: Bitmap?
)
```

### Example usage:

```kotlin
val scannedAccount = ScannedUser(
    scanID = "unique_scan_id",
    userID = "user_id_123",
    username = "example_username",
    updatedAt = System.currentTimeMillis(),
    profileImage = profileImage
)
```

---