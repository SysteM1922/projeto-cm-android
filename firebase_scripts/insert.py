import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import json

cred = credentials.Certificate('serviceAccountKey.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

with open('stops.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

for stop_id, stop_data in data.items():
    db.collection('stops').document(stop_id).set(stop_data)

print("Stops data imported successfully!")
