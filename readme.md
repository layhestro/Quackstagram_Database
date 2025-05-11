# Quackstagram

A Java-based photo sharing application inspired by Instagram. Built with Java Swing for UI and supports both file-based and MariaDB database storage.

## System Requirements

- Ubuntu 24.04 (tested and recommended) or other Linux distribution
- Java Development Kit (JDK) 8 or higher
- MariaDB 10.x or MySQL 8.x

## Database Setup

1. Install MariaDB if not already installed:

```bash
sudo apt update
sudo apt install mariadb-server
```

2. Start MariaDB service:

```bash
sudo systemctl start mariadb
sudo systemctl enable mariadb
```

3. Execute the SQL scripts in order:

```bash
# If using command line
mysql -u root -p < schema.sql
mysql -u root -p quackstagram < triggers.sql
mysql -u root -p quackstagram < view.sql

# Or use DBeaver or any other database client
# 1. Connect to your MariaDB server
# 2. Run schema.sql first
# 3. Then run triggers.sql and view.sql
```

## Application Setup

### 1. Directory Structure

Ensure these directories exist:

```bash
mkdir -p bin data img/uploaded img/storage/profile img/logos config lib
```

### 2. Database Configuration

Create `config/database.properties` with:

```properties
jdbc.url=jdbc:mysql://localhost:3306/quackstagram?useSSL=false&serverTimezone=UTC
jdbc.username=user
jdbc.password=pass
```

### 3. JDBC Driver

Download and add the MySQL/MariaDB JDBC driver:

```bash
# Option 1: Download directly
wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar -O lib/mysql-connector-j.jar

# Option 2: Install system package
sudo apt install libmariadb-java
# Then link or copy from /usr/share/java/mariadb-java-client.jar to lib/
```

## Building and Running

### Using Terminal

1. Compile the application:

```bash
# Compile with JDBC driver
javac -d bin -cp "lib/mysql-connector-j.jar" com/quackstagram/*.java com/quackstagram/*/*.java com/quackstagram/*/*/*.java
```

2. Run the compiled application:

```bash
# Run with JDBC driver
java -cp bin:lib/mysql-connector-j.jar com.quackstagram.QuackstagramApp
```

### Using Visual Studio Code

1. Add the JDBC JAR to Referenced Libraries:
   - Go to Java Projects in the Explorer sidebar
   - Find "Referenced Libraries"
   - Click "+" and add your JDBC JAR file

2. Run directly from VS Code:
   - Open QuackstagramApp.java
   - Click the Run button above the main method

3. Or create a launch configuration in `.vscode/launch.json`:
```json
{
  "configurations": [
    {
      "type": "java",
      "name": "Run Quackstagram",
      "request": "launch",
      "mainClass": "com.quackstagram.QuackstagramApp",
      "classpath": ["${workspaceFolder}/bin", "${workspaceFolder}/lib/mysql-connector-j.jar"]
    }
  ]
}
```

## Project Structure

```
com.quackstagram
├── controller      # Application controllers
├── dao             # Data access objects 
│   ├── impl        # Implementations (file-based and database)
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

Quackstagram supports two storage options:
- **Database storage** (MariaDB): Primary storage method for production use
- **File-based storage**: Files in `data/` and `img/` directories (fallback option)

## Test Accounts

You can log in with these pre-configured accounts:
- Username: Xylo, Password: password123
- Username: Lorin, Password: password123
- Username: Zara, Password: password123
- Username: Mystar, Password: password123

## Troubleshooting

- **Database Connection Issues**: Check that MariaDB is running (`sudo systemctl status mariadb`) and your database credentials are correct
- **Compilation Errors**: Ensure your JDK is properly installed and the MySQL connector is in your classpath
- **Runtime Errors**: Check file permissions for data directories
