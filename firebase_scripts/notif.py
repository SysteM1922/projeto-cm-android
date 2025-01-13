import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from firebase_admin import messaging
import datetime

# Firebase initialization
cred = credentials.Certificate("serviceAccountKey.json")  # Replace with your service account key
try:
    firebase_admin.initialize_app(cred)
    print("Firebase app initialized successfully!")
except Exception as e:
    print(f"Error initializing Firebase app: {e}")
    exit()

db = firestore.client()

def send_test_notification(trip_id, day_of_week, title, body, data=None, user_id_to_send = None):
    """Sends a test notification to a specific user based on preferences."""
    try:
        users_ref = db.collection("users")
        docs = users_ref.stream()

        messages = []
        for doc in docs:
            user_data = doc.to_dict()
            user_id = doc.id
            if user_id_to_send is not None and user_id != user_id_to_send:
                continue
            preferences = user_data.get("preferences", [])
            print(f"Checking user {user_id} preferences: {preferences}")
            for pref in preferences:
                if (pref.get("trip_id") == trip_id and day_of_week in pref.get("days", [])):
                    print(f"Found matching preference for user {user_id}")
                    topic = f"{trip_id}-{day_of_week}"
                    message = messaging.Message(
                        topic=topic,
                        notification=messaging.Notification(
                            title=title,
                            body=body,
                        ),
                        data=data or {},
                    )
                    messages.append(message)
                    print(f"Sending notification to user {user_id} with topic {topic}")
        if messages:
            batch_response = messaging.send_all(messages)
            print(f"Sent {len(messages)} messages: {batch_response}")
        else:
            print(f"No matching preferences found for trip {trip_id} on {day_of_week}")

    except Exception as e:
        print(f"Error sending notifications: {e}")

def send_general_test_notification(title, body, data=None):
    """Sends a test notification to the general_notifications topic."""
    try:
        topic = "general_notifications"
        message = messaging.Message(
            topic=topic,
            notification=messaging.Notification(
                title=title,
                body=body,
            ),
            data=data or {},
        )
        response = messaging.send(message)
        print(f"Sent general notification to topic '{topic}': {response}")
    except Exception as e:
        print(f"Error sending general notification: {e}")


# Example Usage (replace with your actual data)
trip_id_to_test = "L5AW5"  
day_of_week_to_test = "Mon"
title_to_test = "Test Notification"
body_to_test = "This is a test notification from the Python script."

# Send to a specific user
user_id_to_send_to = "Qj7SarNgnxfN6hukqjkbYl2i0so2"
send_test_notification(trip_id_to_test, day_of_week_to_test, title_to_test, body_to_test, {"type": "test"}, user_id_to_send_to)