# Quick Start: Test FinSecure Auth in 5 Minutes

## Prerequisites Checklist
- [ ] MySQL Server running on `localhost:3306`
- [ ] MySQL credentials: `root:root`
- [ ] Java 17+ installed
- [ ] Maven installed

---

## Step 1: Setup Database (1 minute)

**Option A: MySQL Command Line**
```bash
mysql -u root -p
```
Then paste and run:
```sql
CREATE DATABASE IF NOT EXISTS finsecure;
```

**Option B: MySQL Workbench**
- Right-click "Schemas" → "Create Schema"
- Name: `finsecure`
- Click Apply

**Verify:**
```sql
SHOW DATABASES;
-- Should show 'finsecure' in the list
```

---

## Step 2: Start Application (1 minute)

Navigate to project folder:
```bash
cd C:\FinSecure\FinSecureApp
```

Start the app:
```bash
mvn clean spring-boot:run
```

**Wait for:**
```
Started FinSecureProjectApplication in X.XXX seconds
Tomcat started on port(s): 8085 (http)
```

---

## Step 3: Test SignUp (1 minute)

### Using PowerShell:
```powershell
$body = @{
    username = "testuser"
    password = "TestPassword123"
    role = "EMPLOYEE"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8085/finsecure/public/signup" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body $body | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

### Expected Output:
```json
{
  "username": "testuser",
  "role": "EMPLOYEE"
}
```

---

## Step 4: Test Login (1 minute)

### Using PowerShell:
```powershell
$body = @{
    username = "testuser"
    password = "TestPassword123"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8085/finsecure/public/login" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body $body | Select-Object -ExpandProperty Content | ConvertFrom-Json

$response
```

### Expected Output:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "testuser",
  "isValid": true
}
```

**✓ Copy the token value!** You'll use it in the next step.

---

## Step 5: Test Protected Request (1 minute)

### Using PowerShell:
```powershell
$token = "eyJhbGciOiJIUzI1NiJ9..."  # Replace with token from Step 4

Invoke-WebRequest -Uri "http://localhost:8085/finsecure/admin/someendpoint" `
  -Method GET `
  -Headers @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
  }
```

---

## Alternative: Use the Provided Test Script

Simply run:
```bash
.\test-auth.ps1
```

This script will automatically:
- ✓ Check if server is running
- ✓ Create a new user with signup
- ✓ Login and get JWT token
- ✓ Display results in console

---

## Troubleshooting

### "Unknown database 'finsecure'"
1. Create database:
   ```sql
   CREATE DATABASE finsecure;
   ```
2. Restart application

### "Connection refused"
1. Check MySQL is running: `mysql -u root -p` should work
2. Check port: Application expects 8085
3. Check datasource URL in `application.properties`

### "Invalid username or password"
1. Ensure signup was successful
2. Use exact same username and password for login
3. Check console logs for errors

### "Token is invalid"
1. Token expires in 1 hour
2. Get a fresh token by logging in again
3. Format: `Bearer <token>` (with space between Bearer and token)

---

## Available Roles

Create users with any of these roles:
- `ADMIN`
- `EMPLOYEE`
- `FINANCE`
- `HR`
- `SYSTEM`

Example:
```json
{
  "username": "admin1",
  "password": "AdminPass123",
  "role": "ADMIN"
}
```

---

## JWT Token Info

**Generated Token Details:**
- **Issued At:** Current timestamp
- **Expires In:** 1 hour from issue
- **Secret Key:** `descartesdescartesdescartes12345`
- **Algorithm:** HS256

**Extract Username from Token:**
In the JWT filter, token is parsed to extract username and validate.

---

## Key API Endpoints

| Method | Endpoint | Auth Required | Purpose |
|--------|----------|---------------|---------|
| POST | `/finsecure/public/signup` | ❌ No | Register new user |
| POST | `/finsecure/public/login` | ❌ No | Get JWT token |
| GET/POST | `/finsecure/admin/**` | ✅ Yes | Admin operations |
| GET/POST | `/finsecure/employee/**` | ✅ Yes | Employee operations |
| GET/POST | `/finsecure/finance/**` | ✅ Yes | Finance operations |
| GET/POST | `/finsecure/hr/**` | ✅ Yes | HR operations |

---

## Testing Workflow Summary

```
1. Start App (mvn clean spring-boot:run)
    ↓
2. Verify DB (mysql -u root -p → CREATE DATABASE finsecure)
    ↓
3. Signup (POST /signup with username/password/role)
    ↓
4. Login (POST /login with username/password → get token)
    ↓
5. Use Token (Include "Authorization: Bearer <token>" in requests)
    ↓
6. Access Protected Endpoints
```

---

## Files Provided

- **TESTING_GUIDE.md** - Comprehensive testing guide with examples
- **test-auth.ps1** - Automated PowerShell testing script
- **setup-database.sql** - Database initialization SQL
- **FinSecure-Auth-Collection.postman_collection.json** - Postman collection for API testing

---

## Need Help?

Check the console logs for:
```
Username password authenticated       ← Indicates successful login
user with username loaded            ← User details loaded
Token in header : eyJ...             ← Token validation in progress
```

Any errors will be displayed with stack traces.

