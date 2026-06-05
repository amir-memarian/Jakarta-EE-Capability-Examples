\# Jakarta EE Capability Examples

This repository contains a collection of practical projects demonstrating various Jakarta EE capabilities. Each project focuses on a specific specification or feature of the Jakarta EE platform, serving as a hands-on reference for developers exploring enterprise Java development.


\## Project List

Below is the list of Jakarta projects included in this repository. Each project highlights a particular Jakarta EE capability.


| Project                      | Capability Demonstrated                 | Description                                                                       |
|------------------------------|-----------------------------------------|-----------------------------------------------------------------------------------|
| `jakarta-servlet-example`    | Servlet API                             | Basic servlet handling, req/res lifecycle, and annotation-based configuration.    |
| `jakarta-jsp-example`        | Jakarta Server Pages (JSP)              | Dynamic HTML rendering with JSP tags, EL expressions, and custom tags.            |
| `jakarta-jpa-hibernate`      | Jakarta Persistence (JPA)               | Object-relational mapping, entity manag, JPQL queries, and transaction handling.  |
| `jakarta-cdi-example`        | Contexts and Dependency Injection (CDI) | Dependency injection, scopes (request, session, application), and event handling. |
| `jakarta-rest-json`          | Jakarta RESTful Web Services (JAX-RS)   | Building REST APIs, JSON binding (JSON-B), and content negotiation.               |
| `jakarta-security-oidc`      | Jakarta Security                        | Authentication, authorization, and integration with OpenID Connect / JWT.         |
| `jakarta-validation-bean`    | Jakarta Bean Validation                 | Validating method parameters, return values, and entity fields with constraints.  |
| `jakarta-batch-processing`   | Jakarta Batch                           | Chunk-oriented and tasklet-based batch jobs, checkpointing, and scheduling.       |
| `jakarta-mail-smtp`          | Jakarta Mail                            | Sending emails with SMTP, attachments, and MIME messages.                         |
| `jakarta-json-processing`    | Jakarta JSON Processing (JSON-P)        | Parsing, generating, and streaming JSON data.                                     |
| `jakarta-websocket-chat`     | Jakarta WebSocket                       | Real-time bidirectional communication, chat room example.                         |
| `jakarta-concurrency-example`| Jakarta Concurrency                     | Managed executor service, thread management in enterprise environment.            |
| `jakarta-faces-primefaces`   | Jakarta Server Faces (JSF)              | Component-based UI with PrimeFaces, AJAX, and navigation rules.                   |
| `jakarta-messaging-activemq` | Jakarta Messaging (JMS)                 | Point-to-point and publish/subscribe messaging with ActiveMQ.                     |

> \*\*Note\*\*: Click on any project name in the list to view its source code and detailed instructions. (Replace the placeholder `jakarta-xxx-example` with actual folder names if different.)



\## How to Use This Repository

Each project is self-contained and can be imported into your favorite IDE (IntelliJ IDEA, Eclipse, VS Code) or built with Maven/Gradle.

\### Prerequisites

\- JDK 11 or higher (Jakarta EE 10 requires Java 11+)

\- A Jakarta EE compatible application server (e.g., Payara, WildFly, Open Liberty, GlassFish)

\- Maven 3.6+ or Gradle 7+

\### Running a Project

1\. Clone the repository:

&nbsp;  ```bash

&nbsp;  git clone https://github.com/amir-memarian/Jakarta-EE-Capability-Examples.git

&nbsp;  cd Jakarta-EE-Capability-Examples

