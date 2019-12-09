# Number of Root Resources (NOR)
The `Number Of Roots` metric represents the count of API root resources. A root resource is defined as a path with a distinct root element.

The example shown below has three paths. Two of them start with `github` and one starts with `gitlab`, i.e. there are two root resources: `NOR = 2`

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

## Source

F. Haupt, F. Leymann, K. Vukojevic-Haupt. "API governance support through the structural analysis of REST APIs." In: Computer Science - Research and Development (July 2017). doi: 10.1007/s00450-017-0384-1 (cit. on pp. 21, 31, 32, 36, 37).
