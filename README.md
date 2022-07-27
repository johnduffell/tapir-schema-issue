# tapir-schema-issue

How to reproduce
1. `git clone git@github.com:johnduffell/tapir-schema-issue.git`
1. `cd tapir-schema-issue`
1. `sbt run`
1. choose either "actual" or "ideal"

Output from actual includes:
```
  schemas:
    F1:
      type: object
    F2:
      type: object
    W1:
      type: object
    W2:
      type: object
    WithFields:
      oneOf:
      - $ref: '#/components/schemas/F1'
      - $ref: '#/components/schemas/F2'
    WithoutFields:
      oneOf:
      - $ref: '#/components/schemas/W1'
      - $ref: '#/components/schemas/W2'
    Wrapper:
      required:
      - withFields
      - withoutFields
      type: object
      properties:
        withFields:
          $ref: '#/components/schemas/WithFields'
        withoutFields:
          $ref: '#/components/schemas/WithoutFields'
```

output from ideal includes
```
  schemas:
    Wrapper:
      required:
      - withFields
      - withoutFields
      type: object
      properties:
        withFields:
          required:
          - discrimField
          - anotherField
          type: object
          properties:
            discrimField:
              type: string
            anotherField:
              type: string
        withoutFields:
          type: string
          enum:
          - W1
          - W2
```

As you can see, rather than a canonical representation which is 4 empty objects with different discriminators, it allows us to either use an existing field, or the name of the enum case as a discriminator.
