# Snowplow engineering technical test instructions - JSON validation service

Using this specification you will need to build a REST-service for validating JSON documents against JSON Schemas.

This REST-service should allow users to upload JSON Schemas and store them at unique URI and then validate JSON documents against these URIs.

Additionally, this service will "clean" every JSON document before validation: remove keys for which the value is null.

Client-side software as well as GUI is out of scope of this specification - user can choose any tool able to communicate via HTTP like `curl` or write their own.


## API Specification

The primary interface of application is REST (JSON over HTTP).

### Endpoints

```
POST    /schema/SCHEMAID        - Upload a JSON Schema with unique `SCHEMAID`
GET     /schema/SCHEMAID        - Download a JSON Schema with unique `SCHEMAID`

POST    /validate/SCHEMAID      - Validate a JSON document against the JSON Schema identified by `SCHEMAID`
```

### Responses

**All** possible responses should be valid JSON documents.

#### Valid JSON Schema Upload

This should contain Schema id, action and status.

```json
{
    "action": "uploadSchema",
    "id": "config-schema",
    "status": "success"
}
```

#### Invalid JSON Schema Upload

It isn't necessary to check whether the uploaded JSON is a valid JSON Schema v4 (many validation libraries dont allow it),
but it is required to check whether the document is valid JSON.

```json
{
    "action": "uploadSchema",
    "id": "config-schema",
    "status": "error",
    "message": "Invalid JSON"
}
```

#### JSON document was successfully validated

```json
{
    "action": "validateDocument",
    "id": "config-schema",
    "status": "success"
}
```

#### JSON document is invalid against JSON Schema

The returned message should contain a human-readable string or machine-readable JSON document indicating the error encountered.
The exact format can be chosen based on the validator library's features.

```json
{
    "action": "validateDocument",
    "id": "config-schema",
    "status": "error",
    "message": "Property '/root/timeout' is required"
}
```

### Use case

#### Schema validation

The potential user has a configuration JSON file `config.json` like the following:

```json
{
  "source": "/home/alice/image.iso",
  "destination": "/mnt/storage",
  "timeout": null,
  "chunks": {
    "size": 1024,
    "number": null
  }
}
```

And expects it conforms to the following JSON Schema `config-schema.json`:

```json
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "type": "object",
  "properties": {
    "source": {
      "type": "string"
    },
    "destination": {
      "type": "string"
    },
    "timeout": {
      "type": "integer",
      "minimum": 0,
      "maximum": 32767
    },
    "chunks": {
      "type": "object",
      "properties": {
        "size": {
          "type": "integer"
        },
        "number": {
          "type": "integer"
        }
      },
      "required": ["size"]
    }
  },
  "required": ["source", "destination"]
}
```

To check that it really fits the schema:

1. The user should upload the JSON Schema: `curl http://localhost/schema/config-schema -X POST -d @config-schema.json`
2. The server should respond with: `{"action": "uploadSchema", "id": "config-schema", "status": "success"}` and status code 201
3. The user should upload the JSON document to validate it `curl http://localhost/validate/config-schema -X POST -d @config.json`
4. The server should "clean" the uploaded JSON document to remove keys for which the value is `null`:
```json
{
  "source": "/home/alice/image.iso",
  "destination": "/mnt/storage",
  "chunks": {
    "size": 1024
  }
}
```
5. The server should respond with: `{"action": "validateDocument", "id": "config-schema", "status": "success"}` and status code 200

## Other requirements

* Restarting the application should have no effect regarding the previously uploaded JSON Schemas.
* Exceptional cases should be handled.
* Unexpected requests (such as invalid URIs) should be processed according to the RESTful architecture.
