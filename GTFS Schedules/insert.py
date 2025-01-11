import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate('serviceAccountKey.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

with open('trips.txt', 'r', encoding='utf-8') as f:
    # route_id,service_id,trip_id,trip_headsign
    # L11,AW,L11AW1,
    f.readline()  # Skip the first line
    data = []
    for line in f:
        line = line.strip().split(',')
        data.append({
            "route_id": line[0],
            "service_id": line[1],
            "trip_id": line[2],
            "trip_headsign": line[3]
        })

for stop_data in data:
    db.collection('trips').document(stop_data['trip_id']).set(stop_data)

print("Stops data imported successfully!")
