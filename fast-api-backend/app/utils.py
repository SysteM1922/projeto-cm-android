# app/utils.py
import datetime
from geopy.distance import geodesic
from firebase_admin import messaging
from .models import PushNotification
import firebase_admin
from firebase_admin import credentials, messaging
import os
from dotenv import load_dotenv

def is_within_distance(lat1: float, lon1: float, lat2: float, lon2: float, distance_m: float = 100) -> bool:
    """
    Check if two coordinates are within a specified distance in meters.
    """
    point1 = (lat1, lon1)
    point2 = (lat2, lon2)
    distance = geodesic(point1, point2).meters
    return distance <= distance_m

def initialize_firebase():
    """
    Initializes the Firebase Admin SDK if not already initialized.
    """
    if not firebase_admin._apps:
        cred_path = os.getenv("FIREBASE_CREDENTIALS")
        if not cred_path:
            print("FIREBASE_CREDENTIALS environment variable not set.")
            raise ValueError("FIREBASE_CREDENTIALS environment variable not set.")

        try:
            cred = credentials.Certificate(cred_path)
            firebase_admin.initialize_app(cred)
            print("Firebase app initialized successfully!")
        except Exception as e:
            print(f"Failed to initialize Firebase app: {e}")
            raise e

def send_push_notification(topic: str, notification: 'PushNotification', data: dict = None):
    """
    Sends a push notification to a specific Firebase topic.

    Args:
        topic (str): The Firebase topic to which the message will be sent.
        notification (PushNotification): The notification content.
        data (dict, optional): Additional data payload.

    Returns:
        str: Response from FCM if successful, None otherwise.
    """
    try:
        # Initialize Firebase if not already done
        initialize_firebase()

        # Construct the message
        message = messaging.Message(
            notification=messaging.Notification(
                title=notification.title,
                body=notification.message,
            ),
            topic=topic,
            data={k: str(v) for k, v in data.items()} if data else None,
            android=messaging.AndroidConfig(
                ttl=datetime.timedelta(seconds=300),  # Time-to-live set to 5 minutes
                priority='high',
            ),
            apns=messaging.APNSConfig(
                headers={
                    'apns-expiration': str(int(datetime.datetime.now().timestamp()) + 300)
                },
                payload=messaging.APNSPayload(
                    aps=messaging.Aps(
                        sound="default",
                        badge=1,
                    ),
                ),
            ),
        )

        # Send the message
        response = messaging.send(message)
        print(f"Successfully sent message to topic '{topic}': {response}")
        return response

    except Exception as e:
        print(f"Error sending message to topic '{topic}': {e}")
        return None