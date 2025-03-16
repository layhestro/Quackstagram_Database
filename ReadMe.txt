# Quackstagram

A simple Java-based photo sharing application inspired by Instagram. Built with Java Swing for UI and file-based storage.

## Prerequisites

- Java Development Kit (JDK) 8 or higher

## Building the Application

1. Compile the Java files from the project root directory:

```bash
mkdir -p bin
javac -d bin src/com/quackstagram/*.java src/com/quackstagram/*/*.java src/com/quackstagram/*/*/*.java
```
Or simply launch the main method with VSC as the project does not use Maven or any external lib.

## Running the Application

1. Run the compiled application:

```bash
java -cp bin com.quackstagram.QuackstagramApp
```

## Project Structure

```
com.quackstagram
├── controller      # Application controllers
├── dao             # Data access objects 
│   ├── impl        # File-based implementations
│   └── interfaces  # DAO interfaces
├── model           # Data models
├── util            # Utility classes
└── view            # UI views
```

## Basic Usage

1. Register a new account or login with existing credentials
2. Browse images in the home and explore feeds
3. Upload images with captions and apply filters
4. Like posts and follow other users
5. View your profile and notifications

## Data Storage

All data is stored in text files in the following directories:
- `data/` - User credentials, following relationships, and notifications
- `img/` - Uploaded images and their metadata