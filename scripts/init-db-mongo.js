db = db.getSiblingDB("delivery-service");
db.createCollection("delivery_orders");

db = db.getSiblingDB("socket-service");
db.createCollection("socket_sessions");