# Oracle Java Bot

## Introduction
This project is an Oracle Java Bot designed to automate tasks and provide visibility to managers and team members through a Telegram chat interface. The bot leverages DevOps principles to enhance productivity and streamline operations.

## Features
- Task automation for development teams
- Real-time updates and notifications
- Integration with CI/CD pipelines
- User-friendly interface via Telegram

## Table of Contents
- Installation
- Usage
- Architecture
- ER Diagram
- Design Patterns
- Testing
- CI/CD
- Documentation
- Terms & Conditions
- Lessons Learned
- Contributing
- License

## Installation
1. Clone the repository:
    ```bash
    git clone https://github.com/JacobV321/java-bot.git
    ```
2. Navigate to the project directory:
    ```bash
    cd java-bot
    ```
3. Install dependencies:
    ```bash
    mvn install
    ```
4. Configure the bot with your Telegram API key and other settings in the `config.properties` file.

## Usage
To start the bot, run the following command:
```bash
java -jar target/java-bot.jar
```

The bot will connect to Telegram and start processing commands.

## Architecture
!Architecture Diagram
The architecture consists of the following components:
- Telegram API Integration
- Task Scheduler
- Notification System
- CI/CD Pipeline

## ER Diagram
!ER Diagram
The ER diagram shows the relationships between entities, including primary keys, foreign keys, and relation types. The database is designed to be in the 2nd Normal Form.

## Design Patterns
The project implements several design patterns, including:
- Singleton for bot instance management
- Factory for creating different types of tasks
- Observer for real-time notifications

## Testing
The project includes unit tests and integration tests. To run the tests, use the following command:
```bash
mvn test
