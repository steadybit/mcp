# Steadybit MCP Server

MCP Server for Steadybit, enabling LLM tools like Claude to interact with the Steadybit platform.

## Tools

1. `list-experiment-designs`
    - List experiment designs
    - Required inputs:
        - `team` (string): The team key to list experiment designs for
    - Returns: List of experiment designs with their key and name
2. `get-experiment-design-summary`
    - Get a summary of an experiment design
    - Required inputs:
        - `experimentKey` (string): The experiment key to get
    - Returns: A summary of an experiment design

## Development

Please note that there will be no logging to the console when running the MCP Server. The server uses STDIO transport
to communicate with the MCP Clients. Have a look at the `steadybit-mcp.log` file to see the output of the server.

### Local Testing

- Build the project:
    ```bash
    mvn clean install
    ```

- Test with the MCP inspector:
    ```bash
    npx @modelcontextprotocol/inspector java -jar target/mcp-1.0.0-SNAPSHOT.jara -e API_URL=https://platform.steadybit.com/api -e API_TOKEN=123456
    ```

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

### Building a native image

- Install GraalVM 24.0.1 with the following command using sdkman:
    ```bash
    sdk install java 24.0.1-graalce
    ```

- Build the native image:
    ```bash
    mvn -Pnative native:compile
    ```

## License

This MCP server is licensed under the MIT License. This means you are free to use, modify, and distribute the software,
subject to the terms and conditions of the MIT License. For more details, please see the LICENSE file in the project
repository.