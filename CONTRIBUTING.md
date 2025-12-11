# Contributing to PixelCloak

Thank you for your interest in contributing to PixelCloak! This document provides guidelines and instructions for contributing to the project.

## Code of Conduct

We are committed to providing a welcoming and inspiring community for all.

## Reporting Bugs

### Bug Report Template
```
### Title
[Component] Clear, concise issue description

### Environment
- OS: [Windows/macOS/Linux]
- Java Version: [e.g., 17.0.1]
- Python Version: [e.g., 3.9.2]

### Steps to Reproduce
1. Start the application
2. [Continue...]

### Expected Behavior
[What should happen]

### Actual Behavior
[What actually happens]
```

## Requesting Features

### Feature Request Template
```
### Title
[Feature] Clear, concise feature description

### Problem
[Describe the current limitation]

### Proposed Solution
[Describe the proposed feature]

### Why This Matters
[Explain why this feature is important]
```

## Development Setup

### Prerequisites
- Java 17+ (JDK)
- Python 3.8+
- Git
- Maven or Gradle

### Initial Setup

```bash
git clone https://github.com/Geddy-Wendot/PixelCloak.git
cd PixelCloak
git checkout -b feature/your-feature-name
```

## Branching Rules

### Branch Naming Convention

```
<type>/<feature-name>

Types:
- feature/    : New features
- bugfix/     : Bug fixes
- docs/       : Documentation updates
- refactor/   : Code refactoring
- test/       : Adding or updating tests
- chore/      : Build scripts, dependencies

Examples:
- feature/duress-protocol
- bugfix/entropy-calculation-edge-case
- docs/architecture-diagram
```

## Commit Message Standards

### Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation only changes
- `style`: Changes that don't affect code behavior
- `refactor`: Code changes that neither fix bugs nor add features
- `test`: Adding or updating tests
- `chore`: Updates to build process, dependencies

### Scopes
- `ui`: User interface changes
- `crypto`: Cryptography engine
- `stego`: Steganography engine
- `analysis`: Python analysis engine
- `db`: Database/audit logging
- `build`: Build configuration
- `ci`: CI/CD pipeline

### Examples

✓ Good:
```
feat(stego): add support for JPEG steganography

Previously only PNG images were supported. This adds LSB embedding
to JPEG images for increased flexibility.

Closes #42
```

## Code Style

### Java
- Use Java 17+ syntax
- Follow Google Java Style Guide
- Max line length: 120 characters

### Python
- Follow PEP 8
- Use type hints
- Format code with `black`
- Max line length: 100 characters

## Testing

### Before Submitting a PR

**Java Tests:**
```bash
cd frontend
mvn test
```

**Python Tests:**
```bash
cd backend
pytest tests/ -v
```

### Code Coverage Requirements
- Minimum 70% code coverage for new code
- All public methods must have tests
- Edge cases should be tested

## Pull Request Process

### 1. Create PR Description

```markdown
## Description
Brief explanation of changes.

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Checklist
- [ ] Code follows style guide
- [ ] Self-review completed
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] No new warnings generated

## Related Issues
Closes #123

## Testing
Describe how you tested this change.
```

### 2. Ensure Checks Pass
- ✅ All tests pass
- ✅ Code coverage above threshold
- ✅ No linting errors
- ✅ CI/CD pipeline successful

### 3. Request Review
Tag relevant reviewers for code review.

### 4. Address Feedback
- Respond to all comments
- Make requested changes
- Commit again with clear messages
- Mark conversations as resolved

### 5. Merge
Repository maintainers will merge your PR when approved.

## Git Workflow

### Step-by-Step Example

**1. Start new feature:**
```bash
git checkout main
git pull origin main
git checkout -b feature/your-feature-name
```

**2. Make changes and commit:**
```bash
git add src/core/YourFile.java
git commit -m "feat(scope): implement feature"
```

**3. Push to GitHub:**
```bash
git push origin feature/your-feature-name
```

**4. Create Pull Request:**
- Go to GitHub repo
- Click "Compare & pull request"
- Fill in description using template
- Submit PR

**5. Address reviews:**
```bash
git add .
git commit -m "chore: address review feedback"
git push origin feature/your-feature-name
```

**6. After merge:**
```bash
git checkout main
git pull origin main
git branch -d feature/your-feature-name
```

## Security Policy

### Reporting Security Vulnerabilities

**DO NOT** open public issues for security vulnerabilities.

Instead, email with details:
- Vulnerability description
- Affected component
- Steps to reproduce
- Potential impact
- Proposed fix (if any)

**Allow 90 days for patch before public disclosure.**

---

**Last Updated:** December 2024  
**Version:** 1.0

Thank you for contributing to SecureVent! 
