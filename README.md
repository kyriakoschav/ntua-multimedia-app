
# Multimedia Document Management System 

## Overview
This repository contains the source code for a desktop Document Management System developed using **Java** and **JavaFX**. It was created as part of the "Multimedia Technology" course at the School of Electrical and Computer Engineering (ECE), National Technical University of Athens (NTUA).

The project implements a robust Model-View-Controller (MVC) architecture and demonstrates object-oriented programming (OOP) principles, graphical user interface (GUI) design, and custom data persistence mechanisms.

## Key Features & Architecture
* **MVC Architecture:** Separation of concerns using models (User, Document, Category, Version), views (FXML-based dynamic GUI), and controllers (e.g., `LoginController`, `MainController`).
* **Role-Based Access Control (RBAC):** Distinct permission levels (User, Author, Admin) dynamically altering the UI and accessible features (e.g., restricted tabs, read-only vs. editing rights).
* **Document Versioning:** Built-in version control for documents, allowing authors and admins to track and view up to two previous revisions, alongside the current text.
* **Subscription & Notifications:** A "Follow" mechanism enabling users to track document changes, featuring smart, one-time popup notifications upon login.
* **Data Persistence (JSON):** Custom state management using the Jackson `ObjectMapper` library to serialize/deserialize application state (Categories, Users, Documents) to local JSON files (`medialab` directory) ensuring data integrity across sessions.
* **Cascade Operations:** Automated referential integrity, such as cascading deletions of documents and subscriptions when an admin removes a category.

## Technologies & Tools
* **Language:** Java
* **Framework:** JavaFX (UI implemented via FXML)
* **Build Tool:** Maven
* **Data Serialization:** JSON (Jackson Library)
* **Development Environment:** IntelliJ IDEA

## Documentation
For a detailed architectural breakdown and data schema design, please refer to the attached `αναφορα τελικής εργασίας.pdf` (in Greek) included in this repository.

## Team
* **Kyriakos Chaviaras** 
