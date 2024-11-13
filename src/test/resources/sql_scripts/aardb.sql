DROP TABLE IF EXISTS aarsregnskap;

CREATE TABLE IF NOT EXISTS aarsregnskap(
    regnaar SMALLINT NOT NULL,
    orgnr VARCHAR(9) NOT NULL,
    filePath VARCHAR(255) NOT NULL
);

INSERT INTO aarsregnskap VALUES('2013', '312800640', './src/test/resources/aarsregnskap-files/example-multipage.tiff');
INSERT INTO aarsregnskap VALUES('2014', '312800640', './src/test/resources/aarsregnskap-files/example-multipage.tiff');
INSERT INTO aarsregnskap VALUES('2022', '312800640', './src/test/resources/aarsregnskap-files/example-multipage.tiff');
INSERT INTO aarsregnskap VALUES('2023', '312800640', './src/test/resources/aarsregnskap-files/example-multipage.tiff');

INSERT INTO aarsregnskap VALUES('2022', '311032348', './src/test/resources/aarsregnskap-files/example-multipage.tiff');
INSERT INTO aarsregnskap VALUES('2023', '311032348', './src/test/resources/aarsregnskap-files/example-multipage.tiff');
