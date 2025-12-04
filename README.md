[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/VM5AL9S5)

# Team Name
Team-12

## Team Members 
Ryan Ayoubi
Gabriel Clarence
Kareema Kilani
Asim Mohammed

## Summary of Contribution Areas
Ryan Ayoubi
- Add Here
  
Gabriel Clarence
- Add Here

Kareema Kilani

- AI NLP Search Service Backend and Frontend
- Report System Backend and Frontend
- Created DB in Amazon RDS with Mock Data, however DB structure had multipe iterations with Gabriel.
  
Asim Mohammed
- Add Here

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
2. Configure Application Properties   
   a. Create a copy of application-example.properties in src/main/resources/
   b. Rename the file to application.properties (it should be in src/main/resources/application.properties)  
   c. Set your secrets
   d. Set your frontend url
   ```application.properties 
   # in backend/src/main/resources/application.properties
	spring.application.name=backend
	# Use http://localhost:5173 for dev
	# Use http://localhost:3000 for docker
	frontend.url=http://localhost:5173 #<--- change this 
   #---- more code bellow ----
   ```
   
4. Go to Backend Folder
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
   b. Set the frontend url for the url redirect to http://localhost:5173/
   ```.env 
   # in frontend/.env
	VITE_REDIRECT_URI=http://localhost:5173 #<--- change this 
	VITE_API_BASE_URL=http://localhost:8080
   ```
   
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
  1. Update the frontend env and backend application.properties frontend url to http://localhost:3000.
  ```.env 
   # in frontend/.env
	VITE_REDIRECT_URI=http://localhost:3000 #<--- change this 
	VITE_API_BASE_URL=http://localhost:8080
  ```
  ```application.properties 
   # in backend/src/main/resources/application.properties
	spring.application.name=backend
	# Use http://localhost:5173 for dev
	# Use http://localhost:3000 for docker
	frontend.url=http://localhost:3000 #<--- change this 
   #---- more code bellow ----
   ```
  3. Go to the project root file (where the Docker-Compose.yml is located)  
  4. Build and Start Containers  
Run the following command to build (if needed) and start your containers:  
  ```
  docker-compose up --build
  ```
  3. Check frontend and backend  
     a. Frontend: http://localhost:3000.  
     b. Backend: http://localhost:8080.
     
# Backend API List
Health
- GET /health -> return "Backend is running"

Auth  
- POST  /auth/exchange -> exchange code from login to token

NLP
- POST /api/search/nlp -> Return list of listings
- GET /api/search/health" -> Return {"status": "NLP Search Service is running"}
  
Listing
- GET /api/listings/hello  -> return hello from backend
- POST /api/listings/create -> create an empty listing only
- GET /api/listings/search -> Return list of listings
- GET /api/listings/{listingId} -> Return listing 
- GET /api/listings/user -> Return list of seller listing
- PATCH /api/listings/{listingId} -> Input(update), Return updated listing
- POST /api/listings/{listingId}/upload -> Input(photo), Return updated photo url
- PATCH /api/listings/{listingID}/flag -> Return updated listing
- DELETE /api/listings/delete/{listingID} -> Return "Deleted Successfully"

