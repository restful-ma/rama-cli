# Longest Path (LP)

## Overview

Similar to the AveragePathLength (APL), the LongestPath metric focuses on the size of the resource paths of an API. Instead of the average length, it represents the maximum length, i.e. the longest resource path. Again, the path length is defined by the number of slashes ("/"). Slashes at the end of the path are not counted.

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

The first path is three elements long (`/github`, `/repositories`, and `/{username}`). The second and the third path are both four elements long. To calculate the LP value for this interface, we identify the maximum of these path lengths:

`LP = MAX(3, 4, 4) = 4`

## Source

F. Haupt, F. Leymann, K. Vukojevic-Haupt. "API governance support through the structural analysis of REST APIs." In: Computer Science - Research and Development (July 2017). doi: 10.1007/s00450-017-0384-1 (cit. on pp. 21, 31, 32, 36, 37).
