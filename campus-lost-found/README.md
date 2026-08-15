# FindBack — Campus Lost & Found Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://adoptium.net)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16+-blue)](https://www.postgresql.org)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple)](https://getbootstrap.com)

> **University Design Patterns Lab Project** — A fully functional campus platform demonstrating 5 classic Design Patterns in a real-world Spring Boot application.

---

## 📖 Problem Statement

Students on campus lose belongings daily (phones, IDs, bags, keys) with no centralized platform to report or search for them. **FindBack** solves this by providing a smart, observable, pattern-driven lost & found management platform.

---

## ✨ Features

- 🔐 **Authentication** — Register, Login, Logout with BCrypt password hashing
- 📋 **Report Lost Item** — Full form with image upload, urgency flag, reward amount
- 🤝 **Report Found Item** — Post found items and auto-trigger matching
- 🔍 **Browse & Search** — Category/location/date/keyword filters with pagination
- 🔔 **Smart Match Notifications** — Auto-notify lost item owners when a match is detected
- 📊 **User Dashboard** — Stats, recent reports, match notifications
- 📁 **My Reports** — Edit, delete, close, claim own reports
- 🛡️ **Admin Panel** — Manage users, reports, verify items, view stats
- 📱 **Responsive** — Works on mobile, tablet, and desktop

---

## 🏗️ Architecture

```
Browser (Thymeleaf)
        ↓
  Spring MVC Controllers
        ↓
  Service Layer
        ↓
  Design Patterns Layer
   ├── Singleton (LostFoundRegistry)
   ├── Factory  (ItemFactory)
   ├── Decorator (UrgentTag, RewardTag, VerifiedTag)
   ├── Strategy (FilterByCategory, FilterByLocation, FilterByDate)
   └── Observer (LostFoundSubject → ReportingUser → Notification)
        ↓
  JPA Repositories (Spring Data)
        ↓
  PostgreSQL Database
```

---

## 🎨 Design Patterns

### 1. Singleton — `LostFoundRegistry`
Central in-memory registry of active items. Spring's `@Component` default scope is singleton — one instance for the entire app lifetime.

**Flow:** Controller → ItemService → LostFoundRegistry → Active Items

---

### 2. Factory Method — `ItemFactory`
Creates the correct concrete item type based on category. Controllers never instantiate items directly.

```java
Item item = ItemFactory.createItem(Category.ELECTRONICS);
// Returns → ElectronicsItem with defaults pre-set
```

**Concrete Types:** `ElectronicsItem`, `DocumentItem`, `BagItem`, `AccessoryItem`, `OtherItem`

---

### 3. Decorator — `ItemComponent`
Dynamically adds Urgent, Reward, and Verified badges to items at runtime.

```java
ItemComponent item = new BaseItem(savedItem);
if (urgent)  item = new UrgentTag(item);   // adds [URGENT]
if (reward)  item = new RewardTag(item);   // adds [REWARD]
if (verified) item = new VerifiedTag(item); // adds ✓
```

---

### 4. Strategy — `FilterStrategy`
Pluggable filtering algorithm. Switches at runtime based on user's filter choice.

| Strategy | Trigger |
|---|---|
| `FilterByCategory` | User selects a category |
| `FilterByLocation` | User types a location |
| `FilterByDate` | User picks a date |

---

### 5. Observer — `LostFoundSubject` + `ReportingUser`
When a new found item is posted, the matching service computes a score and notifies all observers.

**Scoring Algorithm:**
```
Category match  = +40 points
Location match  = +30 points
Keyword match   = +20 points
Date proximity  = +10 points
Threshold       = 40 (at minimum, category must match)
```

**Flow:** Found Item → MatchingService → LostFoundSubject.notifyObservers() → ReportingUser.onMatchFound() → NotificationService → PostgreSQL

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Web | Spring MVC + Thymeleaf |
| ORM | Spring Data JPA + Hibernate |
| Security | Spring Security + BCrypt |
| Database | PostgreSQL |
| Build | Maven |
| Frontend | Bootstrap 5 + Bootstrap Icons |
| Deployment | Render.com |

---

## 🗄️ Database Schema

**Tables:** `users`, `items`, `notifications`

**Relationships:**
- `users` → `items` (OneToMany)
- `users` → `notifications` (OneToMany)
- `notifications` → `items` (ManyToOne, optional)

---

## ⚙️ Environment Variables

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://host:5432/db` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `yourpassword` |
| `PORT` | Server port | `8080` |

---

## 🚀 Local Setup

### Prerequisites
- Java 17+ (Eclipse Temurin recommended)
- Maven 3.6+
- PostgreSQL 14+

### 1. Clone & Setup Database
```bash
git clone https://github.com/yourusername/findback-campus.git
cd findback-campus

# Create database
psql -U postgres -c "CREATE DATABASE lostfound_db;"
```

### 2. Configure (optional — defaults work locally)
Edit `src/main/resources/application.properties` or set env vars:
```
DB_URL=jdbc:postgresql://localhost:5432/lostfound_db
DB_USERNAME=postgres
DB_PASSWORD=yourpassword
```

### 3. Run
```bash
mvn spring-boot:run
```

Open: **http://localhost:8080**

### 4. Create Admin Account
After registering a normal account, update the role in the DB:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'your@email.com';
```
Then go to: **http://localhost:8080/admin**

---

## ☁️ Deploy to Render

### Step 1: Push to GitHub
```bash
git init
git add .
git commit -m "Initial commit — FindBack Campus Lost & Found"
git remote add origin https://github.com/yourusername/findback.git
git push -u origin main
```

### Step 2: Create PostgreSQL on Render
1. Go to [render.com](https://render.com) → New → PostgreSQL
2. Copy the **Internal Database URL**

### Step 3: Deploy Web Service
1. New → Web Service → Connect your GitHub repo
2. Build Command: `mvn clean package -DskipTests`
3. Start Command: `java -jar target/campus-lost-found-1.0.0.jar`
4. Set Environment Variables:
   ```
   DB_URL=<Internal URL from step 2>
   DB_USERNAME=<from Render DB>
   DB_PASSWORD=<from Render DB>
   ```
5. Deploy!

---

## 📸 Screenshots

> Add screenshots of the running application here.

---

## 👨‍💻 Author

Design Patterns Lab Project — University of [Your University]

---

## 📄 License

MIT License
