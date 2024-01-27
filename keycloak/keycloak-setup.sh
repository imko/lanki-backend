docker exec -it lanki-keycloak bash

cd /opt/keycloak/bin

./kcadm.sh config credentials \
  --server http://localhost:8080 \
  --realm master \
  --user user \
  --password password

./kcadm.sh create realms -s realm=Lanki -s enabled=true

./kcadm.sh create roles -r Lanki -s name=basic
./kcadm.sh create roles -r Lanki -s name=premium

./kcadm.sh create users -r Lanki \
  -s username=isabelle \
  -s firstName=Isabelle \
  -s lastName=Dahl \
  -s enabled=true
./kcadm.sh add-roles -r Lanki \
  --uusername isabelle \
  --rolename basic

./kcadm.sh create users -r Lanki \
  -s username=bjorn \
  -s firstName=Bjorn \
  -s lastName=Vinterberg \
  -s enabled=true
./kcadm.sh add-roles -r Lanki \
  --uusername bjorn \
  --rolename premium

./kcadm.sh set-password -r Lanki \
  --username isabelle --new-password password
./kcadm.sh set-password -r Lanki \
  --username bjorn --new-password password

./kcadm.sh create clients -r Lanki \
  -s clientId=edge-service \
  -s enabled=true \
  -s publicClient=false \
  -s secret=lanki-keycloak-secret \
  -s 'redirectUris=["http://localhost:9000", "http://localhost:9000/login/oauth2/code/*"]'
