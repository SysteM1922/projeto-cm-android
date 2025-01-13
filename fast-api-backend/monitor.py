from time import sleep
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from fastapi import FastAPI, HTTPException, Depends, Header
from firebase_admin import credentials, initialize_app, db
from app.models import LocationUpdate, PushNotification, Stop
from app.utils import is_within_distance, send_push_notification
from geopy.distance import geodesic
from typing import Any, Dict, List
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
        "databaseURL": os.getenv("FIREBASE_DB_URL")
    })
    print("Firebase app initialized successfully!")

# Initialize Firestore and Realtime Database clients
firestore_client = firestore.client()
realtime_db = db.reference()

# Fetch stops from Firestore
def fetch_stops() -> Dict[str, Dict[str, Any]]:
    try:
        stops_ref = firestore_client.collection('stops')
        docs = stops_ref.stream()
        stops = {doc.id: doc.to_dict() for doc in docs}
        if stops:
            print(f"Fetched {len(stops)} stops.")
        else:
            print("No stops data found.")
        return stops
    except Exception as e:
        print(f"Error fetching stops: {e}")
        return {}

# Fetch users from Firestore
def fetch_users() -> Dict[str, Dict[str, Any]]:
    try:
        users_ref = firestore_client.collection('users')
        docs = users_ref.stream()
        users = {doc.id: doc.to_dict() for doc in docs}
        if users:
            print(f"Fetched {len(users)} users.")
        else:
            print("No users data found.")
        return users
    except Exception as e:
        print(f"Error fetching users: {e}")
        return {}

# Process drivers data
def process_drivers(drivers_data: Dict[str, Any], stops: Dict[str, Dict[str, Any]], users: Dict[str, Dict[str, Any]]):
    for driver_id, driver_info in drivers_data.items():
        trip_id = driver_info.get('trip_id')
        driver_lat = driver_info.get('lat')
        driver_lng = driver_info.get('lng')
        
        if not trip_id or driver_lat is None or driver_lng is None:
            print(f"Driver {driver_id} data incomplete. Skipping.")
            continue
        
        # Find users who have preferences matching this trip_id
        interested_users = []
        for user_id, user_data in users.items():
            preferences = user_data.get('preferences', [])
            for pref in preferences:
                if pref.get('trip_id') == trip_id:
                    interested_users.append({
                        "user_id": user_id,
                        "stop_id": pref.get('stop_id'),
                        "device_token": user_data.get('device_token')
                    })
        
        if not interested_users:
            print(f"No users interested in trip_id {trip_id}.")
            continue
        
        # For each interested user, check proximity to their preferred stop
        for user in interested_users:
            stop_id = user.get('stop_id')
            device_token = user.get('device_token')
            if not stop_id or not device_token:
                print(f"User {user.get('user_id')} preference incomplete. Skipping.")
                continue
            
            stop = stops.get(stop_id)
            if not stop:
                print(f"Stop {stop_id} not found. Skipping.")
                continue
            
            stop_lat = stop.get('stop_lat')
            stop_lng = stop.get('stop_lon')
            
            if stop_lat is None or stop_lng is None:
                print(f"Stop {stop_id} coordinates incomplete. Skipping.")
                continue
            
            # Calculate distance
            driver_coords = (driver_lat, driver_lng)
            stop_coords = (stop_lat, stop_lng)
            distance = geodesic(driver_coords, stop_coords).meters
            
            print(f"Driver {driver_id} is {distance:.2f} meters from stop {stop_id}.")
            
            if distance <= 100:
                topic = "L5AW2-Mon" # TOPIC EXAMPLE TODO:
                # Send (mock) notification
                notification = PushNotification(
                    title="Driver Nearby!",
                    message=f"The driver is approaching {stop.get('stop_name', 'a stop')}."
                )
                send_push_notification(topic, notification)
                print(f"Notification sent to user {user.get('user_id')} for stop {stop_id}.")

# Infinite loop to monitor Realtime Database
def monitor_drivers(stops: Dict[str, Dict[str, Any]], users: Dict[str, Dict[str, Any]]):
    while True:
        try:
            # Fetch drivers data from Realtime Database
            drivers_ref = realtime_db.child('drivers')
            drivers_data = drivers_ref.get()
            # print("Drivers data: ", drivers_data)
            if drivers_data:
                print(f"Fetched data for {len(drivers_data)} drivers.")
                process_drivers(drivers_data, stops, users)
            else:
                print("No drivers data found.")
            
        except Exception as e:
            print(f"Error fetching drivers data: {e}")
        
        # Wait for 5 seconds before the next fetch
        sleep(30)

if __name__ == "__main__":
    print("Starting driver monitoring...")
    stops = fetch_stops()
    #print("Stops: ", stops)
    #print("-" * 20)
    users = fetch_users()
    #print("Users: ", users)
    # print("-" * 20)
    monitor_drivers(stops, users)
