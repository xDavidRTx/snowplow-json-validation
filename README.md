# How to run the service

A PostgreSql database is required to be able to run this service. There is several solutions for doing so but the easiest one is to use Docker with: 

```
docker run --name postgres-db -e POSTGRES_PASSWORD=docker -p 5432:5432 -d postgres
export POSTGRES_JSON_VALIDATOR_USER=postgres
export POSTGRES_JSON_VALIDATOR_PASSOWORD=docker
```



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
