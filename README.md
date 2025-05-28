# Steadybit MCP Server

MCP Server for Steadybit, enabling LLM tools like Claude to interact with the Steadybit platform.

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

## Setup

You need to have a Steadybit account and an API token. You can create an API token in the Steadybit platform under
"Settings" → "API Access Tokens". Both token types, `Admin` or `Team` are supported.

### Supported ENV-Variables

- `API_TOKEN`: The API token to use for authentication. This is required.
- `API_URL`: The URL of the Steadybit API. Default is `https://platform.steadybit.com/api`.

### Usage with [Claude Desktop](https://claude.ai/download)

- Settings -> Developer -> Edit
- Add the following JSON to the file, make sure to replace `<your-api-token>` with your actual API token.:
  ```
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
          "ghcr.io/steadybit/mcp:latest",
        ],
        "env": {
          "API_TOKEN": "<your-api-token>",
        }
      }
    }
  }
  ```

## Development

Please note that there will be no logging to the console when running the MCP Server. The server uses STDIO transport
to communicate with the MCP Clients. Have a look at the `steadybit-mcp.log` file to see the output of the server.

### Local Testing

- Build the project:
    ```bash
    mvn clean install
    ```

- Test with the MCP inspector:
    - Launch the inspector:
      ```bash
      npx @modelcontextprotocol/inspector java -jar target/mcp-1.0.0-SNAPSHOT.jara -e API_URL=https://platform.steadybit.com/api -e API_TOKEN=123456
      ```
    - Logs can be found in `steadybit-mcp.log` located in the folder where you started the inspector.

- Use in [Claude Desktop](https://claude.ai/download)
    - Settings -> Developer -> Edit
    - Add something like below.
    ```json
    {
      "mcpServers": {
        "steadybit": {
          "command": "/Users/danielreuter/.sdkman/candidates/java/current/bin/java",
          "args": [
            "-jar",
            "/Users/danielreuter/.m2/repository/com/steadybit/mcp/1.0.0-SNAPSHOT/mcp-1.0.0-SNAPSHOT.jar"
          ],
          "env": {
            "API_URL": "https://platform.steadybit.com/api",
            "API_TOKEN": "123456",  
            "LOGGING_FILE_NAME": "/Users/danielreuter/Library/Logs/Claude/steadybit-mcp-server.log"
          }
        }
      }
    }
    ```
    - MCP-Client-Logs can be found in `~/Library/Logs/Claude/mcp-server-steadybit.log`
    - MCP-Server-Logs can be found in `~/Library/Logs/Claude/steadybit-mcp.log`, depending on the `LOGGING_FILE_NAME`
      you set in the `env` section.

### Building and testing the Docker image

- Build the image:
  ```bash
  docker build -t steadybit/mcp -f Dockerfile . 
  ```

- Create a file `config.json` with the following content:
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
          "API_TOKEN": "123456",
          "API_URL":"https://platform.steadybit.com/api"
        }
      }
    }
  }
  ```

- Run the inspector:
  ```bash
  npx @modelcontextprotocol/inspector --config config.json --server steadybit
  ```

### Building a native image

- Install GraalVM 24.0.1 with the following command using sdkman:
    ```bash
    sdk install java 24.0.1-graalce
    ```

- Use the GraalVM version:
    ```bash
    sdk use java 24.0.1-graalce
    ```

- Build the native image:
    ```bash
    mvn -Pnative native:compile
    ```

## License

This MCP server is licensed under the MIT License. This means you are free to use, modify, and distribute the software,
subject to the terms and conditions of the MIT License. For more details, please see the LICENSE file in the project
repository.