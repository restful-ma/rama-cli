# BiggestRootCoverage (BRC)
The BiggestRootCoverage metric (BRC) represents the percentage of operations that are located under the largest root path element, i.e. under the root path with most operations. The value range of BRC is between `0` and `1`. To calculate it, the number of operations from the root resource with most operations is divided by the API's total number of operations. If an API only has one root path, the value of BRC is therefore `1`, because this root path will contain all operations.

The example shown below has three paths. Two of them start with `github` and one starts with `gitlab`, i.e. there are two root paths. Two operations belong to the root path `github`, one belongs two the root path `gitlab`.

```yaml
/github/repositories/{username}:
    get:
        operationId: getRepository
        parameters:
            - name: username
              in: path
              required: true
              schema:
                type: string

/github/repositories/{username}/{slug}:
    get:
        operationId: getRepository
        parameters:
            - name: username
              in: path
              required: true
              schema:
                type: string
            - name: slug
              in: path
              required: true
              schema:
                type: string

/gitlab/repositories/{username}/{slug}:
    get:
        operationId: getRepository
        parameters:
            - name: username
              in: path
              required: true
              schema:
                type: string
            - name: slug
              in: path
              required: true
              schema:
                type: string
```

The largest root path starts with `/github` and it contains two operations. Therefore, we calculate BRC by dividing these two operations by the total number of operations in the API (`3`):

`BRC = 2 / 3 = 0.67`

## Source

F. Haupt, F. Leymann, K. Vukojevic-Haupt. "API governance support through the structural analysis of REST APIs." In: Computer Science - Research and Development (July 2017). doi: 10.1007/s00450-017-0384-1 (cit. on pp. 21, 31, 32, 36, 37).
