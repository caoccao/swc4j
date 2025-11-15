# swc4j Features

This directory contains detailed documentation for specific features and capabilities of swc4j.

## Core Features

### AST (Abstract Syntax Tree)

- **[ast.md](ast.md)** - Overview of AST structure and navigation
- **[ast_visitor.md](ast_visitor.md)** - Guide to implementing custom AST visitors for code transformation
- **[plugin.md](plugin.md)** - Creating and using plugins to extend swc4j functionality

### Library Loading

- **[custom_library_loading.md](custom_library_loading.md)** - Customize native library deployment and loading behavior
  - Deploy to custom locations
  - Skip deployment for system libraries
  - Suppress classloader errors
  - Configure via system properties

## Security Features

swc4j provides comprehensive code sanitization and security features to protect JavaScript/TypeScript execution environments.

### Identifier Protection

- **[identifier_restriction.md](identifier_restriction.md)** - Restrict access to specific identifiers
- **[identifier_freeze.md](identifier_freeze.md)** - Freeze identifiers to prevent modification
- **[identifier_deletion.md](identifier_deletion.md)** - Delete dangerous identifiers from code
- **[identifier_naming_convention.md](identifier_naming_convention.md)** - Enforce naming conventions for identifiers

### Access Control

- **[built_in_object_protection.md](built_in_object_protection.md)** - Protect built-in JavaScript objects
- **[function_restriction.md](function_restriction.md)** - Restrict usage of specific functions
- **[keyword_restriction.md](keyword_restriction.md)** - Control which JavaScript keywords can be used

## Feature Categories

### 🔍 Code Analysis

- AST structure inspection
- Token extraction and analysis
- Custom visitor patterns

### 🔧 Code Transformation

- AST-based code modification
- Custom transformation plugins
- Visitor-based rewrites

### 🔒 Security & Sanitization

- Identifier restrictions and freezing
- Built-in object protection
- Function and keyword restrictions
- Safe code execution environments

### ⚙️ Configuration & Deployment

- Custom library loading
- Flexible deployment options
- System property configuration

## Quick Navigation

### For Code Analysis

Start with [ast.md](ast.md) to understand the structure, then explore [ast_visitor.md](ast_visitor.md) for implementing custom analysis.

### For Code Transformation

Review [ast_visitor.md](ast_visitor.md) for visitor patterns and [plugin.md](plugin.md) for creating reusable transformations.

### For Security Hardening

Begin with the sanitizer overview in [../sanitizer.md](../sanitizer.md), then dive into specific protection mechanisms:

- Protect built-in objects: [built_in_object_protection.md](built_in_object_protection.md)
- Restrict identifiers: [identifier_restriction.md](identifier_restriction.md)
- Freeze critical identifiers: [identifier_freeze.md](identifier_freeze.md)

### For Custom Deployments

See [custom_library_loading.md](custom_library_loading.md) for all native library loading options including:

- Docker/Kubernetes deployments
- Custom library paths
- System-wide installations

## Related Documentation

- [Parse](../parse.md) - Parsing JavaScript/TypeScript code
- [Transform](../transform.md) - Code transformation and minification
- [Transpile](../transpile.md) - TypeScript to JavaScript transpilation
- [Sanitizer](../sanitizer.md) - Code sanitization overview
- [Tutorials](../tutorials/) - Step-by-step guides

## Additional Resources

### AST Visualization

The `ast.dot` and `ast.svg` files provide visual representations of the AST structure for reference and documentation purposes.

### Examples

Most feature documents include code examples demonstrating:

- Basic usage
- Common patterns
- Advanced configurations
- Real-world scenarios

## Contributing

When adding new features:

1. Create a descriptive markdown file in this directory
2. Include clear examples and use cases
3. Add appropriate links to related features
4. Update this README with the new feature
5. Follow the existing documentation style
