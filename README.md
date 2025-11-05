[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/VM5AL9S5)

# Scrum Manager
- https://docs.google.com/spreadsheets/d/1HfVKEmBjztNtblDBRnfNRHagL97_EXRULnCIfn5pPmA/edit?gid=1742583490#gid=1742583490  
# Project Folder  
- https://drive.google.com/drive/folders/0AAV4cqDMeHrtUk9PVA?dmr=1&ec=wgc-drive-hero-goto

# How to run the app:
# Backend
Prerequisites  
1. Java JDK  
```bash
  java -version
```
2. Maven
```bash
  mvn -v
```

Steps to Run  
1. Clone the repo
```bash
  git clone https://github.com/gopinathsjsu/cmpe202-02-team-project-cmpe202-team11.git
  cd cmpe202-02-team-project-cmpe202-team11
```
2. Configure Application Properties (TBD - need to integrate with AWS Postgress)  
   a. Create a copy of application-example.properties in src/main/resources/
   b. Rename the file to application.properties (it should be in src/main/resources/application.properties)  
   c. Set the username and password for the db and more  
3. Go to Backend Folder
```bash
	cd backend
```
5. Build the Project
```bash
	mvn clean install
```
5. Run the Backend
```bash
	mvn spring-boot:run
```
6. Check Backend  
   a. It should run at http://localhost:8080.

7. To run test  
   a. Go to backend folder
   b. run test
	```bash
		mvn test
	```
   
# Frontend
Prerequisites  
1. Node.js
```bash
  node -v
```
2. npm
```bash
  npm -v
```

Steps to Run  
1. Clone the repo (if you have not)
```bash
  git clone https://github.com/gopinathsjsu/cmpe202-02-team-project-cmpe202-team11.git
  cd cmpe202-02-team-project-cmpe202-team11
```
2. Configure .env frontend url (TBD - need to integrate with AWS Postgress)  
   a. Go to.env in frontend  
   b. Set the frontend url for the url redirect  
   
4. Go to Frontend Folder
```bash
	cd frontend
```
4. Install Dependencies
```bash
	npm install
```
5. Run the Frontend
```bash
	npm run dev
```
6. Check Frontend  
   a. It should run at [http://localhost:5173.](http://localhost:5173/)

# Docker
How to Run the Docker-Compose File  

These instructions will guide you through running your application using Docker Compose.  

Prerequisites  
	1.	Docker: Make sure Docker is installed on your system. You can download it [here](https://www.docker.com/get-started).  
	2.	Docker Compose: Ensure Docker Compose is installed (Docker Desktop usually includes it). Check by running:  
  ```bash
  docker-compose --version
  ```

Steps to Run  
  1.	Go to the project root file (where the Docker-Compose.yml is located)  
  2. Build and Start Containers  
Run the following command to build (if needed) and start your containers:  
  ```
  docker-compose up --build
  ```
  3. Check frontend and backend  
     a. Frontend: http://localhost:3000.  
     b. Backend: http://localhost:8080.
     
# Backend API List
Auth  
- POST  /auth/exchange -> exchange code from login to token
  
Listing
- GET /api/listings/hello
- POST /api/listings/create
- GET /api/listings/search
- GET /api/listings/{listingId}
- GET /api/listings/user
- PATCH /api/listings/{listingId}
- POST /api/listings/{listingId}/upload
- PATCH /api/listings/{listingID}/flag
- DELETE /api/listings/delete/{listingID}
