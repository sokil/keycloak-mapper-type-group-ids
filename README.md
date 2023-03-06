# Keycloak Group Id Mapper Type

Allows to add user group internal ids as JWT claim

## Using in Docker

Build mapper and place it to /opt/keycloak/providers/keycloak-groupIdsProtocolMapper.jar

Example of building inside GitLab CI:

```
RUN \
  curl \
    -v --fail -o /opt/keycloak/providers/keycloak-groupIdsProtocolMapper.jar \
    --header ${ARTIFACT_AUTH_HEADER} \
    https://gitlab.example.io/api/v4/projects/${CI_PROJECT_ID}/jobs/artifacts/${BUILD_VERSION}/raw/target/keycloak-groupIdsProtocolMapper-${BUILD_VERSION}.jar?job=build
```
