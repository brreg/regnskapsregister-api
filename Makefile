CHECK_BRANCH := main
CURRENT_BRANCH := $(shell git rev-parse --abbrev-ref HEAD)
CURRENT_REVISION := $(shell git rev-parse HEAD)

run-psql-dev:
	podman run -d --name postgres_regnskap -e POSTGRESQL_USER=regnskap -e POSTGRESQL_PASSWORD=password -e POSTGRESQL_DATABASE=regnskap -p 5432:5432 rhel8/postgresql-12
reset-sql-dev:
	podman container rm --force postgres_regnskap

ifeq ($(CURRENT_BRANCH),$(CHECK_BRANCH))
trigger-pipeline:
	oc login -u "${USER}" --server "https://api.ocp01.ut.base.brreg.no:6443"
	oc project regnskap-registerinfo
	tkn pipeline start "regnskap-opendata-api" --showlog --param "revision=$(CURRENT_REVISION)" --use-param-defaults
endif
ifneq ($(CURRENT_BRANCH),$(CHECK_BRANCH))
trigger-pipeline:
	echo "Need to be on main branch to trigger pipeline"
endif
