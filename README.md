# Cab Booking Application

A full-stack cab booking platform built with **Spring Boot** and **React**.
It supports authentication, ride booking, driver matching, and real-time ride updates using WebSockets.

## Tech Stack

Backend

* Java
* Spring Boot
* Spring Security
* JWT Authentication
* WebSocket
* PostgreSQL (Supabase)

Frontend

* React
* Vite
* Axios
* WebSocket

Deployment

* Backend: Render (Docker)
* Frontend: Vercel

## Features

* User registration and login
* JWT authentication
* Role selection (User / Driver)
* Book a ride
* Driver ride acceptance
* Real-time ride updates with WebSocket
* Password reset via email
* Secure backend using Spring Security

## Project Structure

```
cab-booking-app
│
├── backend
│   ├── src
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend
│   ├── src
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

## Running the Project Locally

### Backend

```
cd backend
mvn spring-boot:run
```

Backend runs on:

```
http://localhost:8080
```

### Frontend

```
cd frontend
npm install
npm run dev
```

Frontend runs on:

```
http://localhost:5173
```

## Environment Variables

Create `.env` files for both backend and frontend before running the project.

Example backend variables:

```
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
EMAIL_USERNAME=
EMAIL_PASSWORD=
```

## Deployment

* Backend deployed using **Docker on Render**
* Frontend deployed using **Vercel**

## Live Demo

Frontend:
https://cab-booking-app-pi.vercel.app

Backend API:
https://cab-booking-backend-di7d.onrender.com


## Author

Developed as a full-stack project for learning and portfolio purposes.
