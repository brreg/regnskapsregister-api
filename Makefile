run-psql-dev:
	podman run -d --name postgres_regnskap -e POSTGRESQL_USER=regnskap -e POSTGRESQL_PASSWORD=regnskap -e POSTGRESQL_DATABASE=rregapidb -p 5432:5432 rhel8/postgresql-12
reset-sql-dev:
	podman container rm --force postgres_regnskap
