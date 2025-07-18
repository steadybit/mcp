# Changelog

## v0.0.8

- Updated dependencies to latest versions, fixing some CVE issues

## v0.0.7

- Updated dependencies to latest versions, fixing some CVE issues
- Removed `get_api_spec` tool, content gets truncated, and it's not really MCP agnostic / helpful.
- Added `list_environments` tool to retrieve environments
- Added `list_teams` tool to retrieve teams
- Added `list_experiment_schedules` tool to retrieve experiment schedules
- Added `list_experiment_templates` and `get_experiment_template` tool to interact with templates.
- Added `create_experiment_from_template` tool to create experiments from templates. You explicitly need to enable this
  capability via an environment variable `CAPABILITIES_ENABLED_0=CREATE_EXPERIMENT`.

## v0.0.6

- Added `get_api_spec` tool to retrieve the API specification of the Steadybit platform

## v0.0.5

- Added paging to `list_actions` tool

## v0.0.4

- Add `list_actions` tool to list all registered actions

## v0.0.3

- Initial release
