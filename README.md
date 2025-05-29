# Bajaj Finserv Backend Test - Webhook Submission App

This Spring Boot application is built to automate the process of generating a webhook and submitting a SQL query to Bajaj Finserv's assessment API. The app uses `RestTemplate` to perform HTTP operations as a command-line runner.

## 📌 Features

- Sends student registration details to generate a webhook and access token.
- Automatically submits a pre-defined SQL query to the received webhook URL using the access token.
- Implements error handling and logging for failed requests.

## 🛠️ Tech Stack

- Java 17+
- Spring Boot
- Spring Web (RestTemplate)
- Maven

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher installed
- Maven installed

### Clone the Repository

```bash

