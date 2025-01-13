import firebase_admin
from firebase_admin import credentials
from firebase_admin import messaging
import datetime


def send_fcm_message_with_service_account(device_token, title, body, data=None):
    """
    Sends an FCM notification to a specific device using its device token.

    Args:
        device_token (str): The FCM device token.
        title (str): Notification title.
        body (str): Notification body.
        data (dict, optional): Additional data payload.

    Returns:
        str: Response from FCM or None in case of an error.
    """
    try:
        # Initialize Firebase app if not already initialized
        if not firebase_admin._apps:
            cred = credentials.Certificate("serviceAccountKey.json")
            firebase_admin.initialize_app(cred)
            print("Firebase app initialized successfully!")

        # Construct the message for the specific device
        message = messaging.Message(
            topic=topic,
            notification=messaging.Notification(
                title=title,
                body=body,
            ),
            data={k: str(v) for k, v in data.items()} if data else None,
            android=messaging.AndroidConfig(
                ttl=300  # Set TTL (time-to-live) to 300 seconds (5 minutes)
            ),
        )

        # Send the message
        response = messaging.send(message)
        print(f"Successfully sent message to device: {response}")
        return response

    except Exception as e:
        print(f"Error sending message: {e}")
        return None


# Example usage
topic = "L5AW2-Mon"
notification_title = "Notification Title"
notification_body = f"This message will expire in 5 minutes. Sent at: {datetime.datetime.now()}"
notification_data = {"stop_sequence": 100}

send_fcm_message_with_service_account(topic, notification_title, notification_body, notification_data)
