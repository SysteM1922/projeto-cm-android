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
    firebase_admin.initialize_app(cred, {
        "databaseURL": # URL or Service Account Key method
    })
    print("Firebase app initialized successfully!")


# Reference to the Realtime Database path
    drivers_ref = db.reference('drivers')

    # Fetch data from the Realtime Database
    drivers_data = drivers_ref.get()

    if drivers_data:
        print("All drivers data:")
        for driver_id, driver_info in drivers_data.items():
            print(f"Driver ID: {driver_id}")
            print(f"Driver Info: {driver_info}")
            if "trip_id" in driver_info:
                print(f"  trip_id: {driver_info['trip_id']}")
            if "lat" in driver_info:
                print(f"  Latitude: {driver_info['lat']}")
            if "lng" in driver_info:
                print(f"  Longitude: {driver_info['lng']}")
            if "timestamp" in driver_info:
                print(f"  Timestamp: {driver_info['timestamp']}")
            print("-" * 20)  # Separator between drivers
    else:
        print("No drivers data found.")


