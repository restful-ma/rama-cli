# Distinct Message Ratio (DMR)
The `Distinct Message Ratio` metric (DMR) represents a measure for the complexity/cohesion of data types in an interface. This metric is significantly modified from the original version [1]. It is the ratio between distinct messages and all messages that are inside an interface. DMR takes values between `0` and `1`, where lower is considered better.
Messages are either: 
 * an `input` message: the combination of path parameters, query parameters, and the request body. The `input` data can be seen as a single data object which has the different parameters as sub-properties. Usually, each operation has exactly one `input` message.
* an `output` message: the different responses of an operation, e.g. the `200 OK` response and error schemes. Usually, an operation has exactly one valid response plus error messages as `output`.

In the example below are 3 messages from `GET /pets`
* {limit}
* {$ ref: "#/components/schemas/Pets"}
* {$ ref: "#/components/schemas/Error"}

and 3 messages from `GET /pets/{age}`
* {limit, age}
* {$ ref: "#/components/schemas/Pets"}
* {$ ref: "#/components/schemas/Error"}

To calculate DMR, we divide the count of distinct messages that occur only once in the whole API ({limit} and {limit, age}, i.e. `2`) by the number of all messages (`6`):

`DMR = 2 / 6 = 1 / 3`


The fictional OpenAPI V3 specification below describes two paths each containing one operation.
```yaml
paths:
    /pets:
        get:
            parameters:
                - name: limit
                  in: query
                  schema:
                    type: integer
            responses:
                '200':
                    description: An array of pets
                    content:
                        application/json:
                            schema:
                                $ref: "#/components/schemas/Pets"
                default:
                    description: unexpected error
                    content:
                        application/json:
                            schema:
                                $ref: "#/components/schemas/Error"

    /pets/{age}:
        get:
            parameters:
                - name: limit
                  in: query
                  schema:
                    type: integer
                - name: age
                  in: path
                  required: true
                  schema:
                    type: integer
            responses:
                '200':
                    description: An array of pets with specified age
                    content:
                        application/json:
                            schema:
                                $ref: "#/components/schemas/Pets"
                default:
                    description: unexpected error
                    content:
                        application/json:
                            schema:
                                $ref: "#/components/schemas/Error"

components:
    schemas:
        Pet:
            properties:
                age:
                    type: integer
                name:
                    type: string

        Pets:
            type: array
            items:
                $ref: "#/components/schemas/Pet"

        Error:
            properties:
                message:
                    type: string
```

## Source

[1] Baski, D., & Misra, S. (2011). Metrics suite for maintainability of extensible markup language Web Services. IET software, 5(3), 320-341.
