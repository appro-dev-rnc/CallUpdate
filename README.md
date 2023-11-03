# CallUpdate
Call update is an android applcation designed natively using Java language on Android Studio.
The core functionality of this app is to show a custom notification and a toast message set by user, whenever a call is received on the device.
Also, If the call is missed and not answered by the user, the App automatically sends a text message to the caller.

HOW TO RUN THE APP
* Download the zip code from the above link, Extract it and run in Android Studio.
* Allow the permissions for the best functionality of the app.
*Set a custom message and turn on the alert switch and click on save button.
* Now whenever the app is active, the app will show this custom message as a toast and as a Notification if a call is received.
* The app will then send a message to the caller if the call was missed.

PERMISSIONS REQUIRED
* READ_PHONE_STATE: This permission is required to read the phone state and detect the changes .
* POST_NOTIFICATIONS: This permission is required to show notifications to the user when a call is received.
* SEND_SMS: This permission is required to send the text message to the caller if the call is missed.
