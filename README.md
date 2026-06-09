# Steadybit MCP Server

MCP server for Steadybit, enabling LLM tools like Claude to interact with the Steadybit platform.

## Tools

1. `list-experiment-designs`
    - List experiment designs
    - Required inputs:
        - `team` (string): The team key to list experiment designs for
    - Returns: List of experiment designs with their key and name
2. `get_experiment_design`
    - Get an experiment design
    - Required inputs:
        - `experimentKey` (string): The experiment key to get
    - Returns: The experiment design
3. `list_experiment_executions`
    - List experiment executions
    - Optional inputs:
        - `experiment` (list of string): Filter by one or more experiment keys
        - `environment` (list of string): Filter by one or more environment names
        - `team` (list of string): Filter by one or more team keys
        - `state` (list of string): Filter by one or more result states, possible values
          are [CREATED, PREPARED, RUNNING, FAILED, CANCELED, COMPLETED, ERRORED]
        - `from` (string, ISO8601 date): Filter by creation date from
        - `to` (string, ISO8601 date): Filter by creation date to
        - `page` (number): Number of the requested page, default is 0
        - `pageSize` (number): Results per page, defaults to 50, maximum 100 is allowed
    - Returns: The experiment design
4. `get_experiment_execution`
    - Get an experiment execution
    - Required inputs:
        - `executionId` (number): The execution id to get
    - Returns: The experiment execution
5. `list_actions`
    - List of currently registered actions
    - Optional inputs:
        - `page` (number): Number of the requested page, default is 0
        - `pageSize` (number): Results per page, defaults to 50, maximum 100 is allowed
    - Returns: List of actions
6. `list_environments`
    - Get a list of environments
    - Returns: List of environments
7. `list_teams`
    - Get a list of teams
    - Returns: List of teams
8. `list_experiment_schedules`
    - Get a list of experiment schedules
    - Optional inputs:
        - `experiment` (list of string): Filter by one or more experiment keys
        - `team` (list of string): Filter by one or more team keys
    - Returns: List of experiment schedules
9. `list_experiment_templates`
    - Get a list of experiment templates (name and ids)
10. `get_experiment_template`
    - Get an experiment template including its design
    - Required inputs:
        - `templateId` (string): The id of the template to create an experiment from
11. `create_experiment_from_template`
    - Create an experiment from a template
    - Needs to be enabled via environment variable, for example `CAPABILITIES_ENABLED_0=CREATE_EXPERIMENT_FROM_TEMPLATE`
    - Required inputs:
        - `templateId` (string): The id of the template to create an experiment from
        - `environment` (string): The environment to use for the experiment
        - `team` (string): The team to use for the experiment
    - Optional inputs:
        - `placeholders` (object): A map of placeholder keys and their values.
        - `externalId` (string): An optional external id that can be used to update existing experiment designs.
    - Returns: The key of the created experiment or an error message if the experiment could not be created

## Setup

You need a Steadybit account and an API token. You can create an API token in the Steadybit platform under
**Settings → API Access Tokens**. Both token types — `Admin` or `Team` — are supported.

If you want to create experiments, you need a team token for the team you want to create experiments in.

### Supported environment variables

- `API_TOKEN`: The API token to use for authentication. **Required.**
- `API_URL`: The URL of the Steadybit API. Defaults to `https://platform.steadybit.com/api`.
- `CAPABILITIES_ENABLED_0`, `CAPABILITIES_ENABLED_1`, ...: Additional capabilities to enable. Currently supported:
    - `CREATE_EXPERIMENT_FROM_TEMPLATE`: Enables the `create_experiment_from_template` tool.

### Usage with [Claude Desktop](https://claude.ai/download)

In Claude Desktop go to **Settings → Developer → Edit** and add the following JSON, replacing `<your-api-token>`
with your actual token:

```json
{
  "mcpServers": {
    "steadybit": {
      "command": "docker",
      "args": [
        "run",
        "-i",
        "--rm",
        "-e",
        "API_TOKEN",
        "ghcr.io/steadybit/mcp:latest"
      ],
      "env": {
        "API_TOKEN": "<your-api-token>"
      }
    }
  }
}
```

## Local Development

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker (only required for the Docker image flow)
- Node.js / `npx` (used to run the MCP inspector)

> The MCP server uses STDIO transport, so there is no console logging. Server output is written to `steadybit-mcp.log`
> in the directory where you start the server.

### Build

```bash
mvn clean install
```

The resulting jar is at `target/mcp-<version>-SNAPSHOT.jar`.

### Run locally with the MCP inspector

```bash
npx @modelcontextprotocol/inspector \
  java -jar target/mcp-1.0.0-SNAPSHOT.jar \
  -e API_URL=https://platform.steadybit.com/api \
  -e API_TOKEN=<your-api-token>
```

Logs are written to `steadybit-mcp.log` in the directory where you launched the inspector.

### Run in Claude Desktop against your local jar

Edit Claude Desktop's developer config (**Settings → Developer → Edit**) to point at your local Java binary and the
freshly-built jar:

```json
{
  "mcpServers": {
    "steadybit": {
      "command": "/path/to/your/java",
      "args": [
        "-jar",
        "/path/to/your/.m2/repository/com/steadybit/mcp/1.0.0-SNAPSHOT/mcp-1.0.0-SNAPSHOT.jar"
      ],
      "env": {
        "API_URL": "https://platform.steadybit.com/api",
        "API_TOKEN": "<your-api-token>",
        "LOGGING_FILE_NAME": "/path/to/Library/Logs/Claude/steadybit-mcp-server.log"
      }
    }
  }
}
```

- MCP-client logs: `~/Library/Logs/Claude/mcp-server-steadybit.log`
- MCP-server logs: the path configured via `LOGGING_FILE_NAME` (defaults to `steadybit-mcp.log` next to the launcher)

### Run tests

```bash
mvn test
```

### Build the Docker image

```bash
docker build -t steadybit/mcp -f Dockerfile .
```

Then create a `config.json` and run the inspector against the image:

```json
{
  "mcpServers": {
    "steadybit": {
      "command": "docker",
      "args": [
        "run",
        "-i",
        "--rm",
        "-e",
        "API_TOKEN",
        "-e",
        "API_URL",
        "steadybit/mcp"
      ],
      "env": {
        "API_TOKEN": "<your-api-token>",
        "API_URL": "https://platform.steadybit.com/api"
      }
    }
  }
}
```

```bash
npx @modelcontextprotocol/inspector --config config.json --server steadybit
```

### Build a native image

Install GraalVM 24.0.1 via sdkman:

```bash
sdk install java 24.0.1-graalce
sdk use java 24.0.1-graalce
```

Then build with the `native` profile:

```bash
mvn -Pnative native:compile
```

## Example Usage

Example prompts are in [`examples/examples.md`](examples/examples.md).

## License

MIT — see [LICENSE](LICENSE) for details.
