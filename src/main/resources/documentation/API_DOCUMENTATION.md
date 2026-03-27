# Timesheet Module — API Documentation

## Base URL
http://localhost:9001

## 1. Punch In

### Endpoint
POST /attendance/punch-in

### Role
EMPLOYEE

### Description
Creates a new attendance record for today with current time as punchInTime. Status is set to `MISS_SWIPE` until punch out.

### Request Body
{}

### Response — 201 Created
{
  "attendanceId": 101,
  "employeeId": 5001,
  "employeeName": "Mayank Sharma",
  "date": "2026-03-24",
  "punchInTime": "09:15:00",
  "punchOutTime": null,
  "status": "MISS_SWIPE",
  "hoursWorked": null,
  "isRegularized": false
}

## 2. Punch Out

### Endpoint
POST /attendance/punch-out

### Role
EMPLOYEE

### Description
Creates or Updates today's attendance record with punchOutTime. Calculates hoursWorked and updates status automatically.

### Status Calculation Logic
hoursWorked >= 4  → PRESENT
hoursWorked < 4   → HALF_DAY

### Request Body
{}

### Response — 200 OK
{
  "attendanceId": 101,
  "employeeId": 5001,
  "employeeName": "Mayank Sharma",
  "date": "2026-03-24",
  "punchInTime": "09:15:00",
  "punchOutTime": "18:30:00",
  "status": "PRESENT",
  "hoursWorked": 9.25,
  "isRegularized": false
}

## 3. Apply Leave

### Endpoint
POST /leaves

### Role
EMPLOYEE

### Description
Employee applies for leave. Status is set to PENDING. Email notification sent to HR.

### Request Body
{
  "startDate": "2026-04-01",
  "endDate": "2026-04-03",
  "leaveType": "SICK",
  "reason": "Fever and cold"
}

### Validations
startDate  → required, cannot be in the past (@FutureOrPresent)
endDate    → required, must be after startDate (service level)
leaveType  → required (SICK / CASUAL / EARNED)
reason     → required, cannot be blank

### Response — 201 Created
{
  "leaveId": 201,
  "employeeId": 5001,
  "employeeName": "Mayank Sharma",
  "startDate": "2026-04-01",
  "endDate": "2026-04-03",
  "totalDays": 3,
  "leaveType": "SICK",
  "reason": "Fever and cold",
  "status": "PENDING",
  "approvedByName": null,
  "approvalDate": null,
  "rejectionReason": null
}

## 4. Approve / Reject Leave

### Endpoint
PATCH /leaves/{leaveId}/approval

### Role
HR

### Description
HR approves or rejects a leave request. On approval, leave balance is automatically deducted. Email sent to employee.

### Path Variable
leaveId → Long (required)

### Request Body
{
  "status": "APPROVED",
  "rejectionReason": null
}
OR
json
{
  "status": "REJECTED",
  "rejectionReason": "Insufficient leave balance"
}

### Response — 200 OK
{
  "leaveId": 201,
  "employeeId": 5001,
  "employeeName": "Mayank Sharma",
  "startDate": "2026-04-01",
  "endDate": "2026-04-03",
  "totalDays": 3,
  "leaveType": "SICK",
  "reason": "Fever and cold",
  "status": "APPROVED",
  "approvedByName": "Priya HR",
  "approvalDate": "2026-03-24",
  "rejectionReason": null
}

## 5. Get Leave Balance

### Endpoint
GET /leaves/{employeeId}balance?year=2026

### Role
EMPLOYEE (own balance) / HR (any employee)

### Description
Returns leave balance for the given year.

### Query Parameter
year → Integer (required)

### Response — 200 OK
{
  "employeeId": 5001,
  "employeeName": "Mayank Sharma",
  "year": 2026,
  "sickLeaveBalance": 7,
  "casualLeaveBalance": 8,
  "earnedLeaveBalance": 12,
  "carriedForwardEarnedDays": 3
}