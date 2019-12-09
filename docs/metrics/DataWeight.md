# Data Weight (DW)
The `Data Weight` metric represents a measure for the complexity of data types in an interface. It is the count of all path parameters and all parameters in request and response bodies within all operations of an interface.

The POST operation shown below has no path parameters to take into account. It has, however a request and a response body.

```yaml
/pets:
    post:
        description: Creates a new pet in the store. Duplicates are allowed
        operationId: addPet
        requestBody:
            description: Pet to add to the store
            required: true
            content:
                application/json:
                    schema:
                        $ref: '#/components/schemas/NewPet'
        responses:
            '200':
                description: pet response
                content:
                    application/json:
                        schema:
                            $ref: '#/components/schemas/Pet'

components:
    schemas:
        Pet:
            allOf:
                - $ref: '#/components/schemas/NewPet'
                - required:
                    - id
                properties:
                    id:
                        type: integer
                        format: int64

        NewPet:
            required:
                - name
            properties:
                name:
                    type: string
                tag:
                    type: string

```

The request body consists of the `NewPet` schema that has two parameters: `name` and `tag`. The response body consists of the `Pet` schema that has three parameters: `name`, `tag`, and `id`. To calculate the DataWeight value, we simply count the number of parameters per request (`2`) and response (`3`) body and create the sum:

`DW = 2 + 3 = 5`

## Source

D. Basci, S. Misra. "Data Complexity Metrics for XML Web Services." In: Advances in Electrical and Computer Engineering 9 (June 2009). doi: 10.4316/aece.2009.02002 (cit. on pp. 21, 34, 35).
