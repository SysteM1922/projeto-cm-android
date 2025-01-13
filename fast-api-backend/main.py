# app/main.py
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from fastapi import FastAPI, HTTPException, Depends, Header
from firebase_admin import credentials, initialize_app, db
from app.models import LocationUpdate, PushNotification, Stop
from app.utils import is_within_distance, send_push_notification
from geopy.distance import geodesic
from typing import List
import os
from dotenv import load_dotenv
from datetime import datetime

# Load environment variables
load_dotenv()

app = FastAPI()

# Initialize Firebase Admin SDK
if not firebase_admin._apps:
    cred = credentials.Certificate(os.getenv("FIREBASE_CREDENTIALS"))
    firebase_admin.initialize_app(cred)
    print("Firebase app initialized successfully!")

# fetch stops from the database
try:
    db = firestore.client()

    # Get a reference to the "stops" collection.
    stops_ref = db.collection('stops')

    # Get all documents in the collection (you can use queries for more specific data)
    docs = stops_ref.stream()

    stops = {}
    for doc in docs:
        stops[doc.id] = doc.to_dict()

    if stops:
        # print("Stops data:")
        # print(stops)
        for stop_id, stop_data in stops.items():
            # print(f"Stop ID: {stop_id}")
            # print(f"Stop Data: {stop_data}")
            pass

    else:
        print("No stops data found.")

except Exception as e:
    print(f"Error fetching data: {e}")

# fetch all users from the database
try:
    db = firestore.client()

    # Get a reference to the "users" collection.
    users_ref = db.collection('users')

    # Get all documents in the collection (you can use queries for more specific data)
    docs = users_ref.stream()

    users = {}
    for doc in docs:
        users[doc.id] = doc.to_dict()

    if users:
        print("Users data:")
        print(users)
        for user_id, user_data in users.items():
            # print(f"User ID: {user_id}")
            # print(f"User Data: {user_data}")
            pass

    else:
        print("No users data found.")



except Exception as e:
    print(f"Error fetching data: {e}")



API_KEY = os.getenv("API_KEY")

def validate_api_key(x_api_key: str = Header(...)):
    if x_api_key != API_KEY:
        raise HTTPException(status_code=403, detail="Unauthorized")

@app.post("/send-notification", dependencies=[Depends(validate_api_key)])
async def send_notification(notification: PushNotification, topic: str):
    """
    Endpoint to send a push notification to a specific topic.
    """
    send_push_notification(topic, notification)
    return {"status": "Notification sent successfully"}

@app.post("/check-proximity", dependencies=[Depends(validate_api_key)])
async def check_proximity(location: LocationUpdate):
    """
    Endpoint to check if the driver is within 100m of any stop and send notifications.
    """
    # Fetch current stops from the database if dynamic
    # For demonstration, using static STOPS list
    nearby_stops = []
    for stop in STOPS:
        if is_within_distance(location.latitude, location.longitude, stop.latitude, stop.longitude):
            nearby_stops.append(stop)
            # Determine the topic based on your logic, e.g., tripId-stopId
            # For demonstration, we'll use stop_id as the topic
            topic = f"stop-{stop.stop_id}"
            notification = PushNotification(
                title="Driver Nearby!",
                message=f"The driver is approaching {stop.stop_name}."
            )
            send_push_notification(topic, notification)
    
    if not nearby_stops:
        return {"status": "Driver is not near any stops."}
    
    return {"status": "Notifications sent for nearby stops.", "stops": [stop.stop_id for stop in nearby_stops]}
