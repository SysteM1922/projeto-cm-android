import firebase_admin
from firebase_admin import credentials
from firebase_admin import auth

firebase_admin.initialize_app(credentials.Certificate('serviceAccountKey.json'))

uid = "Qj7SarNgnxfN6hukqjkbYl2i0so2"

try:
    auth.set_custom_user_claims(uid, {'role': 'driver'})
    print("Role set successfully!")
except Exception as e:
    print("Error setting custom claims: ", e)