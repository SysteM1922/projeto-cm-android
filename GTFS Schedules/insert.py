import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate('serviceAccountKey.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

with open('stops.txt', 'r', encoding='utf-8') as f:
    # stop_id,stop_name,stop_lat,stop_lon
    # EA,Estação de Aveiro,40.643771,-8.640994
    f.readline()  # Skip the first line
    data = []
    for line in f:
        stop_id, stop_name, stop_lat, stop_lon = line.strip().split(',')
        data.append({
            'stop_id': stop_id,
            'stop_name': stop_name,
            'stop_lat': float(stop_lat),
            'stop_lon': float(stop_lon)
        })

for stop_data in data:
    db.collection('stops').document(stop_data['stop_id']).set(stop_data)

print("Stops data imported successfully!")
