# REST API Documentation - Status Drafter Tool

Base URL: `/api/v1`

## 1. POST /status
Submit a raw status string to be parsed and stored.

**Request Body:**
```json
{
  "userId": 1,
  "rawContent": "Yesterday I worked on authentication module for 3h. Today I will finish the DB schema. I am stuck on the MySQL driver issue."
}
```

**Response (201 Created):**
```json
{
  "id": 101,
  "statusDate": "2024-05-20",
  "formattedReport": {
    "yesterday": ["Worked on authentication module"],
    "today": ["Finish the DB schema"],
    "blockers": ["MySQL driver issue"],
    "hoursSpent": 3.0
  },
  "confidenceScore": 0.95
}
```

---

## 2. GET /status/daily
Fetch all parsed statuses for a specific date (Consolidated Team Report).

**Query Params:**
- `date` (format: YYYY-MM-DD, default: today)
- `team` (optional)

**Response:**
```json
[
  {
    "userName": "John Doe",
    "yesterday": "Completed API docs",
    "today": "Working on UI",
    "blockers": "None"
  }
]
```

---

## 3. GET /status/user/{id}
Get history of statuses for a specific user.

---

## 4. GET /status/weekly
Generate a summary for the last 7 days.

---

## Common Error Codes
- `400 Bad Request`: Validation failed (e.g., empty raw content).
- `404 Not Found`: User not found.
- `500 Internal Server Error`: Parsing engine failure.
