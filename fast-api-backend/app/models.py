# app/models.py
from pydantic import BaseModel
from typing import Optional

class LocationUpdate(BaseModel):
    driver_id: str
    latitude: float
    longitude: float

class Stop(BaseModel):
    stop_id: str
    stop_name: str
    latitude: float
    longitude: float

class PushNotification(BaseModel):
    title: str
    message: str
