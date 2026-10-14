# SmartTaskAI — AI-Powered Task Planner

SmartTaskAI is a Java full-stack web application that helps users manage tasks, break tasks into smaller steps, and track progress using locally running AI.

## Features

- Create, view, update, and delete tasks
- Mark tasks as completed and filter tasks
- Generate task-step suggestions using local AI
- Save AI-generated suggestions as subtasks
- Add, complete, and delete subtasks
- Track subtask completion progress
- Validate API requests
- Persist data using MySQL
- Test backend functionality using automated tests

## Tech Stack

**Backend:** Java, Spring Boot, Spring Data JPA, Hibernate, Maven  
**Frontend:** React, Vite, JavaScript, React Markdown  
**Database:** MySQL  
**AI:** Ollama with Llama 3.2 3B  
**Tools:** IntelliJ IDEA, VS Code, Postman, Git, GitHub

## Project Structure

```text
SmartTaskAI/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/smarttaskai/backend/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── mvnw
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.js
├── .gitignore
└── README.md
```

## Prerequisites

Install the following tools:

- Java JDK
- Maven (or use the included Maven Wrapper)
- Node.js and npm
- MySQL Server
- Ollama

Download links:

- Java: https://adoptium.net/
- Node.js: https://nodejs.org/
- MySQL: https://dev.mysql.com/downloads/
- Ollama: https://ollama.com/
- Git: https://git-scm.com/

## Setup and Run

### 1. Clone the repository

```bash
git clone https://github.com/ganesharnakonda-2604/SmartTaskAI.git
cd SmartTaskAI
```

### 2. Configure MySQL

Create the database in MySQL:

```sql
CREATE DATABASE smarttaskai_db
CHARACTER SET utf8mb4;
```

Configure the database username and password as environment variables in PowerShell:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "YOUR_MYSQL_PASSWORD"
```

Replace the placeholder with your local MySQL password. Never commit real passwords to GitHub.

### 3. Start the backend

Open a PowerShell terminal:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

If the Maven Wrapper is unavailable, use:

```powershell
mvn spring-boot:run
```

The backend runs on `http://localhost:8080`.

Health endpoint:

`GET http://localhost:8080/api/health`

### 4. Start the AI service

Install and start Ollama, then download the model if necessary:

```powershell
ollama pull llama3.2:3b
```

Ollama normally serves its local API at `http://localhost:11434`.

### 5. Start the frontend

Open a second terminal:

```powershell
cd frontend
npm install
npm run dev
```

Open the local URL printed by Vite, usually `http://localhost:5173`.

Keep the backend, frontend, and Ollama running while using AI features.

## API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Check backend health |
| GET | `/api/tasks` | Retrieve tasks |
| POST | `/api/tasks` | Create a task |
| GET | `/api/tasks/{taskId}` | Retrieve one task |
| PUT | `/api/tasks/{taskId}` | Update a task |
| DELETE | `/api/tasks/{taskId}` | Delete a task |
| GET | `/api/tasks/{taskId}/subtasks` | Retrieve subtasks |
| POST | `/api/tasks/{taskId}/subtasks` | Create a subtask |
| PATCH | `/api/subtasks/{subtaskId}` | Update subtask completion |
| DELETE | `/api/subtasks/{subtaskId}` | Delete a subtask |
| POST | `/api/ai/suggest` | Generate AI task-step suggestions |

## Run Tests

From the `backend` directory:

```powershell
.\mvnw.cmd clean test
```

The tests use a separate `smarttaskai_test_db` database. Configure the database credentials before running them.

## Learning Goals

This project provides practice with:

- REST API development using Spring Boot
- Object-relational mapping with JPA and Hibernate
- React frontend development and API integration
- MySQL database design
- Request validation and error handling
- Automated backend testing
- Integrating a locally hosted language model
- Git version control and GitHub collaboration

## Future Improvements

- User registration and authentication
- User-specific task management
- AI-generated priorities and deadlines
- Deployment and CI/CD
- Additional frontend and integration tests

## Author

Developed as a learning project to practice Java full-stack development and AI integration.