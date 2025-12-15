# DBFlute Maven Plugin

[![Maven CI](https://github.com/dbflute/dbflute-maven-plugin/workflows/Maven%20CI/badge.svg)](https://github.com/dbflute/dbflute-maven-plugin/actions)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A Maven plugin that provides seamless integration with [DBFlute](http://dbflute.org/), a powerful database-centric code generation tool for Java applications.

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Plugin Goals](#plugin-goals)
- [Configuration](#configuration)
- [Usage Examples](#usage-examples)
- [Building from Source](#building-from-source)
- [Contributing](#contributing)
- [License](#license)

## Overview

The DBFlute Maven Plugin allows you to:

- **Download** DBFlute engine automatically
- **Create** and configure DBFlute client directories
- **Execute** DBFlute commands directly from Maven
- **Generate** database access code and documentation
- **Manage** database schema evolution

This plugin eliminates the need for manual DBFlute setup and provides a streamlined Maven-based workflow for database-centric development.

## Requirements

- **Java**: JDK 8 or higher
- **Maven**: 3.8.1 or higher
- **DBFlute**: Compatible with DBFlute 1.0.0 and higher

## Installation

Add the plugin to your project's `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.dbflute</groupId>
            <artifactId>dbflute-maven-plugin</artifactId>
            <version>1.2.0-SNAPSHOT</version>
            <configuration>
                <!-- Plugin configuration here -->
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Quick Start

### 1. Download DBFlute Engine

```bash
mvn dbflute:download
```

This downloads the latest DBFlute engine to the `mydbflute` directory.

### 2. Create DBFlute Client

```bash
mvn dbflute:create-client \
  -Ddbflute.clientProject=maihamadb \
  -Ddbflute.packageBase=com.example.dbflute \
  -Ddbflute.database=mysql \
  -Ddbflute.databaseDriver=com.mysql.jdbc.Driver \
  -Ddbflute.databaseUrl=jdbc:mysql://localhost:3306/exampledb \
  -Ddbflute.databaseUser=root \
  -Ddbflute.databasePassword=secret
```

### 3. Generate Code

```bash
mvn dbflute:generate
```

This generates database access code based on your database schema.

## Plugin Goals

The plugin provides the following goals:

### Setup Goals

| Goal | Description |
|------|-------------|
| `download` | Downloads the DBFlute engine ZIP file and extracts it |
| `create-client` | Creates a new DBFlute client directory with initial configuration |
| `upgrade` | Upgrades an existing DBFlute installation to a newer version |

### Code Generation Goals

| Goal | Description |
|------|-------------|
| `generate` | Generates database access code (entities, behaviors, etc.) |
| `sql2entity` | Generates entity classes from SQL files |
| `freegen` | Executes FreeGen templates for custom code generation |
| `doc` | Generates database documentation |

### Database Management Goals

| Goal | Description |
|------|-------------|
| `manage` | Opens the DBFlute management console |
| `replace-schema` | Replaces the database schema with DDL definitions |
| `load-data-reverse` | Loads data from database to TSV files |
| `schema-sync-check` | Checks if the database schema matches the DBFlute client |
| `alter-check` | Checks DDL alter statements |

### Maintenance Goals

| Goal | Description |
|------|-------------|
| `regenerate` | Regenerates all code from scratch |
| `renewal` | Renews the DBFlute client configuration |
| `save-previous` | Saves the current schema as a previous version |
| `refresh` | Refreshes the DBFlute environment |
| `intro` | Downloads and sets up DBFlute Intro (GUI tool) |

### Development Goals

| Goal | Description |
|------|-------------|
| `jdbc` | Tests JDBC connection to the database |
| `outside-sql-test` | Executes OutsideSql tests |
| `take-assert` | Takes assertions from database |

## Configuration

### Common Configuration Parameters

```xml
<configuration>
    <!-- DBFlute Version -->
    <dbfluteVersion>1.2.7</dbfluteVersion>

    <!-- Client Configuration -->
    <clientProject>maihamadb</clientProject>
    <packageBase>com.example.dbflute</packageBase>

    <!-- Database Connection -->
    <database>mysql</database>
    <databaseDriver>com.mysql.jdbc.Driver</databaseDriver>
    <databaseUrl>jdbc:mysql://localhost:3306/exampledb</databaseUrl>
    <databaseSchema>exampledb</databaseSchema>
    <databaseUser>root</databaseUser>
    <databasePassword>secret</databasePassword>

    <!-- Advanced Options -->
    <targetLanguage>java</targetLanguage>
    <targetContainer>spring</targetContainer>
    <mydbfluteDir>${basedir}/mydbflute</mydbfluteDir>
</configuration>
```

### Supported Databases

- MySQL / MariaDB
- PostgreSQL
- Oracle
- SQL Server
- H2
- Derby
- And more...

### Configuration Properties

All configuration parameters can be overridden via Maven properties:

```bash
mvn dbflute:generate -Ddbflute.packageBase=com.myapp.db
```

## Usage Examples

### Example 1: Complete Setup for MySQL

```xml
<plugin>
    <groupId>org.dbflute</groupId>
    <artifactId>dbflute-maven-plugin</artifactId>
    <version>1.2.0-SNAPSHOT</version>
    <configuration>
        <clientProject>maihamadb</clientProject>
        <packageBase>com.example.dbflute</packageBase>
        <database>mysql</database>
        <databaseDriver>com.mysql.jdbc.Driver</databaseDriver>
        <databaseUrl>jdbc:mysql://localhost:3306/maihamadb</databaseUrl>
        <databaseUser>maihamadb</databaseUser>
        <databasePassword>maihamadb</databasePassword>
        <targetContainer>spring</targetContainer>
    </configuration>
</plugin>
```

Then run:

```bash
mvn dbflute:download
mvn dbflute:create-client
mvn dbflute:jdbc  # Test connection
mvn dbflute:generate
```

### Example 2: PostgreSQL with Custom Settings

```xml
<configuration>
    <clientProject>exampledb</clientProject>
    <packageBase>org.mycompany.dbflute</packageBase>
    <database>postgresql</database>
    <databaseDriver>org.postgresql.Driver</databaseDriver>
    <databaseUrl>jdbc:postgresql://localhost:5432/exampledb</databaseUrl>
    <databaseSchema>public</databaseSchema>
    <databaseUser>postgres</databaseUser>
    <databasePassword>${db.password}</databasePassword>
</configuration>
```

### Example 3: Download Specific DBFlute Version

```bash
mvn dbflute:download -Ddbflute.version=1.2.7
```

### Example 4: Custom Download URL

```bash
mvn dbflute:download \
  -Ddbflute.downloadUrl=http://custom-repo.example.com/dbflute-1.2.7.zip
```

## Building from Source

### Prerequisites

- JDK 8 or higher
- Maven 3.8.1 or higher

### Build Steps

```bash
# Clone the repository
git clone https://github.com/dbflute/dbflute-maven-plugin.git
cd dbflute-maven-plugin

# Build the project
mvn clean install -Dgpg.skip=true

# Run tests
mvn test

# Generate JavaDoc
mvn javadoc:javadoc
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=DfPublicPropertiesTest

# Run tests with coverage
mvn clean verify
```

## Project Structure

```
dbflute-maven-plugin/
├── src/
│   ├── main/
│   │   └── java/org/dbflute/maven/plugin/
│   │       ├── *Plugin.java              # Plugin goal implementations
│   │       ├── client/                   # Client creation logic
│   │       ├── command/                  # Command execution
│   │       ├── download/                 # Download handlers
│   │       ├── officialcopy/             # Public properties handling
│   │       └── util/                     # Utility classes
│   └── test/
│       └── java/org/dbflute/maven/plugin/
│           └── **/*Test.java             # Unit tests
├── .github/
│   └── workflows/
│       └── maven-ci.yml                  # GitHub Actions CI
├── pom.xml                               # Maven configuration
└── README.md                             # This file
```

## Contributing

We welcome contributions! Here's how you can help:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

Please ensure:
- Code follows existing style conventions
- All tests pass (`mvn test`)
- New features include appropriate tests
- Commits have clear, descriptive messages

### Development Guidelines

- Use Java 8 compatible syntax
- Follow Maven plugin development best practices
- Add JavaDoc for public APIs
- Write unit tests for new functionality
- Update README for user-facing changes

## Troubleshooting

### Common Issues

**Issue**: "Not found the DBFlute version"
- **Solution**: Ensure you've run `mvn dbflute:download` first

**Issue**: "Could not connect to database"
- **Solution**: Check your database connection parameters and ensure the database is running

**Issue**: "Client directory already exists"
- **Solution**: Remove the existing client directory or use a different `clientProject` name

**Issue**: Build fails with GPG errors
- **Solution**: Skip GPG signing with `-Dgpg.skip=true`

## Community and Support

- **Website**: [http://dbflute.org/](http://dbflute.org/)
- **Issues**: [GitHub Issues](https://github.com/dbflute/dbflute-maven-plugin/issues)
- **Mailing List**: [DBFlute Users Group](https://groups.google.com/forum/#!forum/dbflute)
- **Twitter**: [@dbflute](https://twitter.com/dbflute) (Japanese)

## Related Projects

- [DBFlute](https://github.com/dbflute/dbflute-core) - The core DBFlute engine
- [DBFlute Intro](https://github.com/dbflute/dbflute-intro) - GUI tool for DBFlute
- [DBFlute Example](https://github.com/dbflute-example) - Example projects

## Versioning

This project follows [Semantic Versioning](https://semver.org/):

- **MAJOR** version for incompatible API changes
- **MINOR** version for new functionality in a backwards compatible manner
- **PATCH** version for backwards compatible bug fixes

## License

Copyright 2014-2025 The DBFlute Project and the Others.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Acknowledgments

- The DBFlute Project Team
- All contributors who have helped improve this plugin
- The Apache Maven community for the excellent plugin framework

---

**Made with ❤️ by The DBFlute Project**
