CURRENT_REVISION := $(shell git rev-parse HEAD)

run-psql-dev:
	podman run -d --name postgres_regnskap -e POSTGRESQL_USER=regnskap -e POSTGRESQL_PASSWORD=password -e POSTGRESQL_DATABASE=regnskap -p 5432:5432 rhel8/postgresql-12
reset-sql-dev:
	podman container rm --force postgres_regnskap

trigger-pipeline:
	@echo "Enter your Openshift username:"
	@read -p "Username: " username; \
	oc login -u $$username --server "https://api.ocp01.ut.base.brreg.no:6443"
	oc project regnskap-registerinfo
	tkn pipeline start "regnskap-opendata-api" --showlog --param "revision=$(CURRENT_REVISION)" --param "force_image_build=true" --use-param-defaults
	@echo "${CURRENT_REVISION}" built successfully. Use this hash to deploy the new image from the appconfig.
	@echo "Remember to update the hash for both deployments:"
	@echo "- regnskap-opendata-api"
	@echo "- regnskap-opendata-updater"
