# Arguments per Operation (APO)
The ArgumentsPerOperation metric (APO) represents the average amount of operation arguments for a service. For its calculation, the summed up number of arguments for all service operations is divided by the total number of operations. Each path parameter counts as one argument. Moreover, each request body counts as one argument.

The service shown below has one POST operation with two arguments: one path variable (`pathVariable`), one request body.

```yaml
/pets/{pathVariable}:
    post:
        description: Creates a new pet in the store. Duplicates are allowed
        operationId: addPet
        parameters:
            - name: pathVariable
              in: path
              description: Some path variable.
              required: true
              schema:
                type: string
                default: v1
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
            default:
                description: unexpected error
                content:
                    application/json:
                        schema:
                            $ref: '#/components/schemas/Error'
```

To calculate APO, we divide the count of these arguments (2) by the number of operations (1):

`APO = 2 / 1 = 2`

## Source

D. Basci, S. Misra. "Data Complexity Metrics for XML Web Services." In: Advances in Electrical and Computer Engineering 9 (June 2009). doi: 10.4316/aece.2009.02002 (cit. on pp. 21, 34, 35).
