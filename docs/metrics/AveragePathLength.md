# Average Path Length (APL)
The AveragePathLength metric (APL) represents the average size of resource paths in a RESTful interface. The path length of a single resource is defined by the number of slashes ("/"). Slashes at the end of the path are not counted. All resource path lengths of the RESTful interface are summed up and divided by the total number of resource paths.

The example shown below has three resource paths, namely `/github/repositories/{username}`, `/github/repositories/{username}/{slug}`, and `/gitlab/repositories/{username}/{slug}`.

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

The first path is three elements long (`/github`, `/repositories`, and `/{username}`). The second and the third path are both four elements long. To calculate the APL value for this interface, we sum up all path lengths and divide it by the number of paths:

`APL = (3 + 4 + 4) / 3 = 11 / 3 = 3.67`

## Source

F. Haupt, F. Leymann, K. Vukojevic-Haupt. "API governance support through the structural analysis of REST APIs." In: Computer Science - Research and Development (July 2017). doi: 10.1007/s00450-017-0384-1 (cit. on pp. 21, 31, 32, 36, 37).