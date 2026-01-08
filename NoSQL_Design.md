# NoSQL Design for Patient Notes and Medical Logs

## Overview
While the core of the Hospital Management System relies on a relational database (MySQL/PostgreSQL) for transactional integrity and structured relationships (e.g., Appointments, Billing), certain types of data are better suited for NoSQL storage. 

Specifically, **Patient Notes** and **Medical Logs** are often unstructured or semi-structured, varying significantly in content and length.

## Proposed NoSQL Model (MongoDB Style)

For patient notes and medical logs, a document-oriented database like MongoDB or a key-value store like DynamoDB is recommended.

### Patient Document
```json
{
  "_id": "64f1a2b3c4d5e6f7g8h9i0j1",
  "patient_id": 101,
  "notes": [
    {
      "timestamp": "2025-10-15T10:30:00Z",
      "doctor_id": 1,
      "content": "Patient reported mild chest pain. Vital signs stable.",
      "attachments": ["ekg_result_101.jpg"],
      "tags": ["cardiology", "initial_consult"]
    },
    {
      "timestamp": "2025-10-16T09:00:00Z",
      "doctor_id": 1,
      "content": "Follow-up: Pain subsided. Prescription adjusted.",
      "tags": ["follow-up"]
    }
  ],
  "medical_logs": [
    {
      "event": "Login",
      "user": "dr_johnson",
      "timestamp": "2025-10-15T10:25:00Z",
      "action": "Viewed Patient Records"
    }
  ]
}
```

## Relational vs. NoSQL Comparison

| Feature | Relational (MySQL) | NoSQL (MongoDB/Document) |
| :--- | :--- | :--- |
| **Data Structure** | Highly structured, fixed schema. | Unstructured or semi-structured. |
| **Scalability** | Vertical (Scale Up). | Horizontal (Scale Out/Sharding). |
| **Query Type** | Complex joins and ACID compliance. | Fast lookups by key or nested attributes. |
| **Use Case** | Appointments, Payments, Inventory. | Patient Notes, Telemetry, Logs. |
| **Flexibility** | Schema changes are expensive. | Dynamic schema, easy to add new fields. |

## Justification
- **Flexibility**: Medical notes can include text, images, or even small binary blobs (sensor data). A document schema allows these to be stored together without complex join logic.
- **Performance**: Retrieving the entire history of notes for a single patient is a single lookup in a document store, whereas in SQL it might require joining multiple large tables.
- **Scalability**: As the volume of medical logs grows, a NoSQL database can easily scale across multiple nodes.
